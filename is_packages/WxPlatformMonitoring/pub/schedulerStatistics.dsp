
<HTML>
<HEAD>
	<META http-equiv="Pragma" content="no-cache">
	<META http-equiv='content-type' content='text/html; charset=UTF-8'>
	<META http-equiv="Expires" content="-1">
	<TITLE>Reiff ESB - Scheduled Tasks Statistiken</TITLE>
	<LINK rel="stylesheet" type="text/css" href="reiff_esb.css">
	<SCRIPT SRC="webMethods.js.txt"></SCRIPT>
</HEAD>

<BODY style="border-width:0px;">

   <h1>Reiff ESB - Scheduled Tasks Statistiken</h1>

	%ifvar msg%
        	<div class="message">%value msg%</div>
	%endif%
	
	
		<h2>SAP Scheduled Tasks Statistik</h2>

        	
%include schedulerList.dsp%
        	

</BODY>
</HTML>
