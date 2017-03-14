function evaluateExtendedSettings(propertyName, checkFunctionCallback, details) {
    var url = "/invoke/pub.utils/getServerProperty";
    var data = {
        propertyName: propertyName
    };
    invoke(url, data, propertyName, checkFunctionCallback, function(result) {
        return result.propertyValue;
    }, details.description, details.linkHref);
}

function evaluteService(serviceName, checkFunctionCallback, getStatusValueCallback, details) {
    var serviceNameSplit = serviceName.split(":");
    var url = "/invoke/" + serviceNameSplit[0] + "/" + serviceNameSplit[1];
    if (typeof details === "undefined" || details === null) {
        invoke(url, null, serviceName, checkFunctionCallback, getStatusValueCallback);
    } else {
        invoke(url, null, serviceName, checkFunctionCallback, getStatusValueCallback, details.description, details.linkHref);
    }
}


// function handleResponse(id, isOk, jsonResponse, url, statusValue);

function invoke(url, data, id, checkFunctionCallback, getStatusValueCallback, description, linkHref) {

    if (description !== null && linkHref !== null) {
        createDashbaordElement(id, description, linkHref);
    }
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

function createDashbaordElement(id, description, linkHref) {
    var $statusTr = $("[id='" + id + "']");
    if ($statusTr.length === 0) {
        alert("did not find tr for id: " + id);
    }
    if (typeof description === "undefined") {
        var desc = $statusTr.attr("description");
        if (desc !== null) {
            description = desc;
        }
    }
    if (typeof linkHref === "undefined") {
        linkHref = $statusTr.attr("linkHref");
    }


    $tdState = $("<td/>").addClass("is-state-unknown");
    $spanStatusValue = $("<span/>").addClass("status-value");
    $tdDesc = $("<td/>").addClass("status-description").text(description + "=").append($spanStatusValue);
    $tdStatusIcon = $("<td/>").addClass("status-details icons");
    $tdEdit = $("<td/>").addClass("status-edit");
    $spanEditIcon = $("<span/>").text("mode_edit");
    if (typeof linkHref !== "undefined" && linkHref !== null) {
        $editLink = $('<a>', {
            title: 'Edit',
            href: linkHref,
            target: '_parent'
        });
        // $editLink = $("<a href='" + linkHref + "' target='_parent'");
        $spanEditIcon.addClass("material-icons");
        $editLink.append($spanEditIcon);
        $tdEdit.append($editLink);
    } else {
        $spanEditIcon.addClass("is-inactive material-icons");
        $tdEdit.append($spanEditIcon);
    }
    $statusTr.append($tdState);
    $statusTr.append($tdDesc);
    $statusTr.append($tdStatusIcon);
    $statusTr.append($tdEdit);
}

function handleResponse(id, isOk, jsonResponse, url, statusValue) {

    var $statusTr = $("[id='" + id + "']");
    if ($statusTr.length === 0) {
        alert("did not find tr");
    }
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

function handleResponse2(id, isOk, jsonResponse, url, statusValue) {
    var $statusTr = $("[id='" + id + "']");
    if ($statusTr.length === 0) {
        alert("did not find tr");
    }
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
