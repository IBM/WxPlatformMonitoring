package com.softwareag.wx.platformMonitoring.jmx;

import javax.management.MBeanOperationInfo;
import javax.management.openmbean.OpenDataException;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Session;

public interface ServiceOperation {


	public MBeanOperationInfo createOperation();
	

	public Object buildOutput(Object[] params, String[] signature, Session session, InvokeState state) throws OpenDataException;
	
}
