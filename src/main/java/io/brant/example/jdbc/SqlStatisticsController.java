package io.brant.example.jdbc;


import io.brant.example.jdbc.config.DataSourceConfiguration;
import io.brant.example.jdbc.db.monitor.SqlMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SqlStatisticsController {

	@Autowired
	private SqlMonitoringService sqlMonitoringService;

	@Autowired
	private DataSourceConfiguration dataSourceConfiguration;

	@RequestMapping(value = "/sql", method = RequestMethod.GET)
	public String sql(ModelMap modelMap) {
		modelMap.put("dataSourceConfiguration", dataSourceConfiguration);
		modelMap.put("slowQueryThreshold", dataSourceConfiguration.getSlowQueryThreshold());
		modelMap.put("sqlExecutionInfos", sqlMonitoringService.getSqlExecutionInfos());
		return "view";
	}
}
