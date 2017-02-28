function setIframeHeight($ifrm) {
  var iFrame = $ifrm.get(0);
  var iframewindow= iFrame.contentWindow ? iFrame.contentWindow : iFrame.contentDocument.defaultView;
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

// Run only after entire window (and frames) are loaded
// Note: We use window.load instead of document.ready to ensure frames have loaded
  // $( document ).ready(function() {
  $(window).on("load", function() {

    // alert("wx is ready");
    $wxIFrame = $('<iframe>', {
        src: '../WxPlatformMonitoring/index-copy.html',
        width: '100%',
        id: 'WxPlatformMonitoringIFrame',
        frameborder: 0,
        scrolling: 'no',
    });

    var $mainBody = $(window.parent.frames[0].frames[2].document);

    $('body', $mainBody).append($wxIFrame);
    // setIframeHeight($wxIFrame.get(0));
    setIframeHeight($wxIFrame);
});
// });
