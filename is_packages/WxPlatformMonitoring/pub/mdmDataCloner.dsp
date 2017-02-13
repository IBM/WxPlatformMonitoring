
<HTML>
<HEAD>
	<META http-equiv="Pragma" content="no-cache">
	<META http-equiv='content-type' content='text/html; charset=UTF-8'>
	<META http-equiv="Expires" content="-1">
	<TITLE>Reiff ESB - ERP Update Mail</TITLE>
	<LINK rel="stylesheet" type="text/css" href="reiff_esb.css">
	<SCRIPT>
		function actionHandler(action) {
			switch(action) {
				case 'disableTrigger':
					alert('disableTrigger');
					break;
				case 'deleteAllArticleData':
					alert('deleteAllArticleData');
					break;
				case 'enableTrigger':
					alert('enableTrigger');
					break;
				
				case 'adjustSequenzes':
					alert('adjustSequenzes');
					break;
				default:
					alert('nix');
					break;
			}
		}
	</SCRIPT>
</HEAD>

<BODY style="border-width:0px;">

   <h1>Reiff ESB - MDM Data Cloner</h1>

    %ifvar action -notempty%
		<div class="message">%value action%</div>
		
	%end%

	%ifvar msg%
        	<div class="message">%value msg%</div>
	%endif%
	
	
	<div class="box">
		<h2>Artikeldaten klonen</h2>
		<p class="text">
			Klonen von Artikeldaten des Produktivsystems auf eines der Testsysteme.
		</p>
			<table class="key_value_list">
				<tr>
				<FORM name="callService" target="body" action="mdmDataCloner.dsp" METHOD="POST">
						<td>1.</td>
						<td class="cell_key">Trigger deaktivieren</td>
						<td class="cell_value">
							<input type="button" value="Aktion ausf&uuml;hren" onclick="actionHandler('disableTrigger');"/>
						</td>
					</FORM>
				</tr>
				<tr>
					<FORM name="callService" target="body" action="mdmDataCloner.dsp" METHOD="POST">
						<td>2.</td>
						<td class="cell_key">ALLE DATEN L&Ouml;SCHEN</td>
						<td class="cell_value">
							<input type="button" value="Aktion ausf&uuml;hren" onclick="actionHandler('deleteAllArticleData');"/>
						</td>
					</FORM>
				</tr>
				<tr>
					<FORM name="callService" target="body" action="mdmDataCloner.dsp" METHOD="POST">
						<td>3.</td>
						<td class="cell_key">Job in OneData starten f&uuml;r Datenimport</td>
						<td class="cell_value">
							&nbsp;
						</td>
					</FORM>
				</tr>
				<tr>
					<FORM name="callService" target="body" action="mdmDataCloner.dsp" METHOD="POST">
						<td>4.</td>
						<td class="cell_key">Trigger aktivieren</td>
						<td class="cell_value">
							<input type="button" value="Aktion ausf&uuml;hren" onclick="actionHandler('enableTrigger');"/>
						</td>
					</FORM>
				</tr>
				<tr>
					<FORM name="callService" target="body" action="mdmDataCloner.dsp" METHOD="POST">
						<td>5.</td>
						<td class="cell_key">Sequenzwerte neusetzen</td>
						<td class="cell_value">
							<input type="button" value="Aktion ausf&uuml;hren" onclick="actionHandler('adjustSequenzes');"/>
						</td>
					</FORM>
				</tr>
			</table>
		 </form>

</div>

</BODY>
</HTML>
