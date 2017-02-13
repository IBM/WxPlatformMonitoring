%invoke reiff_administration.services.pub.monitoring.scheduler:listScheduledServices%
%endinvoke%

<table class="status">
	<tr>
		<th class="status_header"></th>
		<th class="status_header">Scheduler</th>
	</tr>
	<tr>
		<td class="status_ok"></td>
		<td class="status_name">%value nrEnabled% Schedulers are enabled.</td>
	</tr>
	%ifvar nrDisabled equals('0')%
		%comment% display nothing, all schedulers are enabled %endcomment%
	%else%
		<tr>
			<td class="status_nok"></td>
			<td class="status_name">%value nrDisabled% Schedulers are disabled.</td>
		</tr>
	%end%
	
</table>
