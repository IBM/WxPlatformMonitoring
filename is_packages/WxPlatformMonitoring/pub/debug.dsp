
<HTML>
<HEAD>
	<META http-equiv="Pragma" content="no-cache">
	<META http-equiv='content-type' content='text/html; charset=UTF-8'>
	<META http-equiv="Expires" content="-1">
	<TITLE>Reiff ESB - Debug</TITLE>
	<LINK rel="stylesheet" type="text/css" href="reiff_esb.css">
	<SCRIPT SRC="webMethods.js.txt"></SCRIPT>
</HEAD>

<BODY style="border-width:0px;">

   <h1>Reiff ESB - Debug</h1>

      	%ifvar callService equals('true')%

        	%invoke reiff_administration.services.pub.test:invokeServiceWithTracePipelineAsString%
		%endinvoke%
	%end%

	%ifvar msg%
        	<div class="message">%value msg%</div>
	%endif%
	
	
	<div class="box">
		<h2>Debug Service</h2>
		<p class="text">
			Call a service with the provided data.
		</p>
        <FORM name="callService" target="body" action="debug.dsp" METHOD="POST">
			<table class="key_value_list">
				<tr>
					<td class="cell_key">Service FQN</td>
					<td class="cell_value">
						<input type="text" name="serviceFQN" size=70></input>
					</td>
				</tr>
				<tr>
					<td class="cell_key">Service FQN</td>
					<td class="cell_value">
						<textarea name="tracePipelineAsString" cols="50" rows="10"></textarea>
					</td>
				</tr>
				<tr>
					<td class="cell_key">Call Service</td>
					<td class="cell_value">
						<input type="hidden" name="callService" value="true"/>
						<input type="submit" value="Call Service"></input>
					</td>
				</tr>
			</table>
		 </form>

</div>

</BODY>
</HTML>
