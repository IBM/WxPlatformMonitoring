<table class="status">
	<tr>
		<th class="status_header"></th>
		<th class="status_header">Asset</th>
	</tr>

	%scope param(propertyName='watt.wx.platformmonitoring.server.quiesce')%
	%invoke pub.utils:getServerProperty%
	%endinvoke%
	<tr>
		%ifvar propertyValue equals('true')%
			<td class="status_nok"/>
		%else%
			<td class="status_ok"/>
		%end%
		<td class="status_name">Loadbalancer: watt.wx.platformmonitoring.server.quiesce=%value propertyValue%</td>
	</tr>

	%scope param(propertyName='watt.server.scheduler.threadThrottle')%
	%invoke pub.utils:getServerProperty%
	%endinvoke%
	<tr>
		%ifvar propertyValue equals('0')%
			<td class="status_nok"/>
		%else%
			<td class="status_ok"/>
		%end%
		<td class="status_name">Scheduled Services Threads: watt.server.scheduler.threadThrottle=%value propertyValue%</td>
	</tr>

	%invoke wx.platformMonitoring.pub.onedata:checkServerConnection%
	%endinvoke%
	<tr>
		%ifvar success equals('true')%
			<td class="status_ok"/>
		%else%
			<td class="status_nok"/>
		%end%
		<td class="status_name">OneData (%value odeUrl%)</td>
	</tr>

</table>
