package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.softwareag.wx.platformMonitoring.propertyResolver.PropertyResolver;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class conf

{
	// ---( internal utility methods )---

	final static conf _instance = new conf();

	static conf _newInstance() { return new conf(); }

	static conf _cast(Object o) { return (conf)o; }

	// ---( server methods )---




	public static final void getConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getConfig)>> ---
		// @sigtype java 3.5
		// [i] field:0:required assetType
		// [o] record:0:required config
		IDataCursor pipelineCursor = pipeline.getCursor();
		String assetType = IDataUtil.getString(pipelineCursor, "assetType");
		if (assetType == null || "".equals(assetType)) {
			throw new ServiceException("assetType must not be empty");
		}
		JsonValue jConfig = _configurations.get(assetType);
		if (jConfig == null) {
			logger.debug(
					"No config stored for asset of type '" + assetType + "', trying to reload default config dir.");
			loadConfigurationFromDefaultConfigDir();
			jConfig = _configurations.get(assetType);
			if (jConfig == null) {
				logger.debug("No config stored for asset of type '" + assetType + "'.");
				return;
			}
		}
		IData configDoc = parseJson(jConfig.asObject());
		IDataUtil.put(pipelineCursor, "config", configDoc);
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void loadConfigurationFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(loadConfigurationFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required configFile
		// [i] field:0:optional packageName
		IDataCursor pipelineC = pipeline.getCursor();
		String configFile = IDataUtil.getString(pipelineC, "configFile");
		String packageName = IDataUtil.getString(pipelineC, "packageName");
		File cFile = null;
		
		if( packageName != null && !"".equals(packageName) ) {
			File configDir = ServerAPI.getPackageConfigDir(packageName);
			if( !configDir.exists() ) {
				logger.error("Could not load '" + configFile + "' from config dir of package " + packageName + "': config dir does not exist.");				
				return;
			}
			cFile = new File(configDir, configFile);
			if( !cFile.exists() ) {
				logger.error("Could not load '" + configFile + "' from config dir of package '" + packageName + "': config file does not exist.");				
				return;
			}
		} else {
			cFile = new File(configFile);
			if (!cFile.exists()) {
				logger.error("Could not load '" + configFile + ": config file does not exist in directory " + new File(".").getAbsolutePath());				
				return;
			}
		}
		if( cFile != null ) {
			logger.debug("Loading configuration file " + cFile.getAbsolutePath());
			addAssetConfiguration(cFile);
		}			
		// --- <<IS-END>> ---

                
	}



	public static final void loadConfigurationFromJsonString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(loadConfigurationFromJsonString)>> ---
		// @sigtype java 3.5
		// [i] field:0:required jsonString
		// [i] field:0:required assetType
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String jsonString = IDataUtil.getString(pipelineCursor, "jsonString");
		String assetType = IDataUtil.getString(pipelineCursor, "assetType");
		pipelineCursor.destroy();
		
		if (jsonString == null || assetType == null) {
			throw new ServiceException(
					"Provide both a JSON string representing the configuration, as well as the asset type for which this configuration is.");
		}
		
		JsonValue assetConfig = loadJsonConfiguration(jsonString);
		addConfig(assetType, assetConfig);
		
		// --- <<IS-END>> ---

                
	}



	public static final void loadDefaultConfiguration (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(loadDefaultConfiguration)>> ---
		// @sigtype java 3.5
		loadConfigurationFromDefaultConfigDir();
			
		// --- <<IS-END>> ---

                
	}



	public static final void loadPlatformMonitoringConfigForPackage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(loadPlatformMonitoringConfigForPackage)>> ---
		// @sigtype java 3.5
		// [i] field:0:required packageName
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String packageName = IDataUtil.getString(pipelineCursor, "packageName");
		pipelineCursor.destroy();
		
		if (packageName == null) {
			return;
		}
		File configDir = ServerAPI.getPackageConfigDir(packageName);
		if (!configDir.exists()) {
			return;
		}
		String[] configFiles = configDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File paramFile, String paramString) {
				return paramString.equalsIgnoreCase("wxplatformmonitoring.json");
			}
		});
		for (String configFileName : configFiles) {
			File configFile = new File(configDir, configFileName);
			addAssetConfiguration(configFile);
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void resetConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(resetConfig)>> ---
		// @sigtype java 3.5
		_configurations.clear();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static org.apache.logging.log4j.Logger logger =  wx.platformMonitoring.impl.internal.utility.logging.getLogger("wx.platformMonitoring.config");
	static java.util.Map<String, JsonValue> _configurations = new java.util.HashMap<String, JsonValue>();
	
	private static void addAssetConfiguration(String configFileName) throws ServiceException {
		File configFile = new File(configFileName);
		if (!configFile.exists()) {
			throw new ServiceException("Config file " + configFileName + " does not exist. Loading with root dir '"
					+ new File(".").getAbsolutePath() + "'.");
		}
		addAssetConfiguration(configFile);
	}
	
	private static void addAssetConfiguration(File configFile) throws ServiceException {
		JsonValue config = loadJsonConfiguration(configFile);
		List<String> assetTypes = getAssetTypesFromJsonConfig(config);
		logger.debug("Found " + assetTypes.size() + " asset types in config file " + configFile
				+ ". Iterating over each of them.");
		for (String assetType : assetTypes) {
			JsonValue assetConfig = config.asObject().get(assetType);
			addConfig(assetType, assetConfig);
		}
	}
	
	private static JsonValue loadJsonConfiguration(String jsonString) throws ServiceException {
		logger.debug("Getting config from jsonString'");
		try {
			return Json.parse(jsonString);
		} catch (com.eclipsesource.json.ParseException pe) {
			throw new ServiceException("IOException: Could not parse json string: " + pe);
		}
	}
	
	private static JsonValue loadJsonConfiguration(File configFile) throws ServiceException {
		logger.debug("Getting config wx.platformMonitoring.impl.internal.utility.logging.startup_SVC'" + configFile.getAbsolutePath() + "'");
		java.io.Reader reader = null;
		try {
			reader = new FileReader(configFile);
			return Json.parse(reader);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			throw new ServiceException(
					"FileNotFoundException: Could not read json config file '" + configFile + "': " + fnfe);
		} catch (IOException ioe) {
			throw new ServiceException("IOException: Could not read json config file '" + configFile + "': " + ioe);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServiceException("Could not close reader for JSON config");
			}
		}
	}
	
	/**
	 * parse the given json object into an IData object
	 * @param jObj
	 * @return
	 */
	static IData parseJson(JsonObject jObj) {
		// create the element which represent the given JSON object
		IData element = IDataFactory.create();
		IDataCursor elementC = element.getCursor();
		for (String childName : jObj.names()) {
			JsonValue child = jObj.get(childName);
			if (child.isObject()) {
				// recursively parse child object and add to IData as child
				IData childDoc = parseJson(child.asObject());
				IDataUtil.put(elementC, childName, childDoc);
			} else if (child.isArray()) {
				// walkt he child array and create either an IData[] or String[] result array
				Object[] array = iterateJsonArray(child.asArray());
				IDataUtil.put(elementC, childName, array);
			} else if (child.isString()) {
				// just add the string to the IData object
				IDataUtil.put(elementC, childName, resolveValue(child.asString()));
			}
		}
		elementC.destroy();
		return element;
	}
	
	/**
	 * Resolve the token value. If the token is surrounded by ${}, then we send
	 * it to the property resolvers. Else we just return the token
	 * 
	 * @param token
	 * @return
	 */
	static String resolveValue(String token) {
		if (token.length() >= 3) {
			// Check if the property is surrounded by ${}
			if (token.charAt(0) == '$' && token.charAt(1) == '{' && token.charAt(token.length() - 1) == '}') {
				String propertyName = token.substring(2, token.length() - 1);
				try {
					String resultValue = PropertyResolver.resolveProperty(propertyName);
					if( resultValue == null ) {
						// if the token cannot be resolved, return it as is
						return token;
					} else {
						return resultValue;
					}
				} catch (ServiceException e) {
					// ignore any error, we just log the error and return the
					// original value
					e.printStackTrace();
				}
			}
		}
		return token;
	}
	
	/**
	 * Iterate a JSON array and return either an IData[] or String[] 
	 * @param jArr
	 * @return
	 */
	static Object[] iterateJsonArray(JsonArray jArr) {
		// we do not know yet if this is a string or IData array, thus we create both
		IData[] docArr = new IData[jArr.size()];
		String[] stringArr = new String[jArr.size()];
		// if any element in the array is a string array element, then we assume that all elements are string elements
		// note: mixed arrays are thus not supported!
		boolean isStringArr = false;
		for (int i = 0; i < jArr.size(); i++) {
			JsonValue jElem = jArr.get(i);
			if (jElem.isObject()) {
				// recursively parse element and add to IData[]
				IData doc = parseJson(jElem.asObject());
				docArr[i] = doc;
			} else if (jElem.isString()) {
				// we have found a string element, mark accordingly
				stringArr[i] = resolveValue(jElem.asString());
				isStringArr = true;
			}
		}
		if (isStringArr) {
			// if we have found one string element, we assume all elements are string elements
			return stringArr;
		} else {
			return docArr;
		}
	}
	
	private static void loadConfigurationFromDefaultConfigDir() throws ServiceException {
		File configDir = getDefaultConfigDir();
		String[] jsonConfigFiles = configDir.list(new FilenameFilter() {
	
			@Override
			public boolean accept(File paramFile, String paramString) {
				return paramString.endsWith(".json");
			}
		});
		for (String jsonConfigFile : jsonConfigFiles) {
			File configFile = new File(configDir, jsonConfigFile);
			addAssetConfiguration(configFile);
		}
	}
	
	private static void addConfig(String assetType, JsonValue config) {
		JsonValue existingConfig = _configurations.get(assetType);
		if (existingConfig == null) {
			logger.debug("Storing first config for asset of type '" + assetType + "'.");
			_configurations.put(assetType, config);
		} else {
			logger.debug("Merging config for asset type '" + assetType + "' with existing config.");
			mergeJson(config, existingConfig);
		}
	}
	/**
	 * Merges two json configs
	 * @param source
	 * @param target
	 */
	private static void mergeJson(JsonValue source, JsonValue target) {
		// iterate over all children of source
		for (java.util.Iterator<Member> it = source.asObject().iterator(); it.hasNext();) {
			Member sourceMember = it.next();
			String sourceName = sourceMember.getName();
			JsonValue sourceValue = sourceMember.getValue();
			JsonValue targetValue = target.asObject().get(sourceName);
			if (targetValue == null) {
				// there is not target value, just add the source value
				target.asObject().add(sourceName, sourceValue);
				continue;
			}
			if (sourceValue.isString()) {
				// the source value overwrites the target value
				target.asObject().set(sourceName, sourceValue);
			} else if (sourceValue.isArray()) {
				// we have an array
				mergeArray(sourceValue.asArray(), targetValue.asArray());
			} else if (sourceValue.isObject()) {
				// recursively walk objects
				mergeJson(sourceValue, targetValue);
			}
		}
	}
	
	/**
	 * merge two arrays by walking all elements from the source and comparing them with the target
	 * @param sourceArray
	 * @param targetArray
	 */
	private static void mergeArray(JsonArray sourceArray, JsonArray targetArray) {
		for(java.util.Iterator<JsonValue> sourceIterator = sourceArray.iterator(); sourceIterator.hasNext(); ) {
			JsonValue sourceElement = sourceIterator.next();
			if( sourceElement.isString() ) {
				// add a string to a string array
				addJsonStringToArray(sourceElement.asString(), targetArray);
			} else if( sourceElement.isObject() ) {
				// add a object to a object array
				addJsonObjectToArray(sourceElement.asObject(), targetArray);
			} else {
				// we only expect to have strings or objects, anyhting else is unsupported right now
				logger.error("unknown element type " +sourceElement+ " in source array when mergin with target array");
			}
			
		}
	}
	
	/**
	 * Add the source object to the target array if it does not exist there
	 * Existance is checked by walking the target array and comparing with equals each element
	 * Equals expects the exact same JSON string
	 * @param sourceObject
	 * @param targetArray
	 */
	private static void addJsonObjectToArray(JsonObject sourceObject, JsonArray targetArray) {
		for( JsonValue targetElem : targetArray.values() ) {
			if( targetElem.isObject() ) {
				if( targetElem.equals(sourceObject) ) {
					// the array already contains this element
					return;
				}
			}
		}
		targetArray.add(sourceObject);
	}
	
	/**
	 * Add the string to the target array.
	 * @param value
	 * @param targetArray
	 */
	private static void addJsonStringToArray(String value, JsonArray targetArray) {
		for( JsonValue arrayElem : targetArray.values() ) {
			if( arrayElem.isString() && arrayElem.asString().equals(value) ) {
				// the element already exists in the array
				return;
			}
		}
		// we did not find the element in the array, thus add
		targetArray.add(value);
	}
	
	private static List<String> getAssetTypesFromJsonConfig(JsonValue config) throws ServiceException {
		return config.asObject().names();
	}
	
	private static File getDefaultConfigDir() {
		File configDir = ServerAPI.getPackageConfigDir("WxPlatformMonitoring");
	
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put(inputCursor, "propertyName", "watt.wx.platformMonitoring.configDir");
		inputCursor.destroy();
	
		// output
		IData output = IDataFactory.create();
		try {
			output = Service.doInvoke("pub.utils", "getServerProperty", input);
			String propertyValue = IDataUtil.getString(output.getCursor(), "propertyValue");
			if (propertyValue != null && !"".equals(propertyValue)) {
				File externalConfigDir = new File(propertyValue);
				if (externalConfigDir.exists() && externalConfigDir.isDirectory()) {
					logger.info("Using " + externalConfigDir.getAbsolutePath() + " as configuration directory");
					return externalConfigDir;
				}
				logger.error(
						"Extended settings 'watt.wx.platformMonitoring.configDir' does not point to a valid directory. Using 'WxPlatformMonitoring/config' as config directory.");
			} else {
				logger.info(
						"No config directory configured with extended settings 'watt.wx.platformMonitoring.configDir'. Using 'WxPlatformMonitoring/config' as config directory.");
			}
			return configDir;
		} catch (Exception e) {
			logger.error(
					"Could not get config directory from extended settings 'watt.wx.platformMonitoring.configDir'. Using 'WxPlatformMonitoring/config' as config directory.");
		}
		logger.info("Using " + configDir.getAbsolutePath() + " as config dir");
		return configDir;
	}
	
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

