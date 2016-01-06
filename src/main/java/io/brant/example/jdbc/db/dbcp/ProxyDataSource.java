package io.brant.example.jdbc.db.dbcp;

import io.brant.example.jdbc.config.DataSourceConfiguration;
import io.brant.example.jdbc.db.aop.CreateStatementInterceptor;
import io.brant.example.jdbc.db.aop.StatementExecutionInfo;
import io.brant.example.jdbc.db.monitor.sql.SqlTaskPool;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.aop.framework.ProxyFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ProxyDataSource extends BasicDataSource {
	private SqlTaskPool sqlTaskPool = new SqlTaskPool();

	private ConcurrentHashMap<Statement, StatementExecutionInfo> statementInfoMap = new ConcurrentHashMap<>();

	public ConcurrentHashMap<Statement, StatementExecutionInfo> getStatementInfoMap() {
		return statementInfoMap;
	}

	public SqlTaskPool getSqlTaskPool() {
		return sqlTaskPool;
	}

	private DataSourceConfiguration dataSourceConfiguration;

	public void setDataSourceConfiguration(DataSourceConfiguration dataSourceConfiguration) {
		this.dataSourceConfiguration = dataSourceConfiguration;
	}

	public DataSourceConfiguration getDataSourceConfiguration() {
		return dataSourceConfiguration;
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection connection = super.getConnection();
		return createProxy(connection);
	}

	@Override
	public Connection getConnection(String user, String pass) throws SQLException {
		Connection connection = super.getConnection(user, pass);
		return createProxy(connection);
	}

	@Override
	public synchronized void close() throws SQLException {
		super.close();
	}

	private Connection createProxy(Connection originalConnection) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(originalConnection);
		proxyFactory.addAdvice(new CreateStatementInterceptor(getDataSourceConfiguration(), getStatementInfoMap(), getSqlTaskPool()));
		proxyFactory.setInterfaces(new Class[]{Connection.class});
		return (Connection) proxyFactory.getProxy();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
