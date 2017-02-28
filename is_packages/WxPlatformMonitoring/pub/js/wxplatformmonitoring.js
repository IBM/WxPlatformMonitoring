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


// function handleResponse(id, isOk, jsonResponse, url, statusValue);

function invoke(url, data, id, checkFunctionCallback, getStatusValueCallback) {
    $.get({
            url: url,
            data: data,
            dataType: "json"
        }).done(function(msg) {
            var isOk = checkFunctionCallback(msg);
            var statusValue = getStatusValueCallback(msg);
            handleResponse(id, isOk, msg, url, statusValue);
        })
        .fail(function(jqXHR, exception, error) {
            if (jqXHR.status === 0) {
                alert('Not connect.\n Verify Network.');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found. [404]');
            } else if (jqXHR.status == 500) {
                // alert('Internal Server Error [500].');
                handleResponse(id, false, jqXHR.responseText, url, "n/a");
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

function handleResponse(id, isOk, jsonResponse, url, statusValue) {
    var $statusTr = $("[id='" + id + "']");
    var $tdStatus = $statusTr.children().first();
    if (isOk) {
        $tdStatus.attr("class", "is-state-ok");
    } else if (!isOk) {
        $tdStatus.attr("class", "is-state-nok");
    } else {
        $statusTr.remove();
    }
    var $spanStatusValue = $tdStatus.next().children().first();
    var $statusDetail = $statusTr.find(".status-details");
    $infoSpanLink = $("<a/>", {
        name: "link",
        href: "#"
    });
    $infoSpan = $("<span/>").attr("class", "is-link material-icons").text("info_outline");
    $infoSpanLink.append($infoSpan);
    $statusDetail.append($infoSpanLink);
    $infoSpan.click(function() {
        var formatter = new JSONFormatter(jsonResponse, 1, {
            hoverPreviewEnabled: true
        });
        $result = $("<span/>").attr("class", "wx-json-formatter json-formatter-row json-formatter-open");
        var rs = $result.get(0);
        rs.innerHTML = '';
        rs.appendChild(formatter.render());
        bootbox.alert({
            size: "medium",
            title: "Response data for '<span class='wx-response-modal-bold'>" + url + "</span>'",
            message: rs,
            className: "wx-response-modal"
        });
    });
    // var statusValue = getStatusValueCallback(msg);
    $spanStatusValue.text(statusValue);
}
