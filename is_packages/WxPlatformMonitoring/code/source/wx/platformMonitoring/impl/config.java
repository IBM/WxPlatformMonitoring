package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-13 19:43:34 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class config

{
	// ---( internal utility methods )---

	final static config _instance = new config();

	static config _newInstance() { return new config(); }

	static config _cast(Object o) { return (config)o; }

	// ---( server methods )---




	public static final void getIntervalForInterface (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getIntervalForInterface)>> ---
		// @sigtype java 3.5
		// [i] field:0:required interface
		// [o] field:0:required interval
		IDataCursor pipelineC = pipeline.getCursor();
		String interfaceName = IDataUtil.getString(pipelineC, "interface");
		if( interfaceName == null || "".equals(interfaceName) ) {
			throw new ServiceException("Provide an interface to lookup the interval for.");
		}
		if( interfaces == null ) {
			callParseConfig();
			if( interfaces == null ) {
				throw new ServiceException("Config cannot be read. Use wx.platformMonitoring.impl.config:parseConfig");
			}
		}
		for (JsonValue i : interfaces) {
		  String name = i.asObject().get("interface").asString();
		  if( name.equals(interfaceName) ) {
			  IDataUtil.put(pipelineC, "interval", i.asObject().get("interval").asString());
			  pipelineC.destroy();
			  return;
		  }
		}
		pipelineC.destroy();
		//		throw new ServiceException("No interface with name " + interfaceName + " defined in configuration!");
		// --- <<IS-END>> ---

                
	}



	public static final void parseConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(parseConfig)>> ---
		// @sigtype java 3.5
		String configFilename = "interfaces.json";
		File configFile = new File(ServerAPI.getPackageConfigDir("WxPlatformMonitoring"),
				configFilename);
		java.io.Reader reader;
		try {
			reader = new FileReader(configFile);
			JsonValue json = Json.parse(reader);
			interfaces = json.asObject().get("interfaces").asArray();
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
			throw new ServiceException("FileNotFoundException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFilename + ": " + fnfe);
		} catch( IOException ioe ) {
			throw new ServiceException("IOException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFilename + ": " + ioe);
		}
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static JsonArray interfaces = null;
	
	private static void callParseConfig() {
	
		// input
		IData input = IDataFactory.create();
	
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wx.platformMonitoring.impl.config", "parseConfig", input );
		}catch( Exception e){}
	
	}
	// --- <<IS-END-SHARED>> ---
}

