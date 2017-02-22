package com.softwareag.wx.platformMonitoring.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ns.Interface;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSService;

public class JmxManagement {

	public void init(String folderName, com.wm.app.b2b.server.Session session, InvokeState state, int localServerPost)
			throws com.wm.app.b2b.server.ServiceException {
		String packageName = "WxPlatformMonitoring";

		List<String> services = new ArrayList<String>();
		NSNode node = com.wm.app.b2b.server.ns.Namespace.current().getNode(folderName);
		if (node instanceof com.wm.app.b2b.server.ns.Interface) {
			Interface ifc = (com.wm.app.b2b.server.ns.Interface) node;
			NSNode[] childNodes = ifc.getNodes();
			for (NSNode child : childNodes) {
				if (child instanceof Interface) {
					init(child.getNSName().getFullName(), session, state, localServerPost);
				} else if (child instanceof NSService) {
					services.add(child.getNSName().getFullName());
				}
			}
		}
		try {
			ObjectName o = new ObjectName(
					packageName + "." + folderName + ":name=" + folderName + ",package=" + packageName);
			o = new ObjectName(packageName + ":folder=" + folderName);
			MBeanServer s = ManagementFactory.getPlatformMBeanServer();
			if (s.queryMBeans(o, null).size() != 0) {
				s.unregisterMBean(o);
			}
			s.registerMBean(
					new ServicesDynamicMBean(packageName, folderName, services, session, state, localServerPost), o);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
				| MalformedObjectNameException | InstanceNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new com.wm.app.b2b.server.ServiceException(
					"Could not init MBean for package " + packageName + " and folder " + folderName + ": " + e1);
		}
	}

}
