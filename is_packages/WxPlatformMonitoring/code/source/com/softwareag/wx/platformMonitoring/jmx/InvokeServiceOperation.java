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

import org.apache.log4j.Logger;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.ServiceThread;
import com.wm.app.b2b.server.Session;
import com.wm.app.b2b.server.StateManager;
import com.wm.app.b2b.server.User;
import com.wm.app.b2b.server.UserManager;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSRecord;

public class InvokeServiceOperation implements ServiceOperation {

	Logger logger = Logger.getLogger(this.getClass());

	// a list of input parameters for the given service
	private List<OpenMBeanParameterInfoSupport> serviceInputParameters = new ArrayList<OpenMBeanParameterInfoSupport>();
	// the service name for which this operation is
	private final String serviceName;
	OpenType<?> serviceOutputParameter = SimpleType.VOID;

	public InvokeServiceOperation(String serviceName) throws ParseException {
		this.serviceName = serviceName;
		parseServiceInputOutput();
	}

	private void parseServiceInputOutput() throws IllegalArgumentException, ParseException {
		parseServiceInputDefinition();
		parseServiceOutputDefinition();
	}

	/**
	 * parse the service's input signature and create for each input parameter a
	 * MBean representation
	 * 
	 * @throws IllegalArgumentException
	 *             is thrown when the service has Non-String input parameters
	 */
	private void parseServiceInputDefinition() throws IllegalArgumentException {
		com.wm.lang.ns.NSService service = (com.wm.lang.ns.NSService) com.wm.app.b2b.server.ns.Namespace.current()
				.getNode(serviceName);
		if (service.getSignature() != null && service.getSignature().getInput() != null
				&& service.getSignature().getInput().getFields() != null) {
			for (NSField f : service.getSignature().getInput().getFields()) {
				if (f.getType() != NSField.FIELD_STRING) {
					throw new IllegalArgumentException("Service " + serviceName
							+ " has input fields which are not simple string values. Not supported for IS Service JMX implementation.");
				}
				addStringInputParameter(f.getName());
			}
		} else {
			throw new IllegalArgumentException("Could not parse the service signature for service " + serviceName
					+ ", it is either non-existing or empty");
		}
	}

	/**
	 * Added the given inputParameter to the list of inputParameters for this
	 * service
	 * 
	 * @param inputParameterName
	 */
	private void addStringInputParameter(String inputParameterName) {
		OpenMBeanParameterInfoSupport op = new OpenMBeanParameterInfoSupport(inputParameterName,
				"Service Input field " + inputParameterName, SimpleType.STRING);
		this.serviceInputParameters.add(op);
	}

	/**
	 * Parse the service's output signature
	 * 
	 * @throws ParseException
	 */
	private void parseServiceOutputDefinition() throws ParseException {
		com.wm.lang.ns.NSService service = (com.wm.lang.ns.NSService) com.wm.app.b2b.server.ns.Namespace.current()
				.getNode(serviceName);
		if (service.getSignature() != null && service.getSignature().getOutput() != null
				&& service.getSignature().getOutput().getFields() != null) {
			NSRecord outputSignatureRecord = service.getSignature().getOutput();
			this.serviceOutputParameter = getOutputFieldDefinition(outputSignatureRecord, "pipeline");
		}
	}

	/**
	 * Recursively parse the NSRecord object which represents either the
	 * service's output signature or referenced documents
	 * 
	 * @param outputRecord
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 *             is thrown if Non-String or Non-Document(-Reference) fields
	 *             are encountered, e.g. Object fields, which are not supported
	 * @throws ParseException
	 *             is thrown when the MBean output representation for the given
	 *             NSRecord cannot be created
	 */
	private OpenType<?> getOutputFieldDefinition(NSRecord outputRecord, String name)
			throws IllegalArgumentException, ParseException {
		List<Object> outputFields = new ArrayList<Object>();
		List<String> outputFieldDescriptions = new ArrayList<String>();
		// iterate over all fields and add recursively to lists of outputFields
		// (either SimpleType.STRING or a complex CompositeType)
		if (outputRecord != null && outputRecord.getFields() != null || outputRecord.getFields().length != 0) {
			for (NSField f : outputRecord.getFields()) {
				outputFieldDescriptions.add(f.getName());
				if (f.getType() == NSField.FIELD_STRING) {
					outputFields.add(f.getName());
				} else if (f.getType() == NSField.FIELD_RECORD || f.getType() == NSField.FIELD_RECORDREF) {
					NSRecord nsRecord = (NSRecord) f;
					outputFields.add(getOutputFieldDefinition(nsRecord, f.getName()));
				} else {
					throw new IllegalArgumentException(
							"Error when creating output composite type: only String and recordss are allowd. Found "
									+ f.getNodeTypeObj().getType() + " in service " + serviceName);
				}
			}
		} else {
			// if the NSRecord has not fields, we have a void field (e.g. a
			// service without Output pipeline
			return SimpleType.VOID;
		}
		// convert the list of output fields to an array
		OpenType[] outputFieldTypes = new OpenType[outputFields.size()];
		for (int i = 0; i < outputFields.size(); i++) {
			Object f = outputFields.get(i);
			if (f instanceof String) {
				outputFieldTypes[i] = SimpleType.STRING;
			} else if (f instanceof CompositeType) {
				outputFieldTypes[i] = (CompositeType) f;
			} else {
				throw new IllegalArgumentException(
						"Only simple String fields or document (references) are allowed for service signature. Service: "
								+ serviceName);
			}
		}
		try {
			// create the CompositeType itself, which represent this NSRecord
			// object for the MBean
			CompositeType compositeType = new CompositeType(name, name, outputFieldDescriptions.toArray(new String[0]),
					outputFieldDescriptions.toArray(new String[0]), outputFieldTypes);
			return compositeType;
		} catch (OpenDataException e) {
			throw new ParseException("Error when creating output compoisite type for name " + name, -1);
		}
	}

