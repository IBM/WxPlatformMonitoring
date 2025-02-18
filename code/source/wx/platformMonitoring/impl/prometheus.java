package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class prometheus

{
	// ---( internal utility methods )---

	final static prometheus _instance = new prometheus();

	static prometheus _newInstance() { return new prometheus(); }

	static prometheus _cast(Object o) { return (prometheus)o; }

	// ---( server methods )---




	public static final void currentTimeMillis (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(currentTimeMillis)>> ---
		// @sigtype java 3.5
		IDataUtil.put(pipeline.getCursor(), "currentTimeMillis", new java.util.Date().getTime());
		// --- <<IS-END>> ---

                
	}
}

