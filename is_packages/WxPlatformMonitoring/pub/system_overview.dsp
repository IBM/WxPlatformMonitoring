%comment%
	Add the following line to the file .\IntegrationServer\packages\WmRoot\pub\stats-general.dsp:

	%include ../../WxPlatformMonitoring/pub/system_overview.dsp%
%endcomment%


	<LINK rel="stylesheet" type="text/css" href="../../WxPlatformMonitoring/wxplatformmonitoring_esb.css"/>

	<div class="box">
		<div class="cell">
			%include ../../WxPlatformMonitoring/pub/miscStatusList.dsp%
			<br/>
			%include ../../WxPlatformMonitoring/pub/schedulerList.dsp%
			<br/>
			%include ../../WxPlatformMonitoring/pub/cacheStatusList.dsp%
		</div>
		<div class="cell">
			%include ../../WxPlatformMonitoring/pub/sapListenerList.dsp%
			<br/>
			%include ../../WxPlatformMonitoring/pub/triggerList.dsp%
		</div>
		<div class="floatclear"></div>
	</div>
	<br/><br/>
