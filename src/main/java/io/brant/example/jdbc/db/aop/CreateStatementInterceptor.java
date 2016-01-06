package io.brant.example.jdbc.db.aop;


import io.brant.example.jdbc.config.DataSourceConfiguration;
import io.brant.example.jdbc.db.monitor.sql.SqlExecutionInfo;
import io.brant.example.jdbc.db.monitor.sql.SqlTaskPool;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Map;

public class CreateStatementInterceptor implements MethodInterceptor {
	private static final String[] CREATE_STATEMENT_METHOD = {"createStatement", "prepareStatement", "prepareCall"};

	private DataSourceConfiguration dataSourceConfiguration;

	private Map<Statement, StatementExecutionInfo> statementInfoMap;

	private SqlTaskPool sqlTaskPool;

	public CreateStatementInterceptor(DataSourceConfiguration dataSourceConfiguration, Map<Statement, StatementExecutionInfo> statementInfoMap, SqlTaskPool sqlTaskPool) {
		this.dataSourceConfiguration = dataSourceConfiguration;
		this.statementInfoMap = statementInfoMap;
		this.sqlTaskPool = sqlTaskPool;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object returnValue = invocation.proceed();
		Method invokedMethod = invocation.getMethod();

		Integer queryTimeout = dataSourceConfiguration.getQueryTimeout();

		if (StringUtils.indexOfAny(invokedMethod.getName(), CREATE_STATEMENT_METHOD) == -1) {
			return returnValue;
		}

		if (returnValue instanceof Statement) {
			((Statement) returnValue).setQueryTimeout(queryTimeout);
		}

		Statement statement = getRealStatement(returnValue);

		if (!statementInfoMap.containsKey(statement)) {
			StatementExecutionInfo statementExecutionInfo = new StatementExecutionInfo(statement);

			if (statementExecutionInfo.getStatementType() != StatementType.statement) {
				String queryFormat = (String) invocation.getArguments()[0];
				statementExecutionInfo.setQueryFormat(queryFormat);

				SqlExecutionInfo sqlExecutionInfo = sqlTaskPool.get(queryFormat);

				if (sqlExecutionInfo.isNew()) {
					sqlExecutionInfo.setDataSourceId(dataSourceConfiguration.getDataSourceId());
					sqlExecutionInfo.setType(statementExecutionInfo.getStatementType());
				}
			}
			statementInfoMap.put(statement, statementExecutionInfo);
		}

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.addAdvice(new StatementMethodInterceptor(dataSourceConfiguration, statementInfoMap, sqlTaskPool));
		proxyFactory.setTarget(returnValue);
		proxyFactory.setInterfaces(returnValue.getClass().getInterfaces());
		return proxyFactory.getProxy();
	}

	private Statement getRealStatement(Object returnValue) throws Throwable {
		if (AopUtils.isAopProxy(returnValue)) {
			Advised advised = (Advised) returnValue;
			return (Statement) advised.getTargetSource().getTarget();
		}
		return (Statement) returnValue;
	}
}
