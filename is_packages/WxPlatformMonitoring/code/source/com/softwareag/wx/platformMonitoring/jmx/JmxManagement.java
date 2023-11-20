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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.Session;
import com.wm.app.b2b.server.User;
import com.wm.app.b2b.server.ns.Interface;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSService;

public class JmxManagement {

	Logger logger = LogManager.getLogger(this.getClass());
	// we need the session when we want to invoke an IS Service
	Session session;
	// we need the user when we want to invoke an IS Service
	User user;
	String packageName;
	// the global standard MBean server
	MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
	
	public JmxManagement(com.wm.app.b2b.server.Session session, User user) {
		this.session = session;
		this.user = user;		
	}
	
	public void exposeServicesInFolder(String packageName, String folderName)
			throws com.wm.app.b2b.server.ServiceException {
		logger.debug("Init JMX for package " + packageName + " and folder " + folderName);
		
		this.packageName = packageName;
		
		this.scanFolder(folderName, false);
	}
	
	public void exposeServicesInFolderRecursively(String packageName, String folderName)
			throws com.wm.app.b2b.server.ServiceException {
		logger.debug("Init JMX for package " + packageName + " and folder " + folderName);
		
		this.packageName = packageName;
		
		this.scanFolder(folderName, true);
	}

	public void exposeService(String serviceName)
			throws com.wm.app.b2b.server.ServiceException {
		logger.debug("Exposing MBean for service " + serviceName);
		// get service details like package and folder name
		NSService service = (NSService)com.wm.app.b2b.server.ns.Namespace.current().getNode(serviceName);
		String packageName = service.getPackage().getName();
		String folderName = service.getNSName().getInterfaceNSName().getFullName();
		
		this.packageName = packageName;
		
		// create list to contain the single service
		List<String> services = new ArrayList<String>();
		services.add(serviceName);
		// register MBean for this service only
		registerMbeanForServices(folderName, services);
	}

	public void scanFolder(String folderName, boolean recursive) throws com.wm.app.b2b.server.ServiceException {

		logger.trace("Scaning folder folder " + folderName);

		// get all services and sub-folders for the given folder
		List<String> services = new ArrayList<String>();
		NSNode node = com.wm.app.b2b.server.ns.Namespace.current().getNode(folderName);
		if (node instanceof com.wm.app.b2b.server.ns.Interface) {
			Interface ifc = (com.wm.app.b2b.server.ns.Interface) node;
			NSNode[] childNodes = ifc.getNodes();
			for (NSNode child : childNodes) {
				if (child instanceof Interface && recursive) {
					// we have a sub-folder, recursively scann this folder
					scanFolder(child.getNSName().getFullName(), recursive);
				} else if (child instanceof NSService) {
					// we have a service, store in the list so that we can
					// create an MBean for the list of all services
					services.add(child.getNSName().getFullName());
				}
			}
		}
		registerMbeanForServices(folderName, services);
	}
	
	private void registerMbeanForServices(String folderName, List<String> services) throws ServiceException {
		try {
			// create the JMX ObjectName
			String objName = packageName + ":folder=" + folderName;
			ObjectName objectName = new ObjectName(objName);
			logger.trace("Using object name" + objName + " for package " + packageName + " and folder " + folderName);

			// check if the object has been registered already.
			if (this.mBeanServer.queryMBeans(objectName, null).size() != 0) {
				// unregister the old object (mbean)
				this.mBeanServer.unregisterMBean(objectName);
			}
			// register the new mbean
			this.mBeanServer.registerMBean(new ServicesDynamicMBean(packageName, folderName, services, session, user),
					objectName);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
				| MalformedObjectNameException | InstanceNotFoundException e1) {
			e1.printStackTrace();
			String message = "Could not init MBean for package " + packageName + " and folder " + folderName + ": "
					+ e1;
			logger.error(message);
			// rethrow exception
			throw new ServiceException(message);
		}
	}

}
