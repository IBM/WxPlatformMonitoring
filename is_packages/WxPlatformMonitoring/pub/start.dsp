<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<title>Bootstrap 101 Template</title>

	<!-- Bootstrap -->
	<!-- <link href="css/bootstrap/bootstrap.min.css" rel="stylesheet"> -->
	<link href="css/pure-nr-min.css" rel="stylesheet">
	<link href="css/wxplatformmonitoring.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="css/bootstrap/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="css/json-formatter.css">

	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
	<h1>Hello, world!</h1>
	<div class="pure-g">
		<div class="pure-u-1-2">
			<table class="pure-table pure-table-horizontal">
				<thead>
					<tr>
						<th>S</th>
						<th>Check</th>
						<th>Details</th>
						<th>Link</th>
					</tr>
				</thead>
				<tbody>
					<tr id="watt.server.scheduler.threadThrottle">
						<td class="is-state-unknown"></td>
						<td class="status-description">watt.server.scheduler.threadThrottle=<span class="status-value"></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr id="watt.wx.platformmonitoring.server.quiesce">
						<td class="is-state-unknown"></td>
						<td class="status-description">watt.wx.platformmonitoring.server.quiesce=<span class="status-value"></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr id="wx.platformMonitoring.pub.onedata:checkServerConnection">
						<td class="is-state-unknown"></td>
						<td class="status-description">ODE URL=<span class="status-value"></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr id="wx.platformMonitoring.pub.trigger:listJmsTriggers">
						<td class="is-state-unknown"></td>
						<td class="status-description">Disabled JMS Triggers=<span class="status-value"></span></td>
						<td></td>
						<td></td>
					</tr>
					<tr id="wx.platformMonitoring.pub.scheduler:listScheduledServices">
						<td class="is-state-unknown"></td>
						<td class="status-description">Disabled Scheduled Services=<span class="status-value"></span></td>
						<td class="status-details">
							<div style="display:none;"></div>
							<button>D</button>
						</td>
						<td></td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="pure-u-1-2">
			<table class="pure-table pure-table-horizontal">
				<thead>
					<tr>
						<th>#</th>
						<th>Make</th>
						<th>Model</th>
						<th>Year</th>
					</tr>
				</thead>

				<tbody>
					<tr>
						<td>1</td>
						<td>Honda</td>
						<td>Accord</td>
						<td>2009</td>
					</tr>

					<tr>
						<td>2</td>
						<td>Toyota</td>
						<td>Camry</td>
						<td>2012</td>
					</tr>

					<tr>
						<td>3</td>
						<td>Hyundai</td>
						<td>Elantra</td>
						<td>2010</td>
					</tr>
				</tbody>
			</table>
		</div>
		<p>Content here. <a class="_xalert" href=#>Alert!</a></p>

		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
		<script src="js/jquery-3.1.1.min.js"></script>
		<!-- Include all compiled plugins (below), or include individual files as needed -->
		<script src="js/bootstrap.min.js"></script>
		<script src="js/bootbox.min.js"></script>
		<script src="js/wxplatformmonitoring.js"></script>
		<script src="js/json-formatter.js"></script>
		<script type="text/javascript">

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
		</script>
</body>

</html>
