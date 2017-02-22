package com.softwareag.wx.platformMonitoring.jmx;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanOperationInfo;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.wm.app.b2b.client.Context;
import com.wm.app.b2b.client.ServiceException;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Session;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSRecord;

public class InvokeServiceOperation implements ServiceOperation {

	private List<OpenMBeanParameterInfoSupport> inputParameters = new ArrayList<OpenMBeanParameterInfoSupport>();
	private final String serviceName;
	OpenType pipelineOutput = SimpleType.VOID;
	int localServerPost = 5555;

	public InvokeServiceOperation(String serviceName, int localServerPost) throws ParseException {
		this.serviceName = serviceName;
		this.localServerPost = localServerPost;
		parseService();
	}

	private void parseService() throws ParseException {
		getInputFields();
		getOutputFields();
	}

	private void getInputFields() throws ParseException {
		com.wm.lang.ns.NSService service = (com.wm.lang.ns.NSService) com.wm.app.b2b.server.ns.Namespace.current()
				.getNode(serviceName);
		if (service.getSignature() != null && service.getSignature().getInput() != null
				&& service.getSignature().getInput().getFields() != null) {
			for (NSField f : service.getSignature().getInput().getFields()) {
				if (f.getType() != NSField.FIELD_STRING) {
					throw new ParseException(
							"Service " + serviceName
									+ " has input fields which are not simple string values. Not supported for IS Service JMX implementation.",
							-1);
				}
				addStringInputParameter(f.getName());
			}
		}
	}

	private void getOutputFields() throws ParseException {
		com.wm.lang.ns.NSService service = (com.wm.lang.ns.NSService) com.wm.app.b2b.server.ns.Namespace.current()
				.getNode(serviceName);
		if (service.getSignature() != null && service.getSignature().getOutput() != null
				&& service.getSignature().getOutput().getFields() != null) {
			this.pipelineOutput = getOutputFields(service.getSignature().getOutput(), "pipeline");
		}
	}

	private OpenType getOutputFields(NSRecord outputRecord, String name) throws ParseException {
		List<Object> outputFields = new ArrayList<Object>();
		List<String> outputFieldDescriptions = new ArrayList<String>();
		if (outputRecord != null && outputRecord.getFields() != null) {
			for (NSField f : outputRecord.getFields()) {
				outputFieldDescriptions.add(f.getName());
				if (f.getType() == NSField.FIELD_STRING) {
					outputFields.add(f.getName());
				} else if (f.getType() == NSField.FIELD_RECORD || f.getType() == NSField.FIELD_RECORDREF) {
					NSRecord nsRecord = (NSRecord) f;
					outputFields.add(getOutputFields(nsRecord, f.getName()));
				} else {
					throw new ParseException(
							"Error when creating output composite type: only String and recordss are allowd", -1);
				}
			}
		}
		OpenType[] outputFieldTypes = new OpenType[outputFields.size()];
		for (int i = 0; i < outputFields.size(); i++) {
			Object f = outputFields.get(i);
			if (f instanceof String) {
				outputFieldTypes[i] = SimpleType.STRING;
			} else if (f instanceof CompositeType) {
				outputFieldTypes[i] = (CompositeType) f;
			}
		}
		if (outputFieldDescriptions.size() == 0) {
			return SimpleType.VOID;
		}
		CompositeType compositeType;
		try {
			compositeType = new CompositeType(name, name, outputFieldDescriptions.toArray(new String[0]),
					outputFieldDescriptions.toArray(new String[0]), outputFieldTypes);
		} catch (OpenDataException e) {
			throw new ParseException("Error when creating output compoisite type for name " + name, -1);
		} catch (java.lang.IllegalArgumentException a) {
			throw new ParseException("Error when creating output compoisite type for name " + name, -1);
		}
		return compositeType;
	}

	private void addStringInputParameter(String inputParameterName) {
		OpenMBeanParameterInfoSupport op = new OpenMBeanParameterInfoSupport(inputParameterName,
				"Service Input field " + inputParameterName, SimpleType.STRING);
		this.inputParameters.add(op);
	}

