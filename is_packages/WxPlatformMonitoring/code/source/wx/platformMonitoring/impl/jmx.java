package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-22 20:41:17 CET
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
import com.webmethods.core.util.Logger;
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




	public static final void registerMBean (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerMBean)>> ---
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
		
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put( inputCursor, "propertyName", "watt.wx.platformmonitoring.jmx.enable" );
		IDataUtil.put( inputCursor, "defaultValue", "false" );
		inputCursor.destroy();
		
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "pub.utils", "getServerProperty", input );
		}catch( Exception e){}
		IDataCursor outputCursor = output.getCursor();
		String	enable = IDataUtil.getString( outputCursor, "propertyValue" );
		outputCursor.destroy();
		
		if( enable.equals("true") ) {
			logger.info("Loading WxPlatformMonitoring MBeans...");
			com.wm.app.b2b.server.Session session = Service.getSession();
			User user = InvokeState.getCurrentUser();
			com.softwareag.wx.platformMonitoring.jmx.JmxManagement jmxManagement = new com.softwareag.wx.platformMonitoring.jmx.JmxManagement(session, user);
			jmxManagement.exposeServicesInFolderRecursively("WxPlatformMonitoring", "wx.platformMonitoring.pub");		
			logger.info("Successfully loaded WxPlatformMonitoring MBeans...");			
		} else {
			logger.info("_NOT_ loading WxPlatformMonitoring MBeans, because ExtendedSettings 'watt.wx.platformmonitoring.jmx.enable' is not set to 'true'!");
		}
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("wx.platformMonitoring.config");
	
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

