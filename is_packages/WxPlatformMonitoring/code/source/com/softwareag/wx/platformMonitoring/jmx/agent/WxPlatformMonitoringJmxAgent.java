package com.softwareag.wx.platformMonitoring.jmx.agent;

import com.softwareag.wx.is.jmx.agent.common.CustomAgent;
import com.softwareag.wx.is.jmx.agent.common.CustomAgentException;

public class WxPlatformMonitoringJmxAgent extends CustomAgent {

    public WxPlatformMonitoringJmxAgent(String agentPropertiesFile) throws CustomAgentException {
        // provide absolute path to props file
    	super(agentPropertiesFile);
//		File configFile = new File(ServerAPI.getPackageConfigDir("WxPlatformMonitoring"), "jmx-wxplatformmonitoring-agent.properties");
		System.out.println("----- Loaded WxPlatformMonitoringJmxAgent");
    }

}
