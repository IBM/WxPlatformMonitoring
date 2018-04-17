package com.softwareag.wx.platformMonitoring.configResolver;

import javax.naming.InvalidNameException;

import org.apache.log4j.Logger;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;

public class ConfigResolver {

	private static ConfigResolver _instance = null;

	private static NSName resolverService;

	private static Logger logger = Logger.getLogger(ConfigResolver.class);

	static {
		_instance = new ConfigResolver();
	}

	private ConfigResolver() {

	}

	public static ConfigResolver getInstance() {
		return _instance;
	}

	public static void registerResolverService(String resolverServiceFqn) throws InvalidNameException {
		try {
			/*
			 * Use the approach below instead of NSName.create() because this
			 * one throws a NPE if the service does not exist
			 */
			BaseService bs = Namespace.getService(NSName.create(resolverServiceFqn));
			resolverService = bs.getNSName();
		} catch (Exception e) {
			throw new InvalidNameException("Provide a valid fully qualified name to a config resolver service: " + e);
		}
	}

	public String resolveConfig(String key, String packageName) throws ServiceException {
		if (resolverService == null) {
			logger.error("No config resolver service registerd! Returning nothing.");
			return null;
		}
		IData input = IDataFactory.create();
		IDataCursor inputC = input.getCursor();
		IDataUtil.put(inputC, "key", key);
		if (packageName != null && !"".equals(packageName)) {
			IDataUtil.put(inputC, "packageName", packageName);
		}
		inputC.destroy();
		try {
			IData output = Service.doInvoke(resolverService, input);
			IDataCursor outputC = output.getCursor();
			String value = IDataUtil.getString(outputC, "value");
			outputC.destroy();
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Could not resolve config for key '" + key + "' in package '" + packageName
					+ "' with config service " + resolverService.getFullName() + ": " + e;
			logger.error(msg);
			throw new ServiceException(msg);
		}
	}

}
