package wx.platformMonitoring.impl.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-06 16:53:53 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.text.ParseException;
import com.wm.lang.ns.NSField;
// --- <<IS-END-IMPORTS>> ---

public final class flow

{
	// ---( internal utility methods )---

	final static flow _instance = new flow();

	static flow _newInstance() { return new flow(); }

	static flow _cast(Object o) { return (flow)o; }

	// ---( server methods )---




	public static final void preserveOutputSig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(preserveOutputSig)>> ---
		// @sigtype java 3.5
		com.wm.lang.ns.NSService callingService = Service.getCallingService();
		String packageName = callingService.getPackage().getName();
		if( "WmRoot".equals(packageName) ) {
		    com.wm.app.b2b.server.InvokeState invokeState = com.wm.app.b2b.server.InvokeState.getCurrentState();
		    if (invokeState != null) {
		        com.wm.lang.flow.FlowState flowState = invokeState.getFlowState();
		        if (flowState != null) {
		            com.wm.lang.flow.FlowElement flowElement = flowState.current();
		            if (flowElement != null) {
		                com.wm.lang.flow.FlowElement flowRoot = flowElement.getFlowRoot();
		                String flowName = flowRoot.getNSName();
		                callingService = com.wm.app.b2b.server.ns.Namespace.getService(com.wm.lang.ns.NSName.create(flowName));
		            }
		        }    
		    }
		}
		IDataCursor pipelineCursor = pipeline.getCursor();        
		
		
		// Initializes the output document that will contain the output fields of the
		// current service's signature
		IData saved = IDataFactory.create();
		IDataCursor savedCursor = saved.getCursor();
		
		// If there is a calling service...
		
		  //Map calling Service Output SigVals to Variable Saved
		   if (callingService.getSignature() != null 	&& 
			callingService.getSignature().getOutput() != null && 
			callingService.getSignature().getOutput().getFields() != null) {
		    for (NSField f : callingService.getSignature().getOutput().getFields()) {
			  String attName = f.getName();
			  Object o = IDataUtil.get(pipelineCursor,attName);
			  if (o != null) {
				IDataUtil.put(savedCursor,attName,o);
			  }
		    }
			  savedCursor.destroy();
			}
			
			//Go to first element of pipeline
		pipelineCursor.first();
		//Remove all pipeline elements
		while(pipelineCursor.delete());
		//add all saved values to pipeline
		if(saved != null) {
		  IDataUtil.append(saved, pipeline);
		}
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

