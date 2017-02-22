package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-22 16:13:15 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import com.softwareag.wx.platformMonitoring.jmx.agent.WxPlatformMonitoringMXBean;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.User;
// --- <<IS-END-IMPORTS>> ---

public final class jmx

{
	// ---( internal utility methods )---

	final static jmx _instance = new jmx();

	static jmx _newInstance() { return new jmx(); }

	static jmx _cast(Object o) { return (jmx)o; }

	// ---( server methods )---




	public static final void registerAgent (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerAgent)>> ---
		// @sigtype java 3.5
		if (agent == null) {
			java.io.File configFile = new java.io.File(
					com.wm.app.b2b.server.ServerAPI.getPackageConfigDir("WxPlatformMonitoring"),
					"jmx-wxplatformmonitoring-agent.properties");
			String pathToFile = getPathForConfigFile(configFile);
			pathToFile = "jmx-wxplatformmonitoring-agent.properties";
			try {
				agent = new com.softwareag.wx.platformMonitoring.jmx.agent.WxPlatformMonitoringJmxAgent(pathToFile);
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				String n = String.format("%s,group=%s,name=%s", "com.softwareag.wx.is.jmx:type=JMXMonitor",
						"IntegrationServer", "WxPlatformMonitoring");
				n = String.format("%s,group=%s,name=%s", "com.softwareag.wx.is.jmx:type=JMXMonitor",
						"WxPlatformMonitoring", "IntegrationServer");
				ObjectName name = new ObjectName(n);
				Set<ObjectInstance> oSet = mbs.queryMBeans(name, null);
				if (oSet.size() != 0 ) {
					mbs.unregisterMBean(name);
				}
		
				mbs.registerMBean(new WxPlatformMonitoringMXBean(), name);
				mbs.getMBeanCount();
		
				// mbs.registerMBean(paramObject, paramObjectName);
			} catch (com.softwareag.wx.is.jmx.agent.common.CustomAgentException c) {
				throw new ServiceException("Could not register WxPlatformMonitoring JMX Agent: " + c);
			} catch (Exception e) {
				throw new ServiceException("Could not register WxPlatformMonitoring JMX Agent: " + e);
			}
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void spring (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(spring)>> ---
		// @sigtype java 3.5
		//		com.softwareag.wx.platformMonitoring.jmx.spring.JmxManagement j = new com.softwareag.wx.platformMonitoring.jmx.spring.JmxManagement();
		//		j.init();
		//		ClassLoader cl1 = Service.class.getClassLoader();
		//		ClassLoader cl2 = Server.class.getClassLoader();
		//		ClassLoader cl3 = Service.getSession().getClass().getClassLoader();
		//		     Thread currentThread = Thread.currentThread();
		//		/* --> */     ClassLoader cl4 = currentThread.getContextClassLoader();
		//		ServerClassLoader cl5 = ServerClassLoader.getCurrent();
		//		
		//		
		//		/* 641 */       currentThread.setContextClassLoader(ServerClassLoader.getPackageLoader("WxPlatformMonitoring"));
		//		/* --> */     ClassLoader cl6 = currentThread.getContextClassLoader(); 
		//		
		com.softwareag.wx.platformMonitoring.jmx.JmxManagement j = new com.softwareag.wx.platformMonitoring.jmx.JmxManagement();
		com.wm.app.b2b.server.Session session = Service.getSession();
		InvokeState state = InvokeState.getCurrentState();
		j.init("wx.platformMonitoring.pub", session, state, InvokeState.getCurrentSocket().getLocalPort());		
				
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static com.softwareag.wx.platformMonitoring.jmx.agent.WxPlatformMonitoringJmxAgent agent = null;
	
	private static String getPathForConfigFile(File file) throws ServiceException {
		java.nio.file.Path pathToFile = java.nio.file.Paths.get(file.getAbsolutePath());
		java.nio.file.Path parent = pathToFile.getParent();
		List<String> pathElements = new ArrayList<String>();
		pathElements.add(pathToFile.getName(pathToFile.getNameCount()-1).toString());
		
		while(true) {
			boolean isSymbolicLink =
			    java.nio.file.Files.isSymbolicLink( parent);
			if( isSymbolicLink ) {
				try {
					File f = java.nio.file.Files.readSymbolicLink(parent).toFile();
					for( int i=pathElements.size()-1; i>=0; i--) {
						f = new File(f, pathElements.get(i));
					}
					return f.getAbsolutePath();
				} catch (java.io.IOException x) {
					throw new ServiceException("Could not get path for file " + file.getAbsolutePath());
				}
			} else {
				if( parent.getNameCount() == 0 ) {
					break;
				}
				pathElements.add(pathToFile.getName(parent.getNameCount()-1).toString());
				parent = parent.getParent();
				if( parent == null ) {
					break;
				}
			}
		}
		
		return file.getAbsolutePath();
	}
	// --- <<IS-END-SHARED>> ---
}

