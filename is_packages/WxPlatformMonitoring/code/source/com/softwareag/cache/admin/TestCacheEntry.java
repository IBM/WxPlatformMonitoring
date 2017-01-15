package com.softwareag.cache.admin;

import java.io.Serializable;

//import com.sun.org.apache.xml.internal.utils.SerializableLocatorImpl;

public class TestCacheEntry implements Serializable {

	private String testValue = null;

	public TestCacheEntry(String testValue) {
		this.testValue = testValue;
	}
	
	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}
	
	
}
