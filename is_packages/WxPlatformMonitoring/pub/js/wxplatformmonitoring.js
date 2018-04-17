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
    var url = createUrlForService(serviceName);
    if (typeof details === "undefined" || details === null) {
        invoke(url, null, serviceName, checkFunctionCallback, getStatusValueCallback);
    } else {
        invoke(url, null, serviceName, checkFunctionCallback, getStatusValueCallback, details.description, details.linkHref);
    }
}

function evaluteServiceAsList(serviceName, listName, checkFunctionCallback, getStatusValueCallback) {
	invokeAsList(createUrlForService(serviceName), serviceName, listName, checkFunctionCallback, getStatusValueCallback);
}

function evaluteServiceAsListWithData(serviceName, data, listName, checkFunctionCallback, getStatusValueCallback) {
	invokeAsListWithData(createUrlForService(serviceName), data, serviceName, listName, checkFunctionCallback, getStatusValueCallback);
}

var createUrlForService = function(serviceName) {
	var serviceNameSplit = serviceName.split(":");
	return "/invoke/" + serviceNameSplit[0] + "/" + serviceNameSplit[1];
}


function invokeAsList(url, id, listName, isOkCallback, getStatusValueCallback) {
	invokeAsListWithData(url, null, id, listName, isOkCallback, getStatusValueCallback);
}
function invokeAsListWithData(url, data, id, listName, isOkCallback, getStatusValueCallback) {
	$.get({
		url: url,
		data: data,
		dataType: "json"
	}).done(function(msg) {
		handleResponseList(id, msg[listName], url, isOkCallback, getStatusValueCallback);
	}).fail(function failure(jqXHR, exception, error) {
		failureFunction(jqXHR, id, url)
	});
}
function invoke(url, data, id, checkFunctionCallback, getStatusValueCallback, description, linkHref) {
	// create the dashbaord td elements
	createDashbaordElement(id, description, linkHref);
	
	$.get({
		url: url,
		data: data,
		dataType: "json"
	}).done(function(msg) {
		var isOk = checkFunctionCallback(msg);
		var statusValue = getStatusValueCallback(msg);
		handleResponse(id, isOk, msg, url, statusValue);
	}).fail(function failure(jqXHR, exception, error) {
		failureFunction(jqXHR, id, url)
	});
}

function invokeSimple(url, data, id, successCallback) {
	$.get({
		url: url,
		data: data,
		dataType: "json"
	}).done(function(msg) {
		successCallback(msg);
	}).fail(function failure(jqXHR, exception, error) {
		failureFunction(jqXHR, id, url)
	});
}

function failureFunction(jqXHR, id, url) {
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

function handleResponseList(id, list, url, isOkCallback, statusCallback) {

	var $tbody = $("[id='" + id + "']");
	if ($tbody.length === 0) {
		alert("did not find tr");
	}
	
    var linkHref = $tbody.attr("linkHref");
    
    $.each(list, function status(index, elem) {
    	var $statusTr = $("<tr/>");
    	var isOk = isOkCallback(elem);
    	var value = elem.name;
    	
    	var $tdState = $("<td/>");    	
        if (isOk) {
        	$tdState.attr("class", "is-state-ok");
        } else {
        	$tdState.attr("class", "is-state-nok");
        }
        
    	$spanStatusValue = $("<span/>").addClass("status-value");
    	$tdDesc = $("<td/>").addClass("status-description").text(value);
    	var $tdReading = $("<td/>").addClass("status-description").text(elem.reading);
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
    	$statusTr.append($tdReading);
    	$statusTr.append($tdEdit);
    	
    	$tbody.append($statusTr);
    	
    	if( statusCallback !== undefined) {
    		statusCallback(value, id, function callback(status, reading) {
    	        if (status === "ok") {
    	        	$tdState.attr("class", "is-state-ok");
    	        } else {
    	        	$tdState.attr("class", "is-state-nok");
    	        }
    	        $tdReading.text(reading);
    		});
    	}
    });    
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
