// alert("inside");


// $(window).on("load", loadSettings());


function loadSettings() {
  alert("loadSettings");
  evaluateExtendedSettings("watt.server.scheduler.threadThrottle", function checkFunction(result) {
    if (result.propertyValue > 0) {
      return true;
    }
    return false;
  });
  evaluateExtendedSettings("watt.wx.platformmonitoring.server.quiesce", function checkFunction(result) {
    if (result.propertyValue == "false") {
      return true;
    }
    return false;
  });
  evaluteService("wx.platformMonitoring.pub.onedata:checkServerConnection", function checkFunction(result) {
    if (result.success == "true") {
      return true;
    }
    return false;
  }, function getStatusValue(result) {
    return result.odeUrl;
  });

  evaluteService("wx.platformMonitoring.pub.trigger:listJmsTriggers", function checkFunction(result) {
    if (result.nrDisabled == 0) {
      return true;
    }
    return false;
  }, function getStatusValue(result) {
    return result.nrDisabled;
  });

  evaluteService("wx.platformMonitoring.pub.scheduler:listScheduledServices", function checkFunction(result) {
    if (result.nrDisabled == 0) {
      return true;
    }
    return false;
  }, function getStatusValue(result) {
    return result.nrDisabled;
  });

  evaluteService("wx.platformMonitoring.pub.server:getCountCurrentlyRunningServices", function checkFunction(result) {
    return true;
  }, function getStatusValue(result) {
    return result.currentlyRunningServicesCount;
  });

  evaluteService("wx.platformMonitoring.pub.um.monitoring:listJmsQueues", function checkFunction(result) {
    return result.nrNOk > 0 ? false : true;
  }, function getStatusValue(result) {
    return result.nrNOk;
  });

  evaluteService("wx.platformMonitoring.pub.um.monitoring:listMessagingQueues", function checkFunction(result) {
    return result.nrNOk > 0 ? false : true;
  }, function getStatusValue(result) {
    return result.nrNOk;
  });

  evaluteService("wx.platformMonitoring.pub.ports:listPorts", function checkFunction(result) {
    var isOk = true;
    $.each(result.portList, function(index, value) {
        if( value.enabled == "false" ) {
          isOk = false;
        }
    });
    return isOk;
  }, function getStatusValue(result) {
    var count = 0;
    $.each(result.portList, function(index, value) {
        if( value.enabled == "false" ) {
          count++;
        }
    });
    return count;
  });
}

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
    if( $statusTr.length === 0 ) {
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
