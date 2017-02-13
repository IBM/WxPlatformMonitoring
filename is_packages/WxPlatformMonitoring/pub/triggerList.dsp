%invoke reiff_administration.services.pub.monitoring.trigger:listTriggers%
%endinvoke%
<table class="status">
	<tr>
		<th class="status_header"></th>
		<th class="status_header">Trigger Name</th>
	</tr>
	<tr>
		<td class="status_ok"></td>
		<td class="status_name">%value nrEnabled% Triggers are enabled.</td>
	</tr>
	%ifvar nrDisabled equals('0')%
		%comment% display nothing, all triggers are enabled %endcomment%
	%else%
		<tr>
			<td class="status_nok"></td>
			<td class="status_name">%value nrDisabled% Triggers (JMS and Broker) are disabled.</td>
		</tr>
	%end%
</table>
