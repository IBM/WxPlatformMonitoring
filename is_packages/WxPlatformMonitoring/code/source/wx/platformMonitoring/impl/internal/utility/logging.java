package wx.platformMonitoring.impl.internal.utility;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class logging

{
	// ---( internal utility methods )---

	final static logging _instance = new logging();

	static logging _newInstance() { return new logging(); }

	static logging _cast(Object o) { return (logging)o; }

	// ---( server methods )---




	public static final void logMessage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logMessage)>> ---
		// @sigtype java 3.5
		// [i] field:0:required logMessage
		// [i] field:0:required severity {"TRACE","DEBUG","INFO","WARN","ERROR","FATAL"}
		// [i] field:0:optional logger
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	logMessage = IDataUtil.getString( pipelineCursor, "logMessage" );
		String	severity = IDataUtil.getString( pipelineCursor, "severity" );
		String logger = IDataUtil.getString( pipelineCursor, "logger" );
		String prefix = IDataUtil.getString( pipelineCursor, "prefix" );
		pipelineCursor.destroy();
		
		if( prefix != null ) {
			logMessage = prefix + " " + logMessage;
		}
		
		if( logger == null ) {
			logger = "wx.platformMonitoring";
		}
		
		Logger log = logCtx.getLogger(logger);
		if( severity.equalsIgnoreCase("warn") ) {
			log.warn(logMessage);
		} else if( severity.equalsIgnoreCase("error") ) {
			log.error(logMessage);
		} else if( severity.equalsIgnoreCase("fatal") ) {
			log.fatal(logMessage);
		} else if( severity.equalsIgnoreCase("info") ) {
			log.info(logMessage);
		} else if( severity.equalsIgnoreCase("debug") ) {
			log.debug(logMessage);
		} else {
			log.trace(logMessage);
		}
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void shutdown (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(shutdown)>> ---
		// @sigtype java 3.5
		if (logCtx != null) {
			logCtx.stop();
		} else {
			throw new ServiceException("Logger '" + PACKAGE_NAME + "' not initialized");
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void startup (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(startup)>> ---
		// @sigtype java 3.5
		// Note: add java parameter -Dlog4j.debug (in the custom_wrapper.conf) to debug log4j. The debug records are logged to the wrapper.log
				
		File log4jConfigFile = new File(LOG4J_CFG);
		
		if (log4jConfigFile.exists() && log4jConfigFile.canRead()) {
		
			ConfigurationFactory factory =  XmlConfigurationFactory.getInstance();
			ConfigurationSource configurationSource = null;
			try {
				configurationSource = new ConfigurationSource(new FileInputStream(log4jConfigFile),log4jConfigFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Configuration configuration = factory.getConfiguration(logCtx, configurationSource);
			
			// Get context instance
			logCtx = new LoggerContext(PACKAGE_NAME);
			
			// Start context
			logCtx.start(configuration);
		} else {
			throw new ServiceException("Configuration file '" + LOG4J_CFG + "' does not exist or cannot be read");
		}		
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static final String PACKAGE_NAME = "WxPlatformMonitoring";
	private static final String LOG4J_CFG = "./packages/" + PACKAGE_NAME + "/config/log4j2.xml";
	private static LoggerContext logCtx  = null;
		
	// --- <<IS-END-SHARED>> ---
}

