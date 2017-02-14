function evaluateExtendedSettings(propertyName, checkFunctionCallback) {
    var url = "/invoke/pub.utils/getServerProperty";
    var data = {
        propertyName: propertyName
    };
    invoke(url, data, propertyName, checkFunctionCallback, function(result) {
        return result.propertyValue;
    });

}

function evaluteService(serviceName, checkFunctionCallback, getStatusValueCallback) {
  var serviceNameSplit = serviceName.split(":");
  var url = "/invoke/" + serviceNameSplit[0] + "/" + serviceNameSplit[1];
  invoke(url, null, serviceName, checkFunctionCallback, getStatusValueCallback);
}

function invoke(url, data, id, checkFunctionCallback, getStatusValueCallback) {
    $.get({
            url: url,
            data: data,
            dataType: "json"
        }).done(function(msg) {
            // console.log("propertyValue: "  + msg.propertyValue);
            var isOk = checkFunctionCallback(msg);
            var $statusTr = $("[id='" + id + "']");
            var $tdStatus = $statusTr.children().first();
            if (isOk) {
                $tdStatus.attr("class", "is-state-ok");
            } else {
                $tdStatus.attr("class", "is-state-nok");
            }
            var $spanStatusValue = $tdStatus.next().children().first();
            var statusValue = getStatusValueCallback(msg);
            $spanStatusValue.text(statusValue);
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
