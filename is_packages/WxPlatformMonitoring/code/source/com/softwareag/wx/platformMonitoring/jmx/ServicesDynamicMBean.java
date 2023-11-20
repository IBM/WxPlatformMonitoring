package com.softwareag.wx.platformMonitoring.jmx;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.OpenDataException;

import org.apache.logging.log4j.Logger;

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServiceException;
import com.wm.app.b2b.server.Session;
import com.wm.app.b2b.server.User;

public class ServicesDynamicMBean implements DynamicMBean {

	String dClassName = this.getClass().getName();
	String folderName = "undefined";
	String packageName = "undefined";
	Session session = null;
	User user = null;
	List<String> services = null;

	/**
	 * These lists/arrays must be initialized in the constructor, else we have
	 * duplicates
	 */
	List<MBeanAttributeInfo> beanAttributesList = null;
	Map<String, ServiceOperation> serviceOperationMap = null;
	MBeanConstructorInfo[] dConstructors = null;

	Logger logger = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	/**
	 * Create a Dynamic MBean for the list of services
	 * 
	 * @param packageName
	 *            gets exposed as an attribte
	 * @param folderName
	 *            gets exposed as an attribte
	 * @param services
	 *            the list of services to expose as operations
	 * @param session
	 *            the IS Server Session with which this bean is created. Used
	 *            for Service Invokes. If null, it is created ad hoc.
	 * @param user
	 *            the IS Server User with which this mBean is created. Used for
	 *            Service Invokes. If null, a dummy User (Administrator) is
	 *            created ad hoc.
	 */
	public ServicesDynamicMBean(String packageName, String folderName, List<String> services,
			com.wm.app.b2b.server.Session session, User user) {
		this.folderName = folderName;
		this.packageName = packageName;
		this.session = session;
		this.user = user;
		this.services = services;

		// we have to initialize these lists here, because this method gets
		// invoked multiple times (don't know why), and if we re-use the list
		// each time we will duplicate entries
		this.beanAttributesList = new ArrayList<MBeanAttributeInfo>();
		this.serviceOperationMap = new HashMap<String, ServiceOperation>();
		this.dConstructors = new MBeanConstructorInfo[1];
	}

	/**
	 * Create a Dynamic MBean for the list of services. User and Session for
	 * Service Invokes are generated at runtime
	 * 
	 * @param packageName
	 *            gets exposed as an attribte
	 * @param folderName
	 *            gets exposed as an attribte
	 * @param services
	 *            the list of services to expose as operations
	 */
	public ServicesDynamicMBean(String packageName, String folderName, List<String> services) {
		this(folderName, packageName, services, null, null);
	}

