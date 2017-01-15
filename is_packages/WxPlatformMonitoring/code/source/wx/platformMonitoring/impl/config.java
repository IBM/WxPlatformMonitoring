package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 15:10:24 CET
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
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
import com.wm.data.IDataFactory;
// --- <<IS-END-IMPORTS>> ---

public final class config

{
	// ---( internal utility methods )---

	final static config _instance = new config();

	static config _newInstance() { return new config(); }

	static config _cast(Object o) { return (config)o; }

	// ---( server methods )---




	public static final void getBrokerConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getBrokerConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required brokerConnectionData wx.platformMonitoring.impl.broker:BrokerConnectionData
	if( configBroker == null ) {
		configBroker = getConfig("brokerConfig.json");
	}
	JsonObject brokerConfig = configBroker.asObject().get("broker").asObject().get("config").asObject();
	// pipeline
	IDataCursor pipelineCursor = pipeline.getCursor();
	// brokerConnectionData
	IData	brokerConnectionData = IDataFactory.create();
	IDataCursor brokerConnectionDataCursor = brokerConnectionData.getCursor();
	IDataUtil.put( brokerConnectionDataCursor, "brokerURI", brokerConfig.get("brokerURI").asString() );
	IDataUtil.put( brokerConnectionDataCursor, "brokerName", brokerConfig.get("brokerName").asString() );
	IDataUtil.put( brokerConnectionDataCursor, "brokerAppName", brokerConfig.get("brokerAppName").asString() );
	IDataUtil.put( brokerConnectionDataCursor, "brokerClientGroup", brokerConfig.get("brokerClientGroup").asString() );
	brokerConnectionDataCursor.destroy();
	IDataUtil.put( pipelineCursor, "brokerConnectionData", brokerConnectionData );
	pipelineCursor.destroy();
	
		// --- <<IS-END>> ---

                
	}



	public static final void getBrokerMonitoringConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getBrokerMonitoringConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required brokerMonitoringConfig wx.platformMonitoring.impl.broker:BrokerMonitoringData
		if( configBrokerMonitoring == null ) {
			configBrokerMonitoring = getConfig("brokerMonitoring.json");
		}
		JsonObject jMonitoring = configBrokerMonitoring.asObject().get("broker").asObject().get("monitoring").asObject();
		JsonArray jClients = jMonitoring.get("clients").asArray();
		JsonObject jAllClients = jMonitoring.get("allClients").asObject();
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// brokerMonitoringConfig
		IData	brokerMonitoringConfig = IDataFactory.create();
		IDataCursor brokerMonitoringConfigCursor = brokerMonitoringConfig.getCursor();
		
		// brokerMonitoringConfig.clients
		IData[]	clients = new IData[jClients.size()];
		for( int i=0; i<clients.length; i++) {
			clients[i] = IDataFactory.create();
			IDataCursor clientsCursor = clients[i].getCursor();
			JsonObject jClient = jClients.get(i).asObject();
			IDataUtil.put( clientsCursor, "clientName", jClient.get("clientName").asString() );
			IDataUtil.put( clientsCursor, "maxElementsQueuedThreshold", jClient.get("maxElementsQueued").asString() );
			clientsCursor.destroy();
		}
		IDataUtil.put( brokerMonitoringConfigCursor, "clients", clients );
		
		// brokerMonitoringConfig.allClients
		IData	allClients = IDataFactory.create();
		IDataCursor allClientsCursor = allClients.getCursor();
		IDataUtil.put( allClientsCursor, "queueSizeBytes.threshold.warn", jAllClients.get("queueSizeBytes.threshold.warn").asString() );
		IDataUtil.put( allClientsCursor, "queueSizeBytes.threshold.critical", jAllClients.get("queueSizeBytes.threshold.critical").asString() );
		allClientsCursor.destroy();
		IDataUtil.put( brokerMonitoringConfigCursor, "allClients", allClients );
		brokerMonitoringConfigCursor.destroy();
		IDataUtil.put( pipelineCursor, "brokerMonitoringConfig", brokerMonitoringConfig );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



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



	public static final void reloadConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(reloadConfig)>> ---
		// @sigtype java 3.5
		interfaces = null;
		configBroker = null;
		configBrokerMonitoring = null;
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static JsonArray interfaces = null;
	static JsonValue configBroker = null;
	static JsonValue configBrokerMonitoring = null;
	
	private static JsonValue getConfig(String configFileName) throws ServiceException {
		String configFilename = configFileName;
		File configFile = new File(ServerAPI.getPackageConfigDir("WxPlatformMonitoring"),
				configFilename);
		java.io.Reader reader;
		try {
			reader = new FileReader(configFile);
			return Json.parse(reader);
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
			throw new ServiceException("FileNotFoundException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFilename + ": " + fnfe);
		} catch( IOException ioe ) {
			throw new ServiceException("IOException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFilename + ": " + ioe);
		}
	}
	
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

