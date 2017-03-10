package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-03-10 13:05:54 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class configuration

{
	// ---( internal utility methods )---

	final static configuration _instance = new configuration();

	static configuration _newInstance() {
		return new configuration();
	}

	static configuration _cast(Object o) {
		return (configuration) o;
	}

	// ---( server methods )---

	public static final void loadDefaultConfiguration(IData pipeline) throws ServiceException {
		// --- <<IS-START(loadDefaultConfiguration)>> ---
		// @sigtype java 3.5
		loadConfigurationFromDefaultConfigDir();
		// --- <<IS-END>> ---

	}

	// --- <<IS-START-SHARED>> ---

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("wx.platformMonitoring.config");

	static java.util.Map<String, JsonValue> _configurations = new java.util.HashMap<String, JsonValue>();
	
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
				IDataUtil.put(elementC, childName, child.asString());
			}
		}
		elementC.destroy();
		return element;
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
				stringArr[i] = jElem.asString();
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
				// TODO Auto-generated method stub
				return paramString.endsWith(".json");
			}
		});
		for( String jsonConfigFile : jsonConfigFiles ) {
			File configFile = new File(configDir, jsonConfigFile);
			JsonValue config = loadJsonConfiguration(configFile);
			String assetType = config.asObject().names().get(0);
			if( assetType == null || "".equals(assetType) ) {
				logger.error("Cannot load config " + jsonConfigFile + " from config dir " + configDir.getAbsolutePath() + ": file has no root String element specifying the asset type.");
				continue;
			}
			logger.info("Reading config " + jsonConfigFile + " from config dir " + configDir.getAbsolutePath() + " as asset type '" + assetType + "'.");
			_configurations.put(assetType, config.asObject().get(jsonConfigFile));
		}
	}
	
	private static JsonValue loadJsonConfiguration(File configFile) throws ServiceException {
		logger.debug("Getting config from " + configFile.getAbsolutePath());
		java.io.Reader reader;
		try {
			reader = new FileReader(configFile);
			return Json.parse(reader);
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
			throw new ServiceException(
					"FileNotFoundException: Could not read json config file '" + configFile + "': " + fnfe);
		} catch (IOException ioe) {
			throw new ServiceException("IOException: Could not read json config file '" + configFile + "': " + ioe);
		}
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
