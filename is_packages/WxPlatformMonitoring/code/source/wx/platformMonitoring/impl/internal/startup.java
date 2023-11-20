package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.ServerAPI;
import com.wm.lang.ns.NSName;
import com.softwareag.util.IDataMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.message.StringMapMessage;
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
		// [i] field:0:required packageName
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
}

