package com.softwareag.wx.platformMonitoring.jmx;

import com.softwareag.wx.is.jmx.agent.common.CustomAgent;
import com.softwareag.wx.is.jmx.agent.common.CustomAgentException;

/**
 * This class loads the included beans and registers them with the MBean server.
 * @author Christian Schuit, Software AG 20120
 */
public class IntegrationServerAgent extends CustomAgent {

    public IntegrationServerAgent() throws CustomAgentException {
        
        super("jmx-is-agent.properties");
    }
}
