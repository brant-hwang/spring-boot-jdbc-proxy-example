package io.brant.example.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dataSource.custom", ignoreInvalidFields = true)
public class DataSourceConfiguration {

	private String databaseType;
	private String username;
	private String password;
	private String url;
	private String schema;
	private String driverClassName;
	private String connectionInitSql;
	private int initialSize;
	private int minIdle;
	private int maxIdle;
	private int maxActive;
	private int maxWait;
	private boolean sqlLogging;
	private long slowQueryThreshold;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean testWhileIdle;
	private boolean accessToUnderlyingConnectionAllowed;
	private long timeBetweenEvictionRunsMillis;
	private long minEvictableIdleTimeMillis;
	private long softMinEvictableIdleTimeMillis;
	private String dataSourceId;
	private int queryTimeout;
	private boolean queryFormatting;
	private boolean hibernateQueryFormatting;
	private String validationQuery;

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getConnectionInitSql() {
		return connectionInitSql;
	}

	public void setConnectionInitSql(String connectionInitSql) {
		this.connectionInitSql = connectionInitSql;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isSqlLogging() {
		return sqlLogging;
	}

	public void setSqlLogging(boolean sqlLogging) {
		this.sqlLogging = sqlLogging;
	}

	public long getSlowQueryThreshold() {
		return slowQueryThreshold;
	}

	public void setSlowQueryThreshold(long slowQueryThreshold) {
		this.slowQueryThreshold = slowQueryThreshold;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isAccessToUnderlyingConnectionAllowed() {
		return accessToUnderlyingConnectionAllowed;
	}

	public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
		this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public long getSoftMinEvictableIdleTimeMillis() {
		return softMinEvictableIdleTimeMillis;
	}

	public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
		this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public boolean isQueryFormatting() {
		return queryFormatting;
	}

	public void setQueryFormatting(boolean queryFormatting) {
		this.queryFormatting = queryFormatting;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public boolean isHibernateQueryFormatting() {
		return hibernateQueryFormatting;
	}

	public void setHibernateQueryFormatting(boolean hibernateQueryFormatting) {
		this.hibernateQueryFormatting = hibernateQueryFormatting;
	}
}


