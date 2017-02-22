package com.softwareag.wx.platformMonitoring.jmx;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.openmbean.OpenDataException;

import com.wm.app.b2b.server.Session;
import com.wm.app.b2b.server.User;

public class InvokeSimpleServiceOperation implements ServiceOperation {

	private List<MBeanParameterInfo> inputParameters = new ArrayList<MBeanParameterInfo>();
	private String serviceName = null;

	public InvokeSimpleServiceOperation(String serviceName) {
		this.serviceName = serviceName;
	}

	public void addInputParameter(String inputParameterName) {

		MBeanParameterInfo p = new MBeanParameterInfo(inputParameterName, "java.lang.String",
				"Service Input field " + inputParameterName);
		this.inputParameters.add(p);
	}

	public MBeanOperationInfo createMBeanOperationForService() {
		MBeanParameterInfo[] inputs = null;
		if (inputParameters.size() > 0) {
			inputs = inputParameters.toArray(new MBeanParameterInfo[0]);
		}
		return new MBeanOperationInfo(serviceName, "Invoke service " + serviceName, inputs, "java.lang.String",
				MBeanOperationInfo.ACTION);
	}

	@Override
	public Object invokeService(Object[] params, String[] signature, Session session, User user) throws OpenDataException {
		// TODO Auto-generated method stub
		return "mySimpleOutput";
	}

}
