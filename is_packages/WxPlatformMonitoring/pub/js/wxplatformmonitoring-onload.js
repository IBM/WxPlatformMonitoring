function setIframeHeight($ifrm) {
    var iFrame = $ifrm.get(0);
    var iframewindow = iFrame.contentWindow ? iFrame.contentWindow : iFrame.contentDocument.defaultView;
    var $windowsContent = $(iframewindow);
    $windowsContent.on("load", function() {
        iFrame.style.visibility = 'hidden';
        iFrame.style.height = "10px"; // reset to minimal height ...
        // IE opt. for bing/msn needs a bit added or scrollbar appears
        var h = getDocHeight(iframewindow.document);
        iFrame.style.height = h + 4 + "px";
        iFrame.style.visibility = 'visible';
    });
}

function getDocHeight(doc) {
    doc = doc || document;
    // stackoverflow.com/questions/1145850/
    var body = doc.body,
        html = doc.documentElement;
    var height = Math.max(body.scrollHeight, body.offsetHeight,
        html.clientHeight, html.scrollHeight, html.offsetHeight);
    return height;
}

function appendScript(url, $parent) {
  var script = document.createElement( 'script' );
  script.type = 'text/javascript';
  script.src = url;
  $parent.append( script );
}




$(window).on("load", function() { 
	
		if (extSettings_showOnStatusPage !== true) {
		console.log("WxPlatformMonitoring: Extended settings 'watt.wx.platformMonitoring.healthPortlet.showOnStatusPage' is set to '" + extSettings_showOnStatusPage + "', there not including WxPlatformMonitoring health page");
			return;
		}
		if( extSettings_importHealthPagePath === null ) {
			extSettings_importHealthPagePath = "/WxPlatformMonitoring/wxplatformmonitoring_healthpage.html";
		}
	  var $mainBody = $(window.parent.frames[0].frames[2].document);
		var $body = $('body', $mainBody);
		var $div = $('<div>');
//		var $table = $('table:first', $body);
//		var $tbody = $('tbody:first', $table);
//		var $td = $('<td colspan="3" valign="top">');
//		var $tr = $('<tr>').append($td);
//		$tbody.append($tr);
		$body.append($div);
		$div.html('<object data="' + extSettings_importHealthPagePath + '" width="100%" height="100%">');
		
		//		$('body', $mainBody).children("#henning").html('<object data="/WxPlatformMonitoring/wxplatformmonitoring_healthpage.html" width="100%" height="100%">');
//		$('body', $mainBody).children("#henning").load('WxPlatformMonitoring/wxplatformmonitoring_healthpage.html');
//    $('#siteloader').load('http://www.somesitehere.com');
	
});
//$(window).on("load", function() {
//	if (extSettings_showOnStatusPage !== true) {
//		console.log("WxPlatformMonitoring: Extended settings 'watt.wx.platformMonitoring.healthPortlet.showOnStatusPage' is set to '" + extSettings_showOnStatusPage + "', there not including WxPlatformMonitoring health page");
//		return;
//	}
//	if( extSettings_importHealthPagePath === null ) {
//		extSettings_importHealthPagePath = "../WxPlatformMonitoring/wxplatformmonitoring_healthpage.html";
//	}
//	$wxIFrame = $('<iframe>', {
//		src: extSettings_importHealthPagePath,
//		width: '100%',
//		id: 'WxPlatformMonitoringIFrame',
//		frameborder: 0,
//		scrolling: 'no',
//	});
//	// get the body of the main frame, i.e. the frame with the is-stats.dsp
//	var $mainBody = $(window.parent.frames[0].frames[2].document);
//	$('body', $mainBody).append($wxIFrame);
//});
