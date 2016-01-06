package io.brant.example.jdbc.db.dbcp;

import io.brant.example.jdbc.config.DataSourceConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ProxyDataSourceFactory {
	private static Logger logger = LoggerFactory.getLogger(ProxyDataSourceFactory.class);

	public static ProxyDataSource create(DataSourceConfiguration dataSourceConfiguration) throws Exception {
		try {
			Collection<String> initSqls = null;
			if (StringUtils.isNoneEmpty(dataSourceConfiguration.getConnectionInitSql())) {
				String[] initSqlTokens = StringUtils.split(dataSourceConfiguration.getConnectionInitSql(), ",");

				if (ArrayUtils.isNotEmpty(initSqlTokens)) {
					initSqls = new ArrayList<>();
					Collections.addAll(initSqls, initSqlTokens);
				}
			}

			ProxyDataSource proxyDataSource = new ProxyDataSource();

			if (!CollectionUtils.isEmpty(initSqls)) {
				proxyDataSource.setConnectionInitSqls(initSqls);
			}

			proxyDataSource.setInitialSize(dataSourceConfiguration.getInitialSize());
			proxyDataSource.setMinIdle(dataSourceConfiguration.getMinIdle());
			proxyDataSource.setMaxIdle(dataSourceConfiguration.getMaxIdle());
			proxyDataSource.setMaxWaitMillis(dataSourceConfiguration.getMaxWait());
			proxyDataSource.setAccessToUnderlyingConnectionAllowed(dataSourceConfiguration.isAccessToUnderlyingConnectionAllowed());
			proxyDataSource.setConnectionInitSqls(initSqls);
			proxyDataSource.setUrl(dataSourceConfiguration.getUrl());
			proxyDataSource.setUsername(dataSourceConfiguration.getUsername());
			proxyDataSource.setPassword(dataSourceConfiguration.getPassword());
			proxyDataSource.setDriverClassName(dataSourceConfiguration.getDriverClassName());
			proxyDataSource.setTestOnBorrow(dataSourceConfiguration.isTestOnBorrow());
			proxyDataSource.setTestOnReturn(dataSourceConfiguration.isTestOnReturn());
			proxyDataSource.setTestWhileIdle(dataSourceConfiguration.isTestWhileIdle());
			proxyDataSource.setTimeBetweenEvictionRunsMillis(dataSourceConfiguration.getTimeBetweenEvictionRunsMillis());
			proxyDataSource.setMinEvictableIdleTimeMillis(proxyDataSource.getMinEvictableIdleTimeMillis());
			proxyDataSource.setSoftMinEvictableIdleTimeMillis(proxyDataSource.getSoftMinEvictableIdleTimeMillis());
			proxyDataSource.setMaxTotal(dataSourceConfiguration.getMaxActive());
			proxyDataSource.setValidationQuery(dataSourceConfiguration.getValidationQuery());

			proxyDataSource.setDataSourceConfiguration(dataSourceConfiguration);

			Connection conn = proxyDataSource.getConnection();
			conn.close();
			logger.info("success to create DataSource('{}')", dataSourceConfiguration.getDataSourceId());

			return proxyDataSource;

		} catch (Exception exception) {
			logger.error("fail to create DataSource('{}')", dataSourceConfiguration.getDataSourceId(), exception);
			throw exception;
		}
	}
}
