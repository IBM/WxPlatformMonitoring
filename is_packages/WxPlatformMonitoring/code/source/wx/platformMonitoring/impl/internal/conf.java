package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-03-14 18:49:24 CET
// -----( ON-HOST: 192.168.221.165

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

	static conf _newInstance() {
		return new conf();
	}

	static conf _cast(Object o) {
		return (conf) o;
	}

	// ---( server methods )---

	public static final void getConfig(IData pipeline) throws ServiceException {
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
			logger.error(
					"No config stored for asset of type '" + assetType + "', trying to reload default config dir.");
			loadConfigurationFromDefaultConfigDir();
			jConfig = _configurations.get(assetType);
			if (jConfig == null) {
				logger.error("No config stored for asset of type '" + assetType + "'.");
				return;
			}
		}
		JsonValue assetConfig = jConfig.asObject().get(assetType);
		// IData configDoc = parseJson(assetConfig.asObject());
		IData configDoc = parseJson(jConfig.asObject());
		IDataUtil.put(pipelineCursor, "config", configDoc);
		pipelineCursor.destroy();

		// --- <<IS-END>> ---

	}

	public static final void loadConfigurationFile(IData pipeline) throws ServiceException {
		// --- <<IS-START(loadConfigurationFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required configFile
		IDataCursor pipelineC = pipeline.getCursor();
		String configFile = IDataUtil.getString(pipelineC, "configFile");

		addAssetConfiguration(configFile);
		// --- <<IS-END>> ---

	}

	public static final void loadConfigurationFromJsonString(IData pipeline) throws ServiceException {
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

	public static final void loadDefaultConfiguration(IData pipeline) throws ServiceException {
		// --- <<IS-START(loadDefaultConfiguration)>> ---
		// @sigtype java 3.5
		loadConfigurationFromDefaultConfigDir();
		// --- <<IS-END>> ---

	}

	public static final void loadPlatformMonitoringConfigForPackage(IData pipeline) throws ServiceException {
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

	public static final void resetConfig(IData pipeline) throws ServiceException {
		// --- <<IS-START(resetConfig)>> ---
		// @sigtype java 3.5
		_configurations.clear();
		// --- <<IS-END>> ---

	}

	// --- <<IS-START-SHARED>> ---
	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("wx.platformMonitoring.config");
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
		logger.debug("Getting config from '" + configFile.getAbsolutePath() + "'");
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

	static IData parseJson(JsonObject jObj) {
		IData element = IDataFactory.create();
		IDataCursor elementC = element.getCursor();
		for (String childName : jObj.names()) {
			JsonValue child = jObj.get(childName);
			if (child.isObject()) {
				IData childDoc = parseJson(child.asObject());
				IDataUtil.put(elementC, childName, childDoc);
			} else if (child.isArray()) {
				Object[] array = iterateJsonArray(child.asArray());
				IDataUtil.put(elementC, childName, array);
			} else if (child.isString()) {
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
				// String propertyName = token.substring(2, token.length() - 1);
				try {
					String resultValue = PropertyResolver.resolveProperty(token);
					return resultValue;
				} catch (ServiceException e) {
					// ignore any error, we just log the error and return the
					// original value
					e.printStackTrace();
				}
			}
		}
		return token;
	}

	static Object[] iterateJsonArray(JsonArray jArr) {
		IData[] docArr = new IData[jArr.size()];
		String[] stringArr = new String[jArr.size()];
		boolean isStringArr = false;
		for (int i = 0; i < jArr.size(); i++) {
			JsonValue jElem = jArr.get(i);
			if (jElem.isObject()) {
				IData doc = parseJson(jElem.asObject());
				docArr[i] = doc;
			} else if (jElem.isString()) {
				stringArr[i] = resolveValue(jElem.asString());
				isStringArr = true;
			}
		}
		if (isStringArr) {
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

	private static void mergeJson(JsonValue source, JsonValue target) {
		for (java.util.Iterator<Member> it = source.asObject().iterator(); it.hasNext();) {
			Member sourceMember = it.next();
			String sourceName = sourceMember.getName();
			JsonValue sourceValue = sourceMember.getValue();
			JsonValue targetValue = target.asObject().get(sourceName);
			if (targetValue == null) {
				target.asObject().add(sourceName, sourceValue);
				continue;
			}
			if (sourceValue.isString()) {
				// the source value overwrites the target value
				target.asObject().set(sourceName, sourceValue);
			} else if (sourceValue.isArray()) {
				for (java.util.Iterator<JsonValue> sourceIt = sourceValue.asArray().iterator(); sourceIt.hasNext();) {
					JsonValue sourceArrValue = sourceIt.next();
					boolean found = false;
					for (JsonValue targetArrValue : targetValue.asArray().values()) {
						if (targetArrValue.isString() && sourceArrValue.isString()) {
							if (targetArrValue.toString().equals(sourceArrValue.toString())) {
								// nothing to do, elements are the same, no need
								// to append
								found = true;
								break;
							} else {
								// search on
								continue;
							}
						} else if (targetArrValue.isObject() && sourceArrValue.isObject()) {
							mergeJson(sourceArrValue, targetArrValue);
						} else {
							// the type of the source and target are not the
							// same, continue
							continue;
						}
					}
					if (!found)
						targetValue.asArray().add(sourceArrValue);
				}
			} else if (sourceValue.isObject()) {
				mergeJson(sourceValue, targetValue);
			}
		}
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
