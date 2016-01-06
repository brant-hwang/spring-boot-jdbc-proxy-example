package io.brant.example.jdbc.db.utils;

import io.brant.example.jdbc.db.monitor.sql.SqlExecutionInfo;
import org.apache.commons.lang3.SystemUtils;

import java.util.List;

public class SqlMonitoringLogUtil {
	private List<SqlExecutionInfo> sqlExecutionInfoList;

	public SqlMonitoringLogUtil(List<SqlExecutionInfo> sqlExecutionInfoList) {
		this.sqlExecutionInfoList = sqlExecutionInfoList;
	}

	public void saveSqlMonitoringInfo() {
		if (this.sqlExecutionInfoList == null) {
			return;
		}
		StringBuilder stringBuilder = new StringBuilder("[SqlMonitoringInfo]");
		stringBuilder.append(SystemUtils.LINE_SEPARATOR);
	}
}
