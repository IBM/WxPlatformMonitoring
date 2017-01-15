package wx.platformMonitoring.services_1.impl.processes;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:17 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class step

{
	// ---( internal utility methods )---

	final static step _instance = new step();

	static step _newInstance() { return new step(); }

	static step _cast(Object o) { return (step)o; }

	// ---( server methods )---




	public static final void genericInvokeService (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(genericInvokeService)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required serviceFQN
		// [i] record:0:required serviceInputPipeline
		// [o] record:0:required serviceOutputPipeline
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	serviceFQN = IDataUtil.getString( pipelineCursor, "serviceFQN" );
		IData	serviceInputPipeline = IDataUtil.getIData( pipelineCursor, "serviceInputPipeline" );
		
		
		String[] service = serviceFQN.split(":");
		IData output = null;
		try{
			output = Service.doInvoke( service[0], service[1], serviceInputPipeline );
		}catch( Exception e){
			throw new ServiceException(e);
		}
		
		
		IDataUtil.put(pipelineCursor, "serviceOutputPipeline", output);
		
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

