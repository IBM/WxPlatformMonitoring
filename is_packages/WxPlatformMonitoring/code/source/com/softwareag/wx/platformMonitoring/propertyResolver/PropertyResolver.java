package com.softwareag.wx.platformMonitoring.propertyResolver;

import javax.naming.InvalidNameException;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;

public class PropertyResolver {

	private static PropertyResolver _instance = null;

	private static NSName resolverService;

	static {
		_instance = new PropertyResolver();
	}

	private PropertyResolver() {

	}

	public static PropertyResolver getInstance() {
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
			throw new InvalidNameException("Provide a valid fully qualified name to a property resolver service: " + e);
		}
	}

	public static String resolveProperty(String propertyName) throws ServiceException {
		if( resolverService == null ) {
			throw new ServiceException("No property resolver service registerd!");
		}
		IData input = IDataFactory.create();
		IDataCursor inputC = input.getCursor();
		IDataUtil.put(inputC, "propertyName", propertyName);
		inputC.destroy();
		try {
			IData output = Service.doInvoke(resolverService, input);
			IDataCursor outputC = output.getCursor();
			String propertyValue = IDataUtil.getString(outputC, "propertyValue");
			outputC.destroy();
			return propertyValue;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Could not resolve property " + propertyName + " with resolver service "
					+ resolverService.getFullName() + ": " + e);
		}
	}

}
