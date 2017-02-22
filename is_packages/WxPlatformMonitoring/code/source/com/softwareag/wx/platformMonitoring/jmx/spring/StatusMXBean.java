package com.softwareag.wx.platformMonitoring.jmx.spring;

public interface StatusMXBean {
	   public StatusEnum getStatus();

	   public void setStatus(final StatusEnum status);

	   public enum StatusEnum{ SUCCESSFUL, FAILURE, UNSPECIFIED }
}
