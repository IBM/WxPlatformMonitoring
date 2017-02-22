package com.softwareag.wx.platformMonitoring.jmx.spring;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
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
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public class SimpleDynamic implements DynamicMBean {

	String dClassName = this.getClass().getName();

	@Override
	public Object getAttribute(String attribute_name)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		// Check attribute_name to avoid NullPointerException later on
		if (attribute_name == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"),
					"Cannot invoke a getter of " + dClassName + " with null attribute name");
		}

		// Call the corresponding getter for a recognized attribute_name
		if (attribute_name.equals("State")) {
			return getState();
		}
		if (attribute_name.equals("NbChanges")) {
			return getNbChanges();
		}
		if (attribute_name.equals("QuoteSnapshot")) {
			return (TabularData) getQuoteSnapshot();
		}

		// If attribute_name has not been recognized
		throw (new AttributeNotFoundException("Cannot find " + attribute_name + " attribute in " + dClassName));
	}

	// internal methods for getting attributes
	public String getState() {
		return state;
	}

	public Integer getNbChanges() {
		return new Integer(nbChanges);
	}

	// internal variables representing attributes
	private String state = "initial state";
	private int nbChanges = 0;

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
		if (operationName.equals("resetAndGetQuoteSnapshot")) {
			try {
				return buildSnapshot(); // This is where you delegate the
										// invokation to your original method.
			} catch (OpenDataException e) {
				throw new MBeanException(e, "invoking resetAndGetQuoteSnapshot: " + e.getClass().getName() + "caught ["
						+ e.getMessage() + "]");
			}
		}

		// Call the corresponding operation for a recognized name
		if (operationName.equals("reset")) {
			// this code is specific to the internal "reset" method:
			reset(); // no parameters to check
			return null; // and no return value
		} else {
			// unrecognized operation name:
			throw new ReflectionException(new NoSuchMethodException(operationName),
					"Cannot find the operation " + operationName + " in " + dClassName);
		}
	}

	public Object buildSnapshot() throws OpenDataException {
		CompositeType snapshotType = null;
		TabularType quoteTableType = null;
		try {
			String[] index = { "symbol" };
			snapshotType = new CompositeType("snapshot", "Quote Data", itemNames, itemDescriptions, itemTypes);
			quoteTableType = new TabularType("quoteSnapshots", "List of Quotes", snapshotType, index);
			quoteSnapshot = new TabularDataSupport(quoteTableType);
		} catch (OpenDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		quoteSnapshot = new TabularDataSupport(quoteTableType);
		// SomeVo vo = //get the data
		Object[] itemValues = { "vo.symbol", "vo.localDate", "vo.response" };
		CompositeData result = new CompositeDataSupport(snapshotType, itemNames, itemValues);
		quoteSnapshot.put(result);
		return quoteSnapshot;
	}

	// internal variable
	private int nbResets = 0;

	// internal method for implementing the reset operation
	public void reset() {
		AttributeChangeNotification acn = new AttributeChangeNotification(this, 0, 0, "NbChanges reset", "NbChanges",
				"Integer", new Integer(nbChanges), new Integer(0));
		state = "initial state";
		nbChanges = 0;
		nbResets++;
		// sendNotification(acn);
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

		if (name.equals("State")) {
			// if null value, try and see if the setter returns any exception
			if (value == null) {
				try {
					setState(null);
				} catch (Exception e) {
					throw (new InvalidAttributeValueException("Cannot set attribute " + name + " to null"));
				}
			}
			// if non null value, make sure it is assignable to the attribute
			else if (String.class.isAssignableFrom(value.getClass())) {
				setState((String) value);
			} else {
				throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a "
						+ value.getClass().getName() + " object, String expected");
			}
		}
		// recognize an attempt to set a read-only attribute
		else if (name.equals("NbChanges")) {
			throw new AttributeNotFoundException("Cannot set attribute " + name + " because it is read-only");
		}

		// unrecognized attribute name
		else {
			throw new AttributeNotFoundException("Attribute " + name + " not found in " + this.getClass().getName());
		}
	}

	// internal method for setting attribute
	public void setState(String s) {
		state = s;
		nbChanges++;
	}

	private void buildDynamicMBeanInfo() {
		CompositeType snapshotType = null;
		TabularType quoteTableType = null;
		try {
			String[] index = { "symbol" };
			snapshotType = new CompositeType("snapshot", "Quote Data", itemNames, itemDescriptions, itemTypes);
			quoteTableType = new TabularType("quoteSnapshots", "List of Quotes", snapshotType, index);
			quoteSnapshot = new TabularDataSupport(quoteTableType);
		} catch (OpenDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dAttributes[0] = new MBeanAttributeInfo("State", "java.lang.String", "State string.", true, true, false);
		dAttributes[1] = new MBeanAttributeInfo("NbChanges", "java.lang.Integer",
				"Number of times the " + "State string has been changed.", true, false, false);
		dAttributes[2] = new OpenMBeanAttributeInfoSupport("QuoteSnapshot", "Table of quotes data", snapshotType, true,
				false, false);

		Constructor[] constructors = this.getClass().getConstructors();
		dConstructors[0] = new MBeanConstructorInfo("Constructs a " + "SimpleDynamic object", constructors[0]);

		MBeanParameterInfo[] params = null;
		dOperations[0] = new MBeanOperationInfo("reset",
				"reset State and NbChanges " + "attributes to their initial values", params, "void",
				MBeanOperationInfo.ACTION);

		MBeanParameterInfo[] params1 = new MBeanParameterInfo[1];
		params1[0] = new MBeanParameterInfo("inputValue", "java.lang.String", "give me input");
		dOperations[1] = new MBeanOperationInfo("invokeOPeration", "Invoke a service", params1, "java.lang.String",
				MBeanOperationInfo.ACTION);

		dOperations[2] = new MBeanOperationInfo("invokeOPeration2", "Invoke a service", params1, "java.lang.String",
				MBeanOperationInfo.ACTION);

		// No arg constructor

		// Just one operation

		OpenMBeanParameterInfo[] paramsQ = new OpenMBeanParameterInfoSupport[0];
		dOperations[3] = new OpenMBeanOperationInfoSupport("resetAndGetQuoteSnapshot",
				"Reset and get the latest available data for the Quotes", paramsQ, snapshotType,
				MBeanOperationInfo.INFO);

		dNotifications[0] = new MBeanNotificationInfo(new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
				AttributeChangeNotification.class.getName(),
				"This notification is emitted when the reset() method is called.");

		dMBeanInfo = new MBeanInfo(this.getClass().getName(), "SIS IS JUST A TEST", dAttributes, dConstructors,
				dOperations, dNotifications);
	}

	String[] itemNames = { "symbol", "localTimeStamp", "latestResponse" };
	String[] itemDescriptions = { "Symbol", "The Timestamp for the response as on server",
			"The response String from Quoteserver" };
	OpenType[] itemTypes = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING };
//	CompositeType snapshotType;
//
//	TabularType quoteTableType;
	private TabularDataSupport quoteSnapshot;

	// PRIVATE VARIABLES
	private MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[3];
	private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
	private MBeanNotificationInfo[] dNotifications = new MBeanNotificationInfo[1];
	private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[4];
	private MBeanInfo dMBeanInfo = null;

	public TabularDataSupport getQuoteSnapshot() {
		return quoteSnapshot;
	}

	public void setQuoteSnapshot(TabularDataSupport quoteSnapshot) {
		this.quoteSnapshot = quoteSnapshot;
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