	/**
	 * Standard implementation
	 */
	@Override
	public Object getAttribute(String attribute_name)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		// Check attribute_name to avoid NullPointerException later on
		if (attribute_name == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"),
					"Cannot invoke a getter of " + dClassName + " with null attribute name");
		}

		// we only have two standard attributes for each MBean, i.e. folder name
		// and package
		// Call the corresponding getter for a recognized attribute_name
		if (attribute_name.equals("Package")) {
			return getPackageName();
		}
		if (attribute_name.equals("Folder")) {
			return getFolderName();
		}
		// If attribute_name has not been recognized
		throw (new AttributeNotFoundException("Cannot find " + attribute_name + " attribute in " + dClassName));
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Standard implementation
	 */
	@Override
	public AttributeList getAttributes(String[] attributeNames) {
		// Check attributeNames to avoid NullPointerException later on
		if (attributeNames == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"),
					"Cannot invoke a getter of " + dClassName);
		}
		AttributeList resultList = new AttributeList();

		// if attributeNames is empty, return an empty result list
		if (attributeNames.length == 0)
			return resultList;

		// build the result attribute list
		for (int i = 0; i < attributeNames.length; i++) {
			try {
				Object value = getAttribute((String) attributeNames[i]);
				resultList.add(new Attribute(attributeNames[i], value));
			} catch (Exception e) {
				// print debug info but continue processing list
				e.printStackTrace();
				logger.error("Exception occured when calling getAttributes: " + e);
			}
		}
		return (resultList);
	}

	/**
	 * Create the MBean
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		// generic building the constructor. Done like this in all tutorials...
		Constructor[] constructors = this.getClass().getConstructors();
		this.dConstructors[0] = new MBeanConstructorInfo("Constructs a " + "ServicesDynamicMBean object",
				constructors[0]);

		// add the two standard attributes to the bean
		addReadOnlyStringAttribute("Folder", "Folder name to scan for services");
		addReadOnlyStringAttribute("Package", "Package name");

		// add all services to the mbean as operations
		parseServicesAndToServiceOperationMap();

		/*
		 * construct the MBeanInfo Object
		 */
		String mBeanDescription = "MBean for package " + this.packageName + " and folder " + this.folderName;
		MBeanOperationInfo[] mMBeanOperationInfoArray = buildMBeanOperationInfoArray();
		// we do not have any notifications
		MBeanNotificationInfo[] mMBeanNotificationArray = new MBeanNotificationInfo[0];
		MBeanInfo dMBeanInfo = new MBeanInfo(this.getClass().getName(), mBeanDescription,
				beanAttributesList.toArray(new MBeanAttributeInfo[0]), dConstructors, mMBeanOperationInfoArray,
				mMBeanNotificationArray);

		return dMBeanInfo;
	}

	/**
	 * Gets called for operations
	 */
	@Override
	public Object invoke(String operationName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		// Check operationName to avoid NullPointerException later on
		if (operationName == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"),
					"Cannot invoke a null operation in " + dClassName);
		} else if (serviceOperationMap.containsKey(operationName)) {
			try {
				// get the serviceOperation
				ServiceOperation serviceOperation = serviceOperationMap.get(operationName);
				// build the output, i.e. invoke the service and generate the
				// MBean output object
				return serviceOperation.invokeService(params, signature, session, user);
			} catch (OpenDataException e) {
				e.printStackTrace();
				String message = "An error occured when invoking operation '" + operationName + "': " + e;
				logger.error(message);
				throw new MBeanException(e, message);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				String message = "An ServiceException occured when invoking IS Service for operation '" + operationName + "': " + e;
				logger.error(message);
				throw new MBeanException(e, message);
			}
		} else {
			// unrecognized operation name:
			throw new ReflectionException(new NoSuchMethodException(operationName),
					"Cannot find the operation " + operationName + " in " + dClassName);
		}
	}

	/**
	 * Standard implementation
	 */
	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		// Check attribute to avoid NullPointerException later on
		if (attribute == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"),
					"Cannot invoke a setter of " + dClassName + " with null attribute");
		}
		String name = attribute.getName();
		Object value = attribute.getValue();

		throw new InvalidAttributeValueException(
				"Cannot set attribute " + name + " to a " + value.getClass().getName() + " object.");

	}

	/**
	 * Iterates over the service list (provided in constructor) and create the
	 * wrapper MBean objects
	 */
	private void parseServicesAndToServiceOperationMap() {
		for (String service : this.services) {
			try {
				InvokeServiceOperation invokeServiceOperation = new InvokeServiceOperation(service);
				serviceOperationMap.put(service, invokeServiceOperation);
			} catch (IllegalArgumentException i) {
				// log error but continue with other services
				i.printStackTrace();
				logger.error("An error occured when parsing input or output of service " + service + ": " + i);
			} catch (ParseException e) {
				// log error but continue with other services
				e.printStackTrace();
				logger.error("Ccould not parse service " + service + ": " + e);
			}
		}
	}

	/**
	 * Creates the MBean array for all operations which where passed to this
	 * bean
	 * 
	 * @return
	 */
	private MBeanOperationInfo[] buildMBeanOperationInfoArray() {
		List<MBeanOperationInfo> operations = new ArrayList<MBeanOperationInfo>();
		for (String serviceName : serviceOperationMap.keySet()) {
			ServiceOperation op = serviceOperationMap.get(serviceName);
			// create the MBean operation objects
			MBeanOperationInfo operation = op.createMBeanOperationForService();
			// add the operation to the list
			operations.add(operation);
		}
		return operations.toArray(new MBeanOperationInfo[0]);
	}

	/**
	 * Use to create an operation for a service which has only one string field
	 * as an output Note: the output field is unnamed if created like this.
	 * 
	 * @param serviceName
	 * @param inputVariables
	 */
	private void addSimpleInvokeServiceOperation(String serviceName, String[] inputVariables) {
		InvokeSimpleServiceOperation op = new InvokeSimpleServiceOperation(serviceName);
		for (String input : inputVariables) {
			op.addInputParameter(input);
		}
		serviceOperationMap.put(serviceName, op);
	}

	/**
	 * Creates a read only attribute for this mbean of the given type
	 * 
	 * @param name
	 * @param type
	 *            e.g. "java.lang.String"
	 * @param description
	 */
	private void addReadOnlyAttribute(String name, String type, String description) {
		MBeanAttributeInfo attribute = new MBeanAttributeInfo(name, type, description, true, true, false);
		beanAttributesList.add(attribute);
	}

	private void addReadOnlyStringAttribute(String name, String description) {
		this.addReadOnlyAttribute(name, "java.lang.String", description);
	}

	/**
	 * Standard implementation
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		// Check attributes to avoid NullPointerException later on
		if (attributes == null) {
			throw new RuntimeOperationsException(
					new IllegalArgumentException("AttributeList attributes cannot be null"),
					"Cannot invoke a setter of " + dClassName);
		}
		AttributeList resultList = new AttributeList();

		// if attributeNames is empty, nothing more to do
		if (attributes.isEmpty())
			return resultList;

		// try to set each attribute and add to result list if successful
		for (Iterator i = attributes.iterator(); i.hasNext();) {
			Attribute attr = (Attribute) i.next();
			try {
				setAttribute(attr);
				String name = attr.getName();
				Object value = getAttribute(name);
				resultList.add(new Attribute(name, value));
			} catch (Exception e) {
				// print debug info but keep processing list
				e.printStackTrace();
			}
		}
		return (resultList);
	}

}
