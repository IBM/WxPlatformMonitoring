package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-25 13:21:15 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import sun.tools.jar.resources.jar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSRecordRef;
import com.wm.lang.ns.NSField;
// --- <<IS-END-IMPORTS>> ---

public final class config

{
	// ---( internal utility methods )---

	final static config _instance = new config();

	static config _newInstance() { return new config(); }

	static config _cast(Object o) { return (config)o; }

	// ---( server methods )---




	public static final void getAdapterMonitoringConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getAdapterMonitoringConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required adapterMonitoringConfig wx.platformMonitoring.impl.adapter:AdapterMonitoringConfig
		JsonValue configAdapters =getConfig("adapters.json");
		JsonObject adapters = configAdapters.asObject().get("adapters").asObject();
		JsonArray jdbcConnections = adapters.get("jdbc").asObject().get("connections").asArray();
		JsonObject jSap = adapters.get("sap").asObject();
		JsonArray jSapConnections = jSap.get("connections").asArray();
		JsonArray jSapNotifications = jSap.get("notifications").asArray();
		JsonArray jSapListeners = jSap.get("listeners").asArray();
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		// adapterMonitoringConfig
		IData	adapterMonitoringConfig = IDataFactory.create();
		IDataCursor adapterMonitoringConfigCursor = adapterMonitoringConfig.getCursor();
		
		// adapterMonitoringConfig.jdbc
		IData	jdbc = IDataFactory.create();
		IDataCursor jdbcCursor = jdbc.getCursor();
		String[]	connections = new String[jdbcConnections.size()];
		for( int i=0; i<connections.length; i++ ) {
			connections[i] = jdbcConnections.get(i).asString();
		}
		IDataUtil.put( jdbcCursor, "connections", connections );
		jdbcCursor.destroy();
		IDataUtil.put( adapterMonitoringConfigCursor, "jdbc", jdbc );
		// adapterMonitoringConfig.sap
		IData	sap = IDataFactory.create();
		IDataCursor sapCursor = sap.getCursor();
		String[]	sapConnections = new String[jSapConnections.size()];
		String[]	sapNotifications = new String[jSapNotifications.size()];
		String[]	sapListeners = new String[jSapListeners.size()];
		for( int i=0; i<sapConnections.length; i++ ) {
			sapConnections[i] = jSapConnections.get(i).asString();
		}
		for( int i=0; i<sapNotifications.length; i++ ) {
			sapNotifications[i] = jSapNotifications.get(i).asString();
		}
		for( int i=0; i<sapListeners.length; i++ ) {
			sapListeners[i] = jSapListeners.get(i).asString();
		}
		IDataUtil.put( sapCursor, "connections", sapConnections );
		IDataUtil.put( sapCursor, "notifications", sapNotifications );
		IDataUtil.put( sapCursor, "listeners", sapListeners );
		sapCursor.destroy();
		IDataUtil.put( adapterMonitoringConfigCursor, "sap", sap );
				
		adapterMonitoringConfigCursor.destroy();
		IDataUtil.put( pipelineCursor, "adapterMonitoringConfig", adapterMonitoringConfig );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getBrokerConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getBrokerConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required brokerConnectionData wx.platformMonitoring.impl.broker:BrokerConnectionData
	JsonValue configBroker =getConfig("brokerConfig.json");
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
		JsonValue configBrokerMonitoring =getConfig("brokerMonitoring.json");
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



