package com.softwareag.wx.platformMonitoring.jmx.agent;

import java.util.ArrayList;

import javax.management.MBeanConstructorInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.log4j.Logger;

import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomAttribute;
import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomAttributeException;
import com.softwareag.wx.is.jmx.agent.common.instrumentation.CustomMXBeanImpl;

import COM.activesw.api.client.BrokerAdminClient;
import COM.activesw.api.client.BrokerEvent;
import COM.activesw.api.client.BrokerException;
import COM.activesw.api.client.BrokerServerClient;

/**
 * 
 * @author Henning Waack
 *
 */
public class WxPlatformMonitoringMXBean extends CustomMXBeanImpl {

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(WxPlatformMonitoringMXBean.class.getName());

    /**
     * Bean group name
     */
    public static final String OBJECT_GROUP = "webMethods";

    /**
     * Bean name
     */
    public static final String OBJECT_NAME = "WxPlatformMonitoring";

    /**
     * Bean description
     */
    public static final String OBJECT_DESCRIPTION = "Application and platform monitoring nformation collected through WxPlatformMonitoring package on IntegrationServer";

    /**
     * Bean version
     */
    public static final String OBJECT_VERSION = "1.0";

    /**
     * Constructor
     */
    public WxPlatformMonitoringMXBean() throws CustomAttributeException {
        
        super(OBJECT_GROUP, OBJECT_NAME, OBJECT_DESCRIPTION, OBJECT_VERSION);
        logger.debug("WxPlatformMonitoring JMX Initialized");
    }
    
    @Override
    public String getJMXName() {
        
        return String.format("%s,group=%s,name=%s,WxPlatformMonitoring",JMX_DOMAIN, OBJECT_GROUP, OBJECT_NAME); 
    }
    
    @Override
    public void update() throws CustomAttributeException {

        // Get broker server details
        logger.debug("Start update WxPlatformMonitoring");

        this.addAttribute("test", "tset", "1");

        logger.debug("End update WxPlatformMonitoring");
    }
    
    
    public void updater() throws CustomAttributeException {

        // Get broker server details
        logger.debug("updaterStart update WxPlatformMonitoring");

//        this.addAttribute("test", "tset", "1");

        
        logger.debug("updaterEnd update WxPlatformMonitoring");
    }
    

    public String nochdoller(String input) {
    	System.out.println("------- doll got called WITH INPUT " + input);
    	return "dolller input: " + input;
    }
    
    @Override
    protected synchronized MBeanOperationInfo[] getOperationInfo() {
        System.out.println("-------------- getOperationInfo");
        ArrayList<MBeanOperationInfo> list = new ArrayList<MBeanOperationInfo>();
        try {
        	list.add(new MBeanOperationInfo("update", this.getClass().getMethod("update", new Class<?>[0])));
            list.add(new MBeanOperationInfo("updater", this.getClass().getMethod("updater", new Class<?>[0])));
            

            MBeanParameterInfo[] params = new MBeanParameterInfo[1];
            params[0] = new MBeanParameterInfo("operation", "java.lang.String", "The operations");
            MBeanOperationInfo dOperations = new MBeanOperationInfo(
                    "doll",                     // name
                    "Resets State and NbChanges attributes to their initial values",
                                                 // description
                    params,                      // parameter types
                    String.class.getName(),                      // return type
                    MBeanOperationInfo.INFO);  // impact
            list.add(dOperations);
        } catch (NoSuchMethodException nsme) {
            logger.error("Failed to add method to MBeanInfo", nsme);
        }
        return list.toArray(new MBeanOperationInfo[0]);
    } 
    
    public String doll(String input) {
    	System.out.println("------- doll got called WITH INPUT " + input);
    	return "dolller input: " + input;
    }
    
    
    private void getServerStats(BrokerServerClient server) 
            throws BrokerException, CustomAttributeException {
        
        BrokerEvent stats = server.getStats();
        int conns = stats.getIntegerField("numConnections");
        int connsSSL = stats.getIntegerField("numSSLConnections");
        int connsPeak = stats.getIntegerField("highestNumConnections");
        int connsSSLPeak = stats.getIntegerField("highestNumSSLConnections");
        int remainingTime = stats.getIntegerField("licenseTimeRemaining");
        boolean isDiskLow = stats.getBooleanField("isDiskSpaceLow");
        boolean isDiskVeryLow = stats.getBooleanField("isDiskSpaceVeryLow");
        
        this.addAttribute("nrOfConnections", "Number of non-SSL connections", conns);
        this.addAttribute("nrOfConnectionsSSL", "Number of SSL connections", connsSSL);
        this.addAttribute("nrOfConnectionsPeak", "Maximum number of non-SSL connections", connsPeak);
        this.addAttribute("nrOfConnectionsSSLPeak", "Maximum number of SSL connections", connsSSLPeak);
        this.addAttribute("millisRemainingTimeLicense", "Time until license expires", remainingTime * 1000);
        this.addAttribute("isDiskSpaceLow", "Available disk space < 1 MB", isDiskLow);
        this.addAttribute("isDiskSpaceVeryLow", "Available disk space < 100 KB", isDiskVeryLow);
    }

