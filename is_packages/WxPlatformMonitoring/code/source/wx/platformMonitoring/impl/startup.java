package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-28 09:58:57 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class startup

{
	// ---( internal utility methods )---

	final static startup _instance = new startup();

	static startup _newInstance() { return new startup(); }

	static startup _cast(Object o) { return (startup)o; }

	// ---( server methods )---




	public static final void setDefaultPackage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setDefaultPackage)>> ---
		// @sigtype java 3.5
		String packageName = IDataUtil.getString(pipeline.getCursor(), "packageName");
		System.getProperties().put("watt.server.defaultPackage", packageName);	
		try {
			com.wm.app.b2b.server.Server.saveConfiguration();
		} catch (java.io.IOException io) { 
			// input
			IData input = IDataFactory.create();
			IDataCursor inputCursor = input.getCursor();
			IDataUtil.put( inputCursor, "logMessage", "Error when registering default package " + packageName + " with IS server extended settings: "  + io );
			IDataUtil.put( inputCursor, "severity", "error" );
			inputCursor.destroy();
			try{
				Service.doInvoke( "wx.platformMonitoring.impl.utility.logging", "logMessage", input );
			}catch( Exception e){
				System.out.println("error when logging error message: " + e);
				
			}
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void setupLogger (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setupLogger)>> ---
		// @sigtype java 3.5
		File configFile = new File(ServerAPI.getPackageConfigDir("WxPlatformMonitoring"),
				"log4j.xml");
		if(configFile.exists() && configFile.canRead()) {
		    DOMConfigurator.configureAndWatch(configFile.getAbsolutePath());
		} else {
			throw new ServiceException("Cannot load log4j.xml config from WxPlatformMonitoring config dir");
		}
		// --- <<IS-END>> ---

                
	}
}

