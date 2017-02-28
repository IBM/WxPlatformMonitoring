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

$(window).on("load", function() {
    if (extSettings_showOnStatusPage !== true) {
        console.log("WxPlatformMonitoring: Extended settings 'watt.wx.platformMonitoring.healthPortlet.showOnStatusPage' is set to '" + extSettings_showOnStatusPage + "', there not including WxPlatformMonitoring health page");
        return;
    }
    if( extSettings_importHealthPagePath === null ) {
      extSettings_importHealthPagePath = "../WxPlatformMonitoring/wxplatformmonitoring_healthpage.html";
    }
    $wxIFrame = $('<iframe>', {
        src: extSettings_importHealthPagePath,
        width: '100%',
        id: 'WxPlatformMonitoringIFrame',
        frameborder: 0,
        scrolling: 'no',
    });
    // get the body of the main frame, i.e. the frame with the is-stats.dsp
    var $mainBody = $(window.parent.frames[0].frames[2].document);
    $('body', $mainBody).append($wxIFrame);
    setIframeHeight($wxIFrame);
});