	public OpenMBeanOperationInfoSupport createOperation() {
		return new OpenMBeanOperationInfoSupport(serviceName, "Invoke service " + serviceName,
				this.inputParameters.toArray(new OpenMBeanParameterInfoSupport[0]), pipelineOutput,
				MBeanOperationInfo.INFO);
	}

	public Object buildOutput(Object[] params, String[] signature, Session session, InvokeState state)
			throws OpenDataException {
		Object output = invokeService(params, signature, session, state);
		return output;
	}

	public Object invokeService(Object[] params, String[] signature, Session session, InvokeState state) {

		IData input = IDataFactory.create();
		for (int i = 0; i < params.length; i++) {
			OpenMBeanParameterInfoSupport param = this.inputParameters.get(i);
			String name = param.getName();
			String value = params[i].toString();
			IDataUtil.put(input.getCursor(), name, value);
		}
		// output
		IData output = IDataFactory.create();
		try {
			output = invokeService(serviceName, input);
			return parseOutput(output, this.pipelineOutput);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("----------- Could not invoke service " + this.serviceName + ": " + e);
		}
		return null;
	}

	private IData invokeService(String service, IData pipeline) throws ServiceException {

		Context context = new Context();
		try {
			context.connect("localhost:" + this.localServerPost, null, null);
		} catch (Exception e) {
			throw new ServiceException("Unable to connect to localhost for service invoke: " + e);
		}
		String[] nsparts = service.split(":");
		IData output = context.invoke(nsparts[0], nsparts[1], pipeline);
		context.disconnect();
		return output;
	}

	private Object parseOutput(IData pipeline, OpenType openType) throws OpenDataException {
		if (openType instanceof CompositeType) {
			return parseOutput(pipeline, (CompositeType) openType);
		} else if (openType instanceof SimpleType<?>) {
			if (openType.isValue(null)) {
				return null;
			} else {
				throw new OpenDataException();
			}
		} else {
			throw new OpenDataException();
		}
	}

	private CompositeDataSupport parseOutput(IData pipeline, CompositeType compositeType) throws OpenDataException {
		if (pipeline == null)
			pipeline = IDataFactory.create();
		String[] keys = new String[compositeType.keySet().size()];
		Object[] values = new Object[compositeType.keySet().size()];
		IDataCursor pipelineC = pipeline.getCursor();
		int i = 0;
		for (String key : compositeType.keySet()) {
			OpenType type = compositeType.getType(key);
			String typeName = type.getTypeName();
			if (typeName.equals("java.lang.String")) {
				String pipelineValue = IDataUtil.getString(pipelineC, key);
				if (pipelineValue == null) {
					pipelineValue = new String("");
				}
				keys[i] = key;
				values[i] = pipelineValue;
			} else {
				IData pipelineValue = IDataUtil.getIData(pipelineC, key);
				keys[i] = key;
				values[i] = parseOutput(pipelineValue, (CompositeType) type);
			}
			i++;
		}
		CompositeDataSupport outputDocument = new CompositeDataSupport(compositeType, keys, values);
		return outputDocument;
	}

	private CompositeDataSupport parseOutput2(IData pipeline, CompositeType compositeType) throws OpenDataException {
		if (pipeline == null)
			return null;
		Map<String, Object> output = new HashMap<String, Object>();
		IDataCursor pipelineC = pipeline.getCursor();
		for (String key : compositeType.keySet()) {
			OpenType type = compositeType.getType(key);
			String typeName = type.getTypeName();
			if (typeName.equals("java.lang.String")) {
				String pipelineValue = IDataUtil.getString(pipelineC, key);
				output.put(key, pipelineValue);
			} else {
				IData pipelineValue = IDataUtil.getIData(pipelineC, key);
				output.put(key, parseOutput(pipelineValue, (CompositeType) type));
			}
		}
		CompositeDataSupport outputDocument = new CompositeDataSupport(compositeType,
				output.keySet().toArray(new String[0]), output.entrySet().toArray());
		return outputDocument;
	}

}
