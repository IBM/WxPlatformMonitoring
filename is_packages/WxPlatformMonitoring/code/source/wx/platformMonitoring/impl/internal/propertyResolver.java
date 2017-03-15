package wx.platformMonitoring.impl.internal;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import javax.naming.InvalidNameException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class propertyResolver

{
	// ---( internal utility methods )---

	final static propertyResolver _instance = new propertyResolver();

	static propertyResolver _newInstance() { return new propertyResolver(); }

	static propertyResolver _cast(Object o) { return (propertyResolver)o; }

	// ---( server methods )---




	public static final void defaultExtendedSettingsPropertyResolver (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(defaultExtendedSettingsPropertyResolver)>> ---
		// @specification wx.platformMonitoring.impl.internal.propertyResolver:propertyResolverSpecification
		// @sigtype java 3.5
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	propertyName = IDataUtil.getString( pipelineCursor, "propertyName" );
				
		IData input = IDataFactory.create();
		IDataCursor inputC = input.getCursor();
		IDataUtil.put(inputC, "propertyName", propertyName);
		inputC.destroy();
		
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "pub.utils", "getServerProperty", input );
		}catch( Exception e){
			pipelineCursor.destroy();
			throw new ServiceException("Could not resolve proeprty " + propertyName + " using default property resolver:"  + e);
		}
		IDataCursor outputCursor = output.getCursor();
		String	propertyValue = IDataUtil.getString( outputCursor, "propertyValue" );
		outputCursor.destroy();
		
		IDataUtil.put( pipelineCursor, "propertyValue", propertyValue );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void registerPropertyResolver (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerPropertyResolver)>> ---
		// @sigtype java 3.5
		// [i] field:0:required resolverServiceFqn
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	resolverServiceFqn = IDataUtil.getString( pipelineCursor, "resolverServiceFqn" );
		pipelineCursor.destroy();
		
		if( resolverServiceFqn == null ) {
			throw new ServiceException("Provide a resolver service.");
		}
		
		try {
			propertyResolver.registerResolverService(resolverServiceFqn);
		} catch(InvalidNameException ine)  {
			throw new ServiceException("Invalid resolver service '" + resolverServiceFqn + "': "  +
		 ine);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void resolveProperty (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(resolveProperty)>> ---
		// @specification wx.platformMonitoring.impl.internal.propertyResolver:propertyResolverSpecification
		// @sigtype java 3.5
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	propertyName = IDataUtil.getString( pipelineCursor, "propertyName" );
		pipelineCursor.destroy();
		
		String propertyValue = propertyResolver.resolveProperty(propertyName);
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "propertyValue", propertyValue );
		pipelineCursor_1.destroy();
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static com.softwareag.wx.platformMonitoring.propertyResolver.PropertyResolver propertyResolver = com.softwareag.wx.platformMonitoring.propertyResolver.PropertyResolver.getInstance();
		
	// --- <<IS-END-SHARED>> ---
}

