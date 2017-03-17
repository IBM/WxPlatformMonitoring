package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class http

{
	// ---( internal utility methods )---

	final static http _instance = new http();

	static http _newInstance() { return new http(); }

	static http _cast(Object o) { return (http)o; }

	// ---( server methods )---




	public static final void generateUrl (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(generateUrl)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required connection wx.platformMonitoring.impl.http:connection
		// [o] field:0:required url
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// connection
		IData	connection = IDataUtil.getIData( pipelineCursor, "connection" );
		if ( connection == null) {
			throw new ServiceException("Provide a connection to create an URL from.");
		}
		IDataCursor connectionCursor = connection.getCursor();
		String	protocol = IDataUtil.getString( connectionCursor, "protocol" );
		String	host = IDataUtil.getString( connectionCursor, "host" );
		String	port = IDataUtil.getString( connectionCursor, "port" );
		String	path = IDataUtil.getString( connectionCursor, "path" );
		connectionCursor.destroy();
		pipelineCursor.destroy();
		
		if( protocol == null || "".equals(protocol) ) {
			throw new ServiceException("Provide a protocol (e.g. http)");
		}
		
		if( host == null || "".equals(host) ) {
			throw new ServiceException("Provide a host");
		}
		
		if( host.endsWith("/") ) {
			host = host.substring(0, host.length()-1);
		}
		
		String url = protocol + "://" + host;
		if( port != null ) {
			url += ":" + port;
		}
		if( path != null ) {
			if( path.startsWith("/") ) {
				path = path.substring(1, path.length());
			}
			url += "/" + path;
		}
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "url", url );
		pipelineCursor_1.destroy();
		
			
		// --- <<IS-END>> ---

                
	}
}