	/**
	 * Returns the MBean representation for this given IntegratoinServer Service
	 * signature Note: always returns an operation of
	 * type @MBeanOperationInfo.ACTION_INFO, so it represents services which
	 * both return information and may set values
	 */
	public OpenMBeanOperationInfoSupport createMBeanOperationForService() {
		return new OpenMBeanOperationInfoSupport(serviceName, "Invoke service " + serviceName,
				this.serviceInputParameters.toArray(new OpenMBeanParameterInfoSupport[0]), this.serviceOutputParameter,
				MBeanOperationInfo.ACTION_INFO);
	}

	/**
	 * Invoke the IS Service and return the service IData outputs as an MBean
	 * object
	 */
	public Object invokeService(Object[] params, String[] signature, Session session, User user)
			throws ServiceException, OpenDataException {
		IData output = invokeService(params, session, user);
		return parseServiceOutput(output);
	}

	private IData invokeService(Object[] params, Session session, User user) throws ServiceException {
		IData input = IDataFactory.create();
		for (int i = 0; i < params.length; i++) {
			OpenMBeanParameterInfoSupport param = this.serviceInputParameters.get(i);
			String name = param.getName();
			String value = params[i].toString();
			IDataUtil.put(input.getCursor(), name, value);
		}
		// output
		IData output = IDataFactory.create();
		try {
			output = invokeService(serviceName, input, user, session);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("----------- Could not invoke service " + this.serviceName + ": " + e);
		}
	}

	/**
	 * Necessary because we do not have a valid invoke state, which is required
	 * for a service invoke
	 * 
	 * @return
	 */
	private static InvokeState createDummyInvokeState() {
		InvokeState state = InvokeState.getCurrentState();
		if (state == null) {
			state = new InvokeState();
		}
		return state;
	}

	private IData invokeService(String serviceName, IData inData, User user, Session session) throws ServiceException {
		String dummySessionName = "WxPlatformMonitoringDummySession";
		try {
			createDummyInvokeState();
			// if no user is provided, we create a dummy "Adminstrator" user
			if (user == null) {
				user = UserManager.getUser("Administrator");
			}
			// if no session is provided, we create a dummy session
			if (session == null) {
				session = StateManager.createContext(Integer.MAX_VALUE, dummySessionName, user);
			}
			// prepare the objects
			session.setUser(user);
			InvokeState.setCurrentSession(session);
			InvokeState.setCurrentUser(session.getUser());
			// do a threaded invoke
			// TODO: check if we can also do a normale invoke (Service.doInvoke())
			ServiceThread st = Service.doThreadInvoke(NSName.create(serviceName), inData);
			IData result = st.getIData();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			String message = "error when invoking service " + serviceName + ": " + e;
			logger.error(message);
			throw new ServiceException(message);
		} finally {
			if (session != null) {
				// delete context if we have created a dummy session
				if( session.getName().equals(dummySessionName) ) {
					StateManager.deleteContext(session.getSessionID());
				}
			}
		}
	}

	/**
	 * Parses an IData object and creates an MBean compatible output object
	 * 
	 * @param pipeline
	 * @return
	 * @throws OpenDataException
	 */
	private Object parseServiceOutput(IData pipeline) throws OpenDataException {
		// check if the service signature has defined any outputs
		if (this.serviceOutputParameter instanceof SimpleType<?> && this.serviceOutputParameter.isValue(null)) {
			// the service does not return anything (service signature is
			// empty (VOID)). return null
			return null;
		} else if (this.serviceOutputParameter instanceof CompositeType) {
			return parseOutput(pipeline, (CompositeType) this.serviceOutputParameter);
		} else {
			// A non VOID output parameter (i.e. empty output) is not
			// allowed.
			// If the signature has an output, it must be
			// referenced by a "CompositeType".
			// If the signature does not have an output, it must be
			// represented by a "SimpleType.VOID".
			// Everything else is not supported
			String message = "The Service signature's MBean representation is neither VOID (empty) nor a CompositeType";
			logger.error(message);
			throw new OpenDataException(message);
		}
	}

	/**
	 * Parse the IData object and create a (nested) CompositeDataSupport object,
	 * based on the provided signature
	 * 
	 * @param pipeline
	 * @param compositeType
	 * @return
	 * @throws OpenDataException
	 */
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
}
