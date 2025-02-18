package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class configResolver

{
	// ---( internal utility methods )---

	final static configResolver _instance = new configResolver();

	static configResolver _newInstance() { return new configResolver(); }

	static configResolver _cast(Object o) { return (configResolver)o; }

	// ---( server methods )---




	public static final void registerConfigResolver (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerConfigResolver)>> ---
		// @sigtype java 3.5
		// [i] field:0:required resolverServiceFqn
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	resolverServiceFqn = IDataUtil.getString( pipelineCursor, "resolverServiceFqn" );
		pipelineCursor.destroy();
		String success = "false";
		if( resolverServiceFqn == null ) {
			throw new ServiceException("Provide a resolver service.");
		}
		
		try {
			com.softwareag.wx.platformMonitoring.configResolver.ConfigResolver.registerResolverService(resolverServiceFqn);
			success = "true";
		} catch(javax.naming.InvalidNameException ine)  {
			throw new ServiceException("Invalid config resolver service '" + resolverServiceFqn + "': "  + ine);
		}
		IDataUtil.put(pipelineCursor, "success", success);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void resolveConfigKey (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(resolveConfigKey)>> ---
		// @specification wx.platformMonitoring.impl.internal.configResolver:configResolverSpecification
		// @sigtype java 3.5
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	key = IDataUtil.getString( pipelineCursor, "key" );
		String	packageName = IDataUtil.getString( pipelineCursor, "packageName" );
		
		String value = com.softwareag.wx.platformMonitoring.configResolver.ConfigResolver.getInstance().resolveConfig(key, packageName);
		
		IDataUtil.put( pipelineCursor, "value", value );
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}
}

