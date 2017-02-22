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

import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.Session;

public class ServicesDynamicMBean implements DynamicMBean {

	String dClassName = this.getClass().getName();
	String folderName = "undefined";
	String packageName = "undefined";
	String serviceName = "undefined";
	Session session = null;
	InvokeState state = null;

	public ServicesDynamicMBean(String packageName, String folderName, String serviceName, com.wm.app.b2b.server.Session session, InvokeState state) {
		this.folderName = folderName;
		this.packageName = packageName;
		this.serviceName = serviceName;
		this.session = session;
		this.state = state;
	}

	@Override
	public Object getAttribute(String attribute_name)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		// Check attribute_name to avoid NullPointerException later on
		if (attribute_name == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"),
					"Cannot invoke a getter of " + dClassName + " with null attribute name");
		}

		// Call the corresponding getter for a recognized attribute_name
		if (attribute_name.equals("Package")) {
			return getPackageName();
		}
		if (attribute_name.equals("Folder")) {
			return getFolderName();
		}
		if (attribute_name.equals("Service")) {
			return getServiceName();
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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

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
			}
		}
		return (resultList);
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		// TODO Auto-generated method stub
		buildDynamicMBeanInfo();
		return dMBeanInfo;
	}

	@Override
	public Object invoke(String operationName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		// Check operationName to avoid NullPointerException later on
		if (operationName == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"),
					"Cannot invoke a null operation in " + dClassName);
		}
		// Call the corresponding operation for a recognized name
		if (operationName.equals("reset")) {
			// this code is specific to the internal "reset" method:
			reset(); // no parameters to check
			return null; // and no return value
		} else if (serviceOperationMap.containsKey(operationName)) {
			try {
				return serviceOperationMap.get(operationName).buildOutput(params, signature, session, state);
			} catch (OpenDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			// unrecognized operation name:
			throw new ReflectionException(new NoSuchMethodException(operationName),
					"Cannot find the operation " + operationName + " in " + dClassName);
		}
	}

	// internal variable
	private int nbResets = 0;

	// internal method for implementing the reset operation
	public void reset() {
		nbResets++;
	}

	public void doInvokeService(String serviceName, String arg) {

	}

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

		if (name.equals("Folder")) {
			// if null value, try and see if the setter returns any exception
			if (value == null) {
				try {
					setFolderName(null);
				} catch (Exception e) {
					throw (new InvalidAttributeValueException("Cannot set attribute " + name + " to null"));
				}
			}
			// if non null value, make sure it is assignable to the attribute
			else if (String.class.isAssignableFrom(value.getClass())) {
				setFolderName((String) value);
			} else {
				throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a "
						+ value.getClass().getName() + " object, String expected");
			}
		} else {
			// unrecognized attribute name
			throw new AttributeNotFoundException("Attribute " + name + " not found in " + this.getClass().getName());
		}
	}

	private void buildDynamicMBeanInfo() {

		Constructor[] constructors = this.getClass().getConstructors();
		this.dConstructors = new MBeanConstructorInfo[1];
		this.dConstructors[0] = new MBeanConstructorInfo("Constructs a " + "ServicesDynamicMBean object",
				constructors[0]);

		this.beanAttributesList = new ArrayList<MBeanAttributeInfo>();
		this.serviceOperationMap = new HashMap<String, ServiceOperation>();

		addReadOnlyStringAttribute("Folder", "Folder name to scan for services");
		addReadOnlyStringAttribute("Package", "Package name");
		addReadOnlyStringAttribute("Service", "Service name");

		addSimpleInvokeServiceOperation("reset", new String[] {});
		addSimpleInvokeServiceOperation("someService", new String[] { "inputValue" });
		addSimpleInvokeServiceOperation("someOtherService", new String[] { "inputValue1", "inputValue2" });
		addComplexInvokeServiceOperation(this.serviceName);

		dMBeanInfo = new MBeanInfo(this.getClass().getName(), "SIS IS JUST A TEST",
				beanAttributesList.toArray(new MBeanAttributeInfo[0]), dConstructors, getOperations(),
				new MBeanNotificationInfo[0]);
	}

	List<MBeanAttributeInfo> beanAttributesList = null;
	Map<String, ServiceOperation> serviceOperationMap = null;
	MBeanConstructorInfo[] dConstructors = null;
	private MBeanInfo dMBeanInfo = null;

	private MBeanOperationInfo[] getOperations() {
		List<MBeanOperationInfo> operations = new ArrayList<MBeanOperationInfo>();
		for (String serviceName : serviceOperationMap.keySet()) {
			ServiceOperation op = serviceOperationMap.get(serviceName);
			operations.add(op.createOperation());
		}
		return operations.toArray(new MBeanOperationInfo[0]);
	}

	private void addSimpleInvokeServiceOperation(String serviceName, String[] inputVariables) {
		InvokeSimpleServiceOperation op = new InvokeSimpleServiceOperation(serviceName);
		for (String input : inputVariables) {
			op.addInputParameter(input);
		}
		serviceOperationMap.put(serviceName, op);
	}

	private void addComplexInvokeServiceOperation(String serviceName) {
		InvokeServiceOperation op;
		try {
			op = new InvokeServiceOperation(serviceName);
			serviceOperationMap.put(serviceName, op);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not create mbean for service " + serviceName + ": " + e);
		}
	}

	private void addReadOnlyAttribute(String name, String type, String description) {
		MBeanAttributeInfo attribute = new MBeanAttributeInfo(name, "java.lang.String", description, true, true, false);
		beanAttributesList.add(attribute);
	}

	private void addReadOnlyStringAttribute(String name, String description) {
		this.addReadOnlyAttribute(name, "java.lang.String", description);
	}

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
