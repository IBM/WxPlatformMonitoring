package com.softwareag.wx.platformMonitoring.jmx.spring;

public class Status2 implements StatusMXBean {
	   private StatusEnum statusEnum = StatusEnum.UNSPECIFIED;

	   public Status2() {}

	   public StatusEnum getStatus()
	   {
	      return this.statusEnum;
	   }

	   public void setStatus(final StatusEnum status)
	   {
	      this.statusEnum = status;
	   }
}
