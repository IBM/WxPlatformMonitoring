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
    //
    // $('head', $mainBody).append('<link rel="stylesheet" type="text/css" href="/WxPlatformMonitoring/css/pure-nr-min.css" />');
    // $('head', $mainBody).append('<link rel="stylesheet" type="text/css" href="/WxPlatformMonitoring/css/bootstrap/bootstrap.min.css" />');
    // $('head', $mainBody).append('<link rel="stylesheet" type="text/css" href="/WxPlatformMonitoring/css/json-formatter.css" />');
    // $('head', $mainBody).append('<link rel="stylesheet" type="text/css" href="/WxPlatformMonitoring/css/wxplatformmonitoring.css" />');
    // $('head', $mainBody).append('<link rel="stylesheet" type="text/css" href="/WmRoot/webMethods.css" />');
    //
    // appendScript("/WxPlatformMonitoring/js/jquery-3.1.1.min.js", $('head', $mainBody));
    // appendScript("/WxPlatformMonitoring/js/bootstrap.min.js", $('head', $mainBody));
    // appendScript("/WxPlatformMonitoring/js/bootbox.min.js", $('head', $mainBody));
    // appendScript("/WxPlatformMonitoring/js/json-formatter.js", $('head', $mainBody));

    // $('head', $mainBody).append('<script src="/WxPlatformMonitoring/js/jquery-3.1.1.min.js"></' + 'script>');
    // $('head', $mainBody).append('<script src="/WxPlatformMonitoring/js/bootstrap.min.js"></' + 'script>');
    // $('head', $mainBody).append('<script src="/WxPlatformMonitoring/js/bootbox.min.js"></' + 'script>');
    // $('head', $mainBody).append('<script src="/WxPlatformMonitoring/js/wxplatformmonitoring.js"></' + 'script>');
    // $('head', $mainBody).append('<script src="/WxPlatformMonitoring/js/json-formatter.js"></' + 'script>');
    //
    // $b = $('body', $mainBody);
    //   $('<script>alert("hi");</' + 'script>').appendTo($b);

    $('body', $mainBody).append($wxIFrame);
    // $.get(extSettings_importHealthPagePath, function(data){
    //   $('body', $mainBody).append(data);
    // });
    // appendScript("/WxPlatformMonitoring/js/wxplatformmonitoring.js", $('head', $mainBody));
    //setIframeHeight($wxIFrame);
});
