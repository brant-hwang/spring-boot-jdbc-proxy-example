package io.brant.example.jdbc.db.aop;


import io.brant.example.jdbc.config.DataSourceConfiguration;
import io.brant.example.jdbc.db.monitor.sql.SqlExecutionInfo;
import io.brant.example.jdbc.db.monitor.sql.SqlTaskPool;
import io.brant.example.jdbc.db.utils.HibernateQueryNormalizer;
import io.brant.example.jdbc.db.utils.QueryNormalizer;
import io.brant.example.jdbc.db.utils.SqlFormatter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CommunicationException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatementMethodInterceptor implements MethodInterceptor {
	private final Logger logger = LoggerFactory.getLogger(StatementMethodInterceptor.class);

	private static final String BATCH_STATEMENT_FORMAT = "%s /** count(%d) **/";
	private static final String STATEMENT_METHOD_ADD_BATCH = "addBatch";
	private static final String STATEMENT_METHOD_EXECUTE_BATCH = "executeBatch";
	private static final String STATEMENT_METHOD_EXECUTE = "execute";
	private static final String PREPARED_STATEMENT_METHOD_SET = "set";
	private static final String PREPARED_STATEMENT_METHOD_SET_NULL = "setNull";
	private static final String ORACLE_QUERY_TIMEOUT_CODE = "ORA-01013";

	private static QueryNormalizer queryNormalizer = new QueryNormalizer();
	private static HibernateQueryNormalizer hibernateQueryNormalizer = new HibernateQueryNormalizer();
	private static SqlFormatter sqlFormatter = new SqlFormatter();

	private Map<Statement, StatementExecutionInfo> statementInfoMap;

	private SqlTaskPool sqlTaskPool;

	private DataSourceConfiguration dataSourceConfiguration;

	public StatementMethodInterceptor(DataSourceConfiguration dataSourceConfiguration, Map<Statement, StatementExecutionInfo> statementInfoMap, SqlTaskPool sqlTaskPool) {
		this.dataSourceConfiguration = dataSourceConfiguration;
		this.statementInfoMap = statementInfoMap;
		this.sqlTaskPool = sqlTaskPool;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!(invocation.getThis() instanceof Statement)) {
			return invocation.proceed();
		}
		Object returnValue;
		SqlExecutionInfo sqlExecutionInfo = null;
		Method invokedMethod = invocation.getMethod();

		try {
			StatementExecutionInfo statementExecutionInfo = statementInfoMap.get(invocation.getThis());
			if (statementExecutionInfo == null) {
				logger.error("StatementExecutionInfo is null.");
				return invocation.proceed();
			}

			if (invocation.getThis() instanceof PreparedStatement &&
					invokedMethod.getName().startsWith(PREPARED_STATEMENT_METHOD_SET)) {
				returnValue = invocation.proceed();

				if (invocation.getArguments().length >= 2) {
					List<Object> parameterList = statementExecutionInfo.getCurrentParameters();
					Integer position = (Integer) invocation.getArguments()[0];

					while (position >= parameterList.size()) {
						parameterList.add(null);
					}

					if (!StringUtils.equals(invokedMethod.getName(), PREPARED_STATEMENT_METHOD_SET_NULL)) {
						parameterList.set(position, invocation.getArguments()[1]);
					}
				}
			} else if (invokedMethod.getName().equals(STATEMENT_METHOD_ADD_BATCH)) {
				returnValue = invocation.proceed();

				if (statementExecutionInfo.getBatchCount() == 0) {
					String statementQuery = getQueryFromStatement(statementExecutionInfo, invocation);
					statementExecutionInfo.setFirstBatchQuery(statementQuery);
				}

				statementExecutionInfo.incrementBatchCount();
			} else if (invokedMethod.getName().startsWith(STATEMENT_METHOD_EXECUTE)) {
				if (statementExecutionInfo.getStatementType() == StatementType.statement) {
					String statementQuery;

					if (invokedMethod.getName().equals(STATEMENT_METHOD_EXECUTE_BATCH)) {
						statementQuery = String.format(BATCH_STATEMENT_FORMAT, statementExecutionInfo.getFirstBatchQuery(), statementExecutionInfo.getBatchCount());
					} else {
						statementQuery = getQueryFromStatement(statementExecutionInfo, invocation);
					}

					sqlExecutionInfo = sqlTaskPool.get(statementQuery);

					if (sqlExecutionInfo.isNew()) {
						sqlExecutionInfo.setDataSourceId(dataSourceConfiguration.getDataSourceId());
						sqlExecutionInfo.setType(statementExecutionInfo.getStatementType());
					}

				} else {
					sqlExecutionInfo = sqlTaskPool.get(statementExecutionInfo.getQueryFormat());
				}

				long currentTaskStartTime = System.currentTimeMillis();
				returnValue = invocation.proceed();
				long lastTaskTime = System.currentTimeMillis() - currentTaskStartTime;

				if (isSlowQuery(lastTaskTime) && sqlExecutionInfo != null) {
					sqlExecutionInfo.addSlowQueryCount();
				}

				if (sqlExecutionInfo != null) {
					sqlExecutionInfo.appendTaskTime(lastTaskTime, statementExecutionInfo.getCurrentParameters().toArray());
				}

				if (isSlowQuery(lastTaskTime) && logger.isErrorEnabled()) {
					String sqlLogging = getLoggingSql(statementExecutionInfo, sqlExecutionInfo, invocation);
					logger.error("\n[slowQuery] - {}ms {}\n", lastTaskTime, sqlLogging);

				} else if (logger.isDebugEnabled()) {
					String sqlLogging = getLoggingSql(statementExecutionInfo, sqlExecutionInfo, invocation);
					logger.info("\n[query] - {} {}\n", new Date().toString(), sqlLogging);

				} else if (logger.isInfoEnabled() && dataSourceConfiguration.isSqlLogging()) {
					String sqlLogging = getLoggingSql(statementExecutionInfo, sqlExecutionInfo, invocation);
					logger.info("\n[query] - {} {}\n", new Date().toString(), sqlLogging);
				}
			} else {
				returnValue = invocation.proceed();
			}
		} catch (Exception exception) {
			if (sqlExecutionInfo != null) {
				boolean timeoutException = false;
				switch (dataSourceConfiguration.getDatabaseType()) {
					case "mysql":
						if (exception instanceof CommunicationException && (exception.getCause() != null && exception.getCause() instanceof SocketTimeoutException)) {
							sqlExecutionInfo.addSocketTimeoutCount();
							timeoutException = true;
						}
						break;
					case "oracle":
						if (StringUtils.indexOf(exception.getMessage(), ORACLE_QUERY_TIMEOUT_CODE) != -1) {
							sqlExecutionInfo.addQueryTimeoutCount();
							timeoutException = true;
						}
						break;
				}
				if (!timeoutException) {
					sqlExecutionInfo.addExceptionCount();
				}
			}
			throw exception;
		} finally {
			if (invocation.getMethod().getName().startsWith("close") && invocation.getThis() instanceof Statement) {
				statementInfoMap.remove(invocation.getThis());
			}
		}
		return returnValue;
	}

	private boolean isSlowQuery(long lastTaskTime) {
		return lastTaskTime > dataSourceConfiguration.getSlowQueryThreshold();
	}

	private String getQueryFromStatement(StatementExecutionInfo statementExecutionInfo, MethodInvocation methodInvocation) {
		String query = null;

		switch (statementExecutionInfo.getStatementType()) {
			case statement: {
				query = (String) methodInvocation.getArguments()[0];
				break;
			}
			case preparedStatement:
			case callableStatement: {
				List<Object> parameters = statementExecutionInfo.getCurrentParameters();
				String queryFormat = statementExecutionInfo.getQueryFormat();
				query = queryNormalizer.format(dataSourceConfiguration.getDatabaseType(), queryFormat, parameters);
				break;
			}
		}
		return query;
	}

	private String getLoggingSql(StatementExecutionInfo statementExecutionInfo, SqlExecutionInfo sqlExecutionInfo, MethodInvocation invocation) {
		String sqlLogging = (statementExecutionInfo.getBatchCount() > 0) ? sqlExecutionInfo.getSql() : getQueryFromStatement(statementExecutionInfo, invocation);

		if (dataSourceConfiguration.isQueryFormatting()) {
			try {
				String formattedSql = sqlFormatter.format(sqlLogging);

				if (dataSourceConfiguration.isHibernateQueryFormatting()) {
					return hibernateQueryNormalizer.format(formattedSql);
				}

				return formattedSql;
			} catch (Exception except) {
				logger.debug("sql formatting exception, sql - {}", sqlLogging, except);
			}
		}
		return sqlLogging;
	}
}
