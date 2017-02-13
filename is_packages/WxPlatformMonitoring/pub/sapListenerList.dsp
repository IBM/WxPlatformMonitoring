%invoke reiff_administration.services.pub.monitoring.sap:checkSapListenerStatus%
%endinvoke%

<table class="status">
	<tr>
		<th class="status_header"></th>
		<th class="status_header">SAP</th>
	</tr>
	<tr>
		<td class="status_na"></td>
		<td class="status_name" style="font-weight:bold">SAP Listeners</td>
	</tr>	
	%ifvar nrEnabled equals('0')%
		%comment% display nothing, all SAP Listeners are disabled %endcomment%
	%else%
		<tr>
			<td class="status_ok"></td>
			<td class="status_name">%value nrEnabled% SAP Listeners are enabled.</td>
		</tr>
	%end%
	%ifvar nrDisabled equals('0')%
		%comment% display nothing, all SAP Listeners are enabled %endcomment%
	%else%
		<tr>
			<td class="status_nok"></td>
			<td class="status_name">%value nrDisabled% SAP Listeners are disabled.</td>
		</tr>
	%end%
	
%invoke reiff_administration.services.pub.monitoring.sap:checkSapListenerNotificationStatus%
%endinvoke%

	<tr>
		<td class="status_na"></td>
		<td class="status_name" style="font-weight:bold">SAP Listener Notifications</td>
	</tr>
	%ifvar nrEnabled equals('0')%
		%comment% display nothing, all SAP Listeners are disabled %endcomment%
	%else%
		<tr>
			<td class="status_ok"></td>
			<td class="status_name">%value nrEnabled% SAP Listener Notifications are enabled.</td>
		</tr>
	%end%
	%ifvar nrDisabled equals('0')%
		%comment% display nothing, all SAP Listener Notifications are enabled %endcomment%
	%else%
		<tr>
			<td class="status_nok"></td>
			<td class="status_name">%value nrDisabled% SAP Listener Notifications are disabled.</td>
		</tr>
	%end%
	
</table>		