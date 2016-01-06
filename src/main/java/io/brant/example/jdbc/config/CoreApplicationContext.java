package io.brant.example.jdbc.config;

import io.brant.example.jdbc.db.dbcp.ProxyDataSourceFactory;
import io.brant.example.jdbc.db.monitor.SqlMonitoringService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(value = DataSourceConfiguration.class)
public class CoreApplicationContext {

	@Bean
	@Primary
	public DataSource dataSource(DataSourceConfiguration dataSourceConfiguration) {
		try {
			return ProxyDataSourceFactory.create(dataSourceConfiguration);
		} catch (Throwable e) {
			throw new DataSourceLookupFailureException("Creating dataSource Failed!");
		}
	}

	@Bean
	public SqlMonitoringService sqlMonitoringService(DataSource dataSource) {
		return new SqlMonitoringService(dataSource);
	}
}
