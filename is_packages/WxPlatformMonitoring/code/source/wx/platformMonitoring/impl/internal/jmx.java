package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-03-15 12:38:54 CET
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
		com.wm.app.b2b.server.Session session = Service.getSession();
		User user = InvokeState.getCurrentUser();
		com.softwareag.wx.platformMonitoring.jmx.JmxManagement jmxManagement = new com.softwareag.wx.platformMonitoring.jmx.JmxManagement(session, user);
		jmxManagement.exposeServicesInFolderRecursively("WxPlatformMonitoring", "wx.platformMonitoring.pub");		
		logger.info("Successfully loaded WxPlatformMonitoring MBeans...");			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("wx.platformMonitoring.config");
	
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