	public static final void getConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getConfig)>> ---
		// @sigtype java 3.5
		// [i] field:0:required jsonConfig
		// [o] record:0:required config
		IDataCursor pipelineCursor = pipeline.getCursor();
		String jsonConfig = IDataUtil.getString(pipelineCursor, "jsonConfig");
		if( jsonConfig == null || "".equals(jsonConfig) ) {
			throw new ServiceException("jsonConfig must not be empty");
		}
		JsonValue configUm =getConfig(jsonConfig);
		IData umDoc = IDataFactory.create();
		iterateJson(configUm.asObject().get("um").asObject(), umDoc.getCursor());
		IDataUtil.put(pipelineCursor, "config", umDoc);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getInterfaceConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getInterfaceConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required interfacesMonitoringConfig wx.platformMonitoring.impl.interfaces:InterfacesMonitoringConfig
		JsonArray jInterfaces = getConfig("interfaces.json").asObject().get("interfaces").asArray();
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// interfacesMonitoringConfig
		IData	interfacesMonitoringConfig = IDataFactory.create();
		IDataCursor interfacesMonitoringConfigCursor = interfacesMonitoringConfig.getCursor();
		
		// interfacesMonitoringConfig.interfaces
		IData[]	interfaces = new IData[jInterfaces.size()];
		for( int i=0; i<jInterfaces.size(); i++ ) {
			interfaces[i] = IDataFactory.create();
			IDataCursor interfacesCursor = interfaces[i].getCursor();
			IDataUtil.put( interfacesCursor, "interface", jInterfaces.get(i).asObject().get("interface").asString() );
			IDataUtil.put( interfacesCursor, "interval", jInterfaces.get(i).asObject().get("interval").asString());
			interfacesCursor.destroy();
		}
		IDataUtil.put( interfacesMonitoringConfigCursor, "interfaces", interfaces );
		interfacesMonitoringConfigCursor.destroy();
		IDataUtil.put( pipelineCursor, "interfacesMonitoringConfig", interfacesMonitoringConfig );
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
		JsonValue interfaces = getConfig("interfaces.json");
		for (JsonValue i : interfaces.asArray()) {
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



	public static final void getOnedataConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getOnedataConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required onedataConnectionData wx.platformMonitoring.impl.onedata:OnedataConnectionData
		JsonValue configOnedata = getConfig("onedata.json");
		JsonObject onedata = configOnedata.asObject().get("onedata").asObject().get("config").asObject();
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		// onedataConnectionData
		IData	onedataConnectionData = IDataFactory.create();
		IDataCursor onedataConnectionDataCursor = onedataConnectionData.getCursor();
		IDataUtil.put( onedataConnectionDataCursor, "host", onedata.get("host").asString() );
		IDataUtil.put( onedataConnectionDataCursor, "port", onedata.get("port").asString( ));
		IDataUtil.put( onedataConnectionDataCursor, "restPath", onedata.get("restPath").asString( ));
		onedataConnectionDataCursor.destroy();
		IDataUtil.put( pipelineCursor, "onedataConnectionData", onedataConnectionData );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getPortsConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPortsConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required portMonitoringConfig wx.platformMonitoring.impl.port:PortMonitoringConfig
	JsonValue configPorts = getConfig("ports.json");
	JsonObject jPort = configPorts.asObject().get("ports").asObject();
	JsonArray jPorts = jPort.get("ports").asArray();
	JsonArray jPackages = jPort.get("packages").asArray();
	JsonArray jPortAliases = jPort.get("portAliases").asArray();
	// pipeline
	IDataCursor pipelineCursor = pipeline.getCursor();
	// schedulerMonitoringConfig
	IData	portMonitoringConfig = IDataFactory.create();
	IDataCursor portMonitoringConfigCursor = portMonitoringConfig.getCursor();
	
	String[]	packages = new String[jPackages.size()];
	for( int i=0; i<packages.length; i++ ) {
		packages[i] = jPackages.get(i).asString();
	}
	IDataUtil.put( portMonitoringConfigCursor, "packages", packages );
	
	String[]	ports = new String[jPorts.size()];
	for( int i=0; i<ports.length; i++ ) {
		ports[i] = jPorts.get(i).asString();
	}
	IDataUtil.put( portMonitoringConfigCursor, "ports", ports );
	
	String[]	portAliases = new String[jPortAliases.size()];
	for( int i=0; i<portAliases.length; i++ ) {
		portAliases[i] = jPortAliases.get(i).asString();
	}
	IDataUtil.put( portMonitoringConfigCursor, "portAliases", portAliases );
	
	portMonitoringConfigCursor.destroy();
	IDataUtil.put( pipelineCursor, "portMonitoringConfig", portMonitoringConfig );
	pipelineCursor.destroy();

	
		// --- <<IS-END>> ---

                
	}



	public static final void getSchedulersConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSchedulersConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required schedulerMonitoringConfig wx.platformMonitoring.impl.scheduler:SchedulerMonitoringConfig
	JsonValue configSchedulers = getConfig("schedulers.json");
	JsonArray jSchedulers = configSchedulers.asObject().get("schedulers").asArray();
	// pipeline
	IDataCursor pipelineCursor = pipeline.getCursor();
	// schedulerMonitoringConfig
	IData	schedulerMonitoringConfig = IDataFactory.create();
	IDataCursor schedulerMonitoringConfigCursor = schedulerMonitoringConfig.getCursor();
	String[]	schedulers = new String[jSchedulers.size()];
	for( int i=0; i<schedulers.length; i++ ) {
		schedulers[i] = jSchedulers.get(i).asString();
	}
	IDataUtil.put( schedulerMonitoringConfigCursor, "schedulers", schedulers );
	schedulerMonitoringConfigCursor.destroy();
	IDataUtil.put( pipelineCursor, "schedulerMonitoringConfig", schedulerMonitoringConfig );
	pipelineCursor.destroy();

	
		// --- <<IS-END>> ---

                
	}



	public static final void getTriggersConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getTriggersConfig)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required triggerMonitoringConfig wx.platformMonitoring.impl.trigger:TriggerMonitoringConfig
		JsonValue configTriggers = getConfig("triggers.json");
		JsonObject jTriggers = configTriggers.asObject().get("triggers").asObject();
		JsonArray jMessagingTriggers = jTriggers.get("messagingTriggers").asArray();
		JsonArray jJmsTriggers = jTriggers.get("jmsTriggers").asArray();
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		// schedulerMonitoringConfig
		IData	triggerMonitoringConfig = IDataFactory.create();
		IDataCursor triggerMonitoringConfigCursor = triggerMonitoringConfig.getCursor();
		String[]	messagingTriggers = new String[jMessagingTriggers.size()];
		for( int i=0; i<messagingTriggers.length; i++ ) {
			messagingTriggers[i] = jMessagingTriggers.get(i).asString();
		}
		IDataUtil.put( triggerMonitoringConfigCursor, "messagingTriggers", messagingTriggers );
		String[]	jmsTriggers = new String[jJmsTriggers.size()];
		for( int i=0; i<jmsTriggers.length; i++ ) {
			jmsTriggers[i] = jJmsTriggers.get(i).asString();
		}
		IDataUtil.put( triggerMonitoringConfigCursor, "jmsTriggers", jmsTriggers );
		triggerMonitoringConfigCursor.destroy();
		IDataUtil.put( pipelineCursor, "triggerMonitoringConfig", triggerMonitoringConfig );
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void reloadConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(reloadConfig)>> ---
		// @sigtype java 3.5
		_configs.clear();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static void iterateJson(JsonObject jObj, IDataCursor pipelineCursor) {
			for( String childName: jObj.names() ) {
				JsonValue child = jObj.get(childName);
				if( child.isObject() ) {
					IData childDoc = IDataFactory.create();
					IDataUtil.put(pipelineCursor, childName, childDoc);
					iterateJson(child.asObject(), childDoc.getCursor());
				} else if( child.isArray() ) {
					Object[] array = iterateJsonArray(child.asArray());
					IDataUtil.put(pipelineCursor, childName, array);
				} else if( child.isString() ) {
					IDataUtil.put(pipelineCursor, childName, child.asString());
				}
			}
	}
	
	static Object[] iterateJsonArray(JsonArray jArr) {
		Object[] array = new Object[jArr.size()];
		IData[] docArr = new IData[jArr.size()];
		String[] stringArr = new String[jArr.size()];
		boolean isStringArr = false;
		for( int i=0; i<jArr.size(); i++) {
			JsonValue jElem = jArr.get(i);
			if( jElem.isObject() ) {
				IData doc = IDataFactory.create();
				docArr[i] = doc;
				iterateJson(jElem.asObject(), doc.getCursor());
			} else if( jElem.isString() ) {
				stringArr[i] = jElem.asString();
				isStringArr = true;
			}
		}
		if( isStringArr ) {
			return stringArr;
		} else {
			return docArr;
		}
	}
	
	static Object[] iterateJsonArrayx(JsonArray jArr) {
		Object[] array = new Object[jArr.size()];
		for( int i=0; i<jArr.size(); i++) {
			JsonValue jElem = jArr.get(i);
			if( jElem.isObject() ) {
				IData doc = IDataFactory.create();
				array[i] = doc;
				iterateJson(jElem.asObject(), doc.getCursor());
			} else if( jElem.isString() ) {
				array[i] = jElem.asString();
			}
		}
		return array;
	}
	/*static void iterateJsonx(JsonValue jValue, String name, IData pipeline) {
		IDataCursor pipelineCursor = pipeline.getCursor();
		if( jValue.isObject() ) {
			IData doc = IDataFactory.create();
			IDataUtil.put(pipelineCursor, name, doc);
			for( String childName: jValue.asObject().names() ) {
				JsonValue child = jValue.asObject().get(childName);
				iterateJson(child, childName, doc);
			}
		} else if ( jValue.isArray() ) {
			JsonArray jArr = jValue.asArray();
			for( int i=0; i<jArr.size(); i++ ) {
				JsonValue child = jArr.get(i);
				if( child.isObject() ) {
					
				} else if( child.isString() ) {
					
				}
			}
			
			
			IData[] docArr = new IData[jArr.size()];
			IDataUtil.put(pipelineCursor, name, docArr);
			for( int i=0; i<docArr.length; i++ ) {
				docArr[i] = IDataFactory.create();
				
			}
		} else if( jValue.isString() ) {
			IDataUtil.put(pipelineCursor, name, jValue.asString());
		}
	}
	*/
	
	static java.util.Map<String, JsonValue> _configs = new java.util.HashMap<String, JsonValue>();
	
	private static JsonValue getConfig(String configFileName) throws ServiceException {
		if( !_configs.containsKey(configFileName) ) {
			File configFile = new File(ServerAPI.getPackageConfigDir("WxPlatformMonitoring"),
					configFileName);
			java.io.Reader reader;
			try {
				reader = new FileReader(configFile);
				_configs.put(configFileName, Json.parse(reader));
			} catch (FileNotFoundException fnfe) {
				// TODO Auto-generated catch block
				fnfe.printStackTrace();
				throw new ServiceException("FileNotFoundException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFileName + ": " + fnfe);
			} catch( IOException ioe ) {
				throw new ServiceException("IOException: Could not read standard json interfaces config file from WxPlatformMonitoring/config/" + configFileName + ": " + ioe);
			}
		}
		return _configs.get(configFileName);
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

