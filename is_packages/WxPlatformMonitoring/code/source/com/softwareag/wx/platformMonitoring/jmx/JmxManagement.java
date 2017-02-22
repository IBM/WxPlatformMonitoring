package com.softwareag.wx.platformMonitoring.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.wm.app.b2b.server.InvokeState;

public class JmxManagement {

	public void init(String folderName, com.wm.app.b2b.server.Session session, InvokeState state) {
		String packageName = "WxPlatformMonitoring";
		try {
			ObjectName o = new ObjectName(packageName + "." + folderName + ":name=" + folderName + ",package=" + packageName + ",henning=ich");
			MBeanServer s = ManagementFactory.getPlatformMBeanServer();
			if (s.queryMBeans(o, null).size() != 0) {
				s.unregisterMBean(o);
			}
			s.registerMBean(new ServicesDynamicMBean(packageName, folderName, "wx.platformMonitoring.pub.trigger:getTriggerStatus", session, state), o);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
				| MalformedObjectNameException | InstanceNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("NOT COMPLIANT: " + e1);
		}
	}

}
