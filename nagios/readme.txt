-------- Extended JMX plugin for Nagios ---------

Java Extended JMX Nagios plugin enables you to monitor JMX attributes and invoke JMX operations in Nagios.

-------- Installation ---------

Pre-requsisites are:
- Java version >= 1.8 JRE

1) Files from "plugin" folder must be copied to /usr/lib/nagios/plugins (or another - where your nagios plugins located)
2) Make sure that check_jmx executable : chmod a+x /usr/lib/nagios/plugins/check_jmx


-------- Check Installation ---------

Run /usr/lib/nagios/plugins/check_jmx_ext -help to see available options

Try run some command, for example:
/usr/lib/nagios/plugins/check_jmx_ext -U service:jmx:rmi:///jndi/rmi://localhost:1616/jmxrmi -O java.lang:type=Memory -A HeapMemoryUsage -K used -I HeapMemoryUsage -J used -vvvv -w 10000000 -c 100000000

(replace 1616 with your JMX port)

This must return something like:

JMX OK HeapMemoryUsage=7715400{committed=12337152;init=0;max=66650112;used=7715400}

-------- Configuration ---------

To see available options use
/usr/lib/nagios/plugins/check_jmx_ext -help

