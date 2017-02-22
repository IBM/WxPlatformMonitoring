package com.softwareag.wx.platformMonitoring.jmx;

import javax.management.MBeanOperationInfo;
import javax.management.openmbean.OpenDataException;

import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.Session;
import com.wm.app.b2b.server.User;

public interface ServiceOperation {

	public MBeanOperationInfo createMBeanOperationForService();

	public Object invokeService(Object[] params, String[] signature, Session session, User user)
			throws OpenDataException, ServiceException;

}
