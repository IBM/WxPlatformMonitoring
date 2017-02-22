package com.softwareag.wx.platformMonitoring.jmx.agent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.softwareag.wx.is.jmx.agent.common.AgentConfiguration;
import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomAttributeException;
import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomMXBeanFactory;
import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomMXBeanImpl;

public class WxPlatformMonitoringMXBeanFactory extends CustomMXBeanFactory {

    public WxPlatformMonitoringMXBeanFactory(AgentConfiguration config) {

        super(config);
    }

    @Override
    public List<CustomMXBeanImpl> createMXBeans() throws CustomAttributeException {
        List<CustomMXBeanImpl> beans = new ArrayList<CustomMXBeanImpl>();
        beans.add(new WxPlatformMonitoringMXBean());
        return beans;
    }

}
