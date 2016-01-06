package io.brant.example.jdbc.db.monitor;

import io.brant.example.jdbc.db.dbcp.ProxyDataSource;
import io.brant.example.jdbc.db.monitor.sql.SqlExecutionInfo;
import io.brant.example.jdbc.db.utils.SqlMonitoringLogUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

public class SqlMonitoringService implements InitializingBean, DisposableBean {

	private ProxyDataSource dataSource;

	private SqlMonitoringLogUtil sqlMonitoringLogUtil;

	public SqlMonitoringService(DataSource dataSource) {
		if (dataSource instanceof ProxyDataSource) {
			this.dataSource = (ProxyDataSource) dataSource;
		}
	}

	public List<SqlExecutionInfo> getSqlExecutionInfos() {
		if (this.dataSource != null) {
			return this.dataSource.getSqlTaskPool().getSqlExecutionInfoList();
		} else {
			return Collections.emptyList();
		}
	}

	public void saveAll() {
		sqlMonitoringLogUtil.saveSqlMonitoringInfo();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.dataSource != null) {
			sqlMonitoringLogUtil = new SqlMonitoringLogUtil(this.dataSource.getSqlTaskPool().getSqlExecutionInfoList());
		}
	}

	@Override
	public void destroy() throws Exception {
		if (this.dataSource != null) {
			saveAll();
		}
	}
}
