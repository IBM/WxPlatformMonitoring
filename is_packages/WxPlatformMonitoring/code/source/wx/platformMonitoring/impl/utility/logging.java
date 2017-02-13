package wx.platformMonitoring.impl.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-13 18:39:40 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
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
		
		Logger log = Logger.getLogger(logger);
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
}

