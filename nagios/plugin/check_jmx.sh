#!/bin/sh
#
# Nagios plugin to monitor Java JMX (http://java.sun.com/jmx) attributes and operations.
#
RDIR=`dirname $0`
java -cp $RDIR/WxPlatformMonitoring_NagiosPlugin.jar om.softwareag.wx.platformMonitoring.nagios.JMXQuery $@
