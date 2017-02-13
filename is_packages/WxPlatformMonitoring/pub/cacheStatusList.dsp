<table class="status">
	<tr>
		<th class="status_header"></th>
		<th class="status_header">Cache</th>
		<th class="status_header">Cache Manager</th>
	</tr>

	%invoke reiff_administration.services.pub.monitoring.cache:listCaches%
	%endinvoke%
	%loop statusList%
		<tr>
			%ifvar enabled equals('true')%
				<td class="status_ok"/>
			%else%
				<td class="status_nok"/>						
			%end%
			<td class="status_name">%value name%</td>
			<td class="status_name">%value package%</td>
		</tr>
	%end%
	
</table>
