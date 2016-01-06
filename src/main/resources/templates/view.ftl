<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
</head>
<body>
<div class="container-fluid">
	<table id="performance" class="table table-striped">
		<thead>
		<tr>
			<th>SQL</th>
			<th>min(ms)</th>
			<th>max(ms)</th>
			<th>average(ms)</th>
			<th>total(ms)</th>
			<th>total Execution</th>
			<th>slowQueries(>${slowQueryThreshold}ms)</th>
			<th>exceptions</th>
			<th>timeout</th>
			<th>last execution time</th>
			<th>longest execution time</th>
		</tr>
		</thead>
		<tbody>
		<#if sqlExecutionInfos??>
			<#list sqlExecutionInfos as item>
			<tr>
				<td>
					<pre>${item.getFormattedSql(dataSourceConfiguration.isHibernateQueryFormatting())?string}</pre>
				</td>
				<td>${item.min?c}</td>
				<td>${item.max?c}</td>
				<td>${item.average?c}</td>
				<td>${item.total?c}</td>
				<td>${item.count?c}</td>
				<td>${item.slowQueryCount?c}</td>
				<td>${item.exceptionCount?c}</td>
				<td>${item.queryTimeoutCount?c}</td>
				<td>${item.lastQueryDate?datetime}</td>
				<td>${item.longestQueryDateTime?datetime}</td>
			</tr>
			</#list>
		<#else>
		<tr>
			<td colspan="">No data</td>
		</tr>
		</#if>
		</tbody>
	</table>
</div>
</body>
</html>