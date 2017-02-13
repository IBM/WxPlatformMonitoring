function evaluateExtendedSettings(propertyName, checkFunctionCallback, parentId) {


$.get({
    url: "/invoke/pub.utils/getServerProperty",
    data: {
      propertyName: propertyName
    },
    dataType: "json"
  }).done(function(msg) {
      console.log("propertyValue: "  + msg.propertyValue);
      var isOk = checkFunctionCallback(msg.propertyValue);
      var $extSettingTr = $("[id='" + propertyName + "']");
      var $tdStatus = $("<td />");
      if( isOk ) {
        $tdStatus.attr("class","status_ok");
      } else {
        $tdStatus.attr("class","status_nok");
      }
      var $tdText = $("<td />").text("Property " + propertyName + ": " + msg.propertyValue);
      $extSettingTr.append($tdStatus);
      $extSettingTr.append($tdText);
  })
  .fail(function(jqXHR, exception) {
    if (jqXHR.status === 0) {
      alert('Not connect.\n Verify Network.');
    } else if (jqXHR.status == 404) {
      alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
      alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
      alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
      alert('Time out error.');
    } else if (exception === 'abort') {
      alert('Ajax request aborted.');
    } else {
      alert('Uncaught Error.\n' + jqXHR.responseText);
    }
  });
}