    private void getUsageStats(BrokerServerClient server) 
            throws BrokerException, CustomAttributeException {
        
        BrokerEvent stats = server.getUsageStats();
        double cpuLoad = stats.getDoubleField("percentageCPUUsed");
        long guaranteedUsed = stats.getLongField("guaranteedSpaceUsed");
        long guaranteedMax = stats.getLongField("guaranteedSpaceMax");
        long guaranteedRes = stats.getLongField("guaranteedSpaceReserved");
        long guaranteedDiskFree = stats.getLongField("guaranteedDiskFree");
        long persistentUsed = stats.getLongField("persistentSpaceUsed");
        long persistentMax = stats.getLongField("persistentSpaceMax");
        long persistentDiskFree = stats.getLongField("persistentDiskFree");
        long swapFree = stats.getLongField("swapSpaceFree");
        long swapMax = stats.getLongField("swapSpaceMax");
        long memoryMax = stats.getLongField("maxMemorySize");
        long memoryUsed = stats.getLongField("memoryUsed");
        
        this.addAttribute("percCPULoad", "Current CPU load", cpuLoad / 100);
        this.addAttribute("kilobytesGuaranteedUsed", "Used space for guaranteed messages", guaranteedUsed);
        this.addAttribute("kilobytesGuaranteedMax", "Maximum space for guaranteed messages", guaranteedMax);
        this.addAttribute("kilobytesGuaranteedReserved", "Reserved space for guaranteed messages", guaranteedRes);
        this.addAttribute("kilobytesGuaranteedFree", "Free space for guaranteed messages", (guaranteedMax - guaranteedUsed));
        this.addAttribute("percGuaranteedFree", "Percentage free space for guaranteed messages", (guaranteedUsed / guaranteedMax));
        this.addAttribute("megabytesGuaranteedDiskFree", "Free disk space for guaranteed messages", guaranteedDiskFree / 1024);
        this.addAttribute("kilobytesPersistentUsed", "Used space for persistent messages", guaranteedUsed);
        this.addAttribute("kilobytesPersistentMax", "Maximum space for persistent messages", guaranteedMax);
        this.addAttribute("kilobytesPersistentFree", "Free space for persistent messages", (persistentMax - persistentUsed));
        this.addAttribute("percPersistentFree", "Percentage free space for persistent messages", (persistentUsed / persistentMax));
        this.addAttribute("megabytesPersistentDiskFree", "Free disk space for persistent messages", persistentDiskFree / 1024);
        this.addAttribute("megabytesSwapSpaceFree", "Free swap space", swapFree / 1024);
        this.addAttribute("percSwapSpaceUsed", "Free swap space", ((swapMax - swapFree) / swapMax) / 1024);
        this.addAttribute("megabytesMemoryFree", "Free swap space", (memoryMax - memoryUsed) / 1024);
        this.addAttribute("percMemoryUsed", "Free swap space", (memoryUsed / memoryMax) / 1024);
    }

    private void getBrokerStats(BrokerAdminClient admin, int index) 
            throws BrokerException, CustomAttributeException {
        
        BrokerEvent stats = admin.getBrokerStats();
        int clients = stats.getIntegerField("numClients");
        int published = stats.getIntegerField("numEventsPublished");
        int delivered = stats.getIntegerField("numEventsDelivered");
        int queued = stats.getIntegerField("numEventsQueued");
        long queueLength = admin.getQueueLength();

        this.addAttribute("broker_" + index, "Connected broker", admin.getBrokerName());
        this.addAttribute("nrOfClients_" + index, "Number of connected clients", clients);
        this.addAttribute("nrOfEventsInQueues_" + index, "Total Broker queue length", queueLength);
        this.addAttribute("nrOfEventsPublished_" + index, "Number of published events", published);
        this.addAttribute("nrOfEventsDelivered_" + index, "Number of delivered events", delivered);
        this.addAttribute("nrOfEventsQueued_" + index, "Number of queued events", queued);
    }

    @Override
    protected final synchronized MBeanConstructorInfo[] getConstructorInfo() {
        
        ArrayList<MBeanConstructorInfo> list = new ArrayList<MBeanConstructorInfo>();
        try {
            list.add(new MBeanConstructorInfo("Constructor", this.getClass().getConstructor(String.class)));
        } catch (Exception e) {
            logger.error("Failed to add constructors to MBeanInfo", e);
        }
        return list.toArray(new MBeanConstructorInfo[0]);
    }
}
