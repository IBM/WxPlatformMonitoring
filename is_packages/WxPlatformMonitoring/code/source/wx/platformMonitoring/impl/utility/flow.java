package wx.platformMonitoring.impl.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-13 18:28:55 CET
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
import java.util.Stack;
import com.wm.app.b2b.server.InvokeState;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSService;
import com.wm.util.coder.IDataCodable;
// --- <<IS-END-IMPORTS>> ---

public final class flow

{
	// ---( internal utility methods )---

	final static flow _instance = new flow();

	static flow _newInstance() { return new flow(); }

	static flow _cast(Object o) { return (flow)o; }

	// ---( server methods )---




	public static final void extractResourcePathFromServiceName (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(extractResourcePathFromServiceName)>> ---
		// @sigtype java 3.5
		// [i] field:0:required callingService
		// [o] field:0:required resourcePath
		IDataCursor pipelineC = pipeline.getCursor();
		String callingService = IDataUtil.getString(pipelineC, "callingService");
		IDataUtil.put(pipelineC, "resourcePath", callingService.substring(0, callingService.indexOf(":")));
		pipelineC.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getCallingServiceDetails (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCallingServiceDetails)>> ---
		// @sigtype java 3.5
		// [o] recref:0:required serviceDetails wx.restServices.impl.documents:serviceDetails
		String callingServiceName = "undefined";
		
		// get callingServiceName
		IDataCursor pipelineCursor = pipeline.getCursor();
		try {
			Stack stack = InvokeState.getCurrentState().getCallStack();
			// iterate over the stack and find the first service which is not from this WxRestServices package
			for( int i=stack.size()-1; i>=0; i--) {
				callingServiceName = stack.get(i).toString();
				if( callingServiceName.startsWith("wx.restService") ) {
					// still a wx.restservice service, continue
					continue;
				} else  {
					// we have found the first non-wx.restserivice service
					break;
				}
			}
		} catch(Exception e) {
			if( callingServiceName.equals("undefined") ) {
					com.wm.app.b2b.server.InvokeState invokeState = com.wm.app.b2b.server.InvokeState.getCurrentState();
					if (invokeState != null) {
						com.wm.lang.flow.FlowState flowState = invokeState.getFlowState();
						if (flowState != null) {
							com.wm.lang.flow.FlowElement flowElement = flowState.current();
							if (flowElement != null) {
								com.wm.lang.flow.FlowElement flowRoot = flowElement.getFlowRoot();
								String flowName = flowRoot.getNSName();
								com.wm.app.b2b.server.BaseService bs = com.wm.app.b2b.server.ns.Namespace.getService(com.wm.lang.ns.NSName.create(flowName));
								callingServiceName = bs.getNSName().getFullName();
							}
						}   
					}
			}	
		}	
		String currentContextID = "";
		try{
			String[] contextStack = InvokeState.getCurrentState().getAuditRuntime().getContextStack();
			currentContextID = contextStack[0];
			/*
			if(contextStack!=null) {
				if(contextStack.length >=3) {
					currentContextID = contextStack[2];
				} else if (contextStack.length>=2) {
					currentContextID = contextStack[1];
				} else if (contextStack.length>=1) {
					currentContextID = contextStack[0];
				} 
			}
			*/
		} catch(Exception ex) {
			// ignore exception, just return no context Id
			currentContextID = "n/a";
		}
		
		IData serviceDetails = IDataFactory.create();
		IDataCursor serviceDetailsC = serviceDetails.getCursor();
		IDataUtil.put( serviceDetailsC, "contextId", currentContextID );
		IDataUtil.put( serviceDetailsC, "restServiceName", callingServiceName );
		serviceDetailsC.destroy();
		IDataUtil.put(pipelineCursor, "serviceDetails", serviceDetails);
		pipelineCursor.destroy();
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void getResponseString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getResponseString)>> ---
		// @sigtype java 3.5
		// [o] field:0:required responseString
		com.wm.app.b2b.server.InvokeState is = com.wm.app.b2b.server.InvokeState.getCurrentState();
		byte response[] = (byte[])is.getPrivateData("$msgBytesOut");
		if( response != null ) {
			String responseString = new String(response);
			IDataUtil.put(pipeline.getCursor(), "responseString", responseString);
		}
		// --- <<IS-END>> ---

                
	}



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



	public static final void tracePipelineToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(tracePipelineToString)>> ---
		// @sigtype java 3.5
		// [i] record:0:required doc
		// [i] field:1:required ignore
		// [o] field:0:required pipelineAsString
		IDataCursor pipelineC = pipeline.getCursor();
		String[] ignore = IDataUtil.getStringArray(pipelineC, "ignore");
		java.util.Set<String> ignoreSet = new java.util.HashSet<String>();
		if( ignore != null ) {
			for( String i : ignore ) {
				ignoreSet.add(i);
			}
		}
		IDataUtil.remove(pipelineC, "ignore");
		StringBuilder buffer = new StringBuilder(); 
		// used as tree to drill down the pipeline 
		java.util.Hashtable<Object, Object> table = new java.util.Hashtable<Object, Object>();
		table.put(pipeline, pipeline); 
		dumpIData(buffer, 0, pipeline, 0, table, ignoreSet); 
		
		pipelineC.insertAfter("pipelineAsString", buffer.toString());
		
		pipelineC.destroy(); 
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static final String NL = System.getProperty("line.separator"); 
	private static void dumpIData(StringBuilder sb, int level, IData pipeline, int nextLevel, java.util.Hashtable<Object, Object> table, java.util.Set<String> ignoreSet) { 
	    if (pipeline == null) { 
	      return; 
	    } 
	    IDataCursor cursor = pipeline.getCursor(); 
	    for (int i = 0; cursor.next(); i++) { 
	      String str = cursor.getKey(); 
	      if( ignoreSet.contains(str)) {
	    	  continue;
	      }
	      Object currentValue = cursor.getValue(); 
	      if ((currentValue != null) && (table.get(currentValue) != null)) { 
	        sb.append(NL) 
	                .append(str.toString()) 
	                .append(" {") 
	                        .append(((Object)currentValue).getClass().getName()) 
	                .append("}"); 
	        return; 
	      } 
	      if ((currentValue instanceof IDataCodable)) { 
	        currentValue = ((IDataCodable)currentValue).getIData(); 
	        if ((currentValue != null) && (table.get(currentValue) != null)) { 
	          sb.append(NL)
	          			.append("[")
	                  .append(new Integer(nextLevel).toString()) 
	                  .append("] ") 
	                  .append(str.toString()); 
	          return; 
	        } 
	      } 
	      Object[] castValue; 
	      int j; 
	      if ((currentValue instanceof String[][])) { 
	            String[][] localObject3 = (String[][])currentValue; 
	        for (j = 0; j < localObject3.length; j++) { 
	          for (int k = 0; k < localObject3[0].length; k++) { 
	            sb 
	                    .append(NL)
	                    .append("[")
	                    .append(nextLevel) 
	                    .append("]")
	                    .append(" ").append(str); 
	                    /*for (int z = 0; z < j; z++) { 
	                            sb.append("\t"); 
	                    }*/ 
	                    sb.append(j) 
	                            .append("[") 
	                                    .append(k) 
	                            .append("] {java.lang.String[][]} = ") 
	                            .append("'") 
	                                    .append(localObject3[j][k]) 
	                            .append("'"); 
	          } 
	        } 
	      } 
	      else if ((currentValue instanceof String[])) { 
	        castValue = (String[])currentValue; 
	        for (j = 0; j < castValue.length; j++) { 
	                sb.append(NL); 
	      			sb.append("[" + nextLevel + "]"); 
	                        for (int z = 0; z < nextLevel; z++) { 
	//	                                sb.append("\t"); 
	                        };                                 
	                        sb.append(" ") 
	                        .append(str);                         
	                sb.append("{java.lang.String[]} = ") 
	                        .append("'") 
	                                .append(castValue[j]) 
	                        .append("'"); 
	        } 
	      } 
	      else if ((currentValue instanceof IData[])) 
	      { 
	        castValue = (IData[])currentValue; 
	        for (j = 0; j < castValue.length; j++) { 
	          if (castValue[j] != null) { 
	            table.put(castValue[j], castValue[j]); 
	          } 
	          sb.append(NL); 
				sb.append("[" + nextLevel + "]"); 
	                  for (int z = 0; z < nextLevel; z++) { 
	//	                        sb.append("\t"); 
	                };                           
	                  sb.append(" ") 
	                  .append(str);                   
	          sb.append(((Object)currentValue).getClass().getName()).append("} => "); 
	          // recursion down the tree 
	          dumpIData(sb, level, (IData)castValue[j], nextLevel + 1, table, ignoreSet); 
	          if (castValue[j] != null) { 
	            table.remove(castValue[j]); 
	          } 
	        } 
	      } else if ((currentValue instanceof IData)) { 
	        table.put(currentValue, currentValue); 
	        sb.append(NL); 
			sb.append("[" + nextLevel + "]"); 
	                for (int z = 0; z < nextLevel; z++) { 
	//	                        sb.append("\t"); 
	                }                         
	                sb.append(" ") 
	                        .append(str) 
	                .append(" {") 
	                .append(((Object)currentValue).getClass().getName()) 
	                .append("} => "); 
	        // recursion down the tree 
	        dumpIData(sb, level, (IData)currentValue, nextLevel + 1, table, ignoreSet); 
	        table.remove(currentValue); 
	      } 
	      else if ((currentValue instanceof IDataCodable[])) { 
	        castValue = (IDataCodable[])currentValue; 
	        for (j = 0; j < castValue.length; j++) { 
	          if (castValue[j] != null) { 
	            table.put(castValue[j], castValue[j]); 
	          } 
	          sb.append(NL); 
	          			sb.append("[" + nextLevel + "]"); 
	                  for (int z = 0; z < nextLevel; z++) { 
	//	                          sb.append("\t"); 
	                  } 
	                  sb.append(" ") 
	                          .append(str) 
	                  .append("[") 
	                          .append(j) 
	                  .append("] {") 
	                          .append(((Object)currentValue).getClass().getName()) 
	                  .append("} => "); 
	          // recursion down the tree 
	          dumpIData(sb, level, ((IDataCodable)castValue[j]).getIData(), nextLevel + 1, table, ignoreSet); 
	          if (castValue[j] != null) { 
	            table.remove(castValue[j]); 
	          } 
	        } 
	      } 
	      else if (currentValue != null) { 
	        sb.append(NL); 
	                sb.append("[" + nextLevel + "]"); 
	                for (int z = 0; z < nextLevel; z++) { 
	//	                          sb.append("\t"); 
	                  } 
	                sb.append(" ") 
	                .append(str) 
	                        .append(" {") 
	                                .append(((Object)currentValue).getClass().getName()) 
	                        .append("} = ") 
	                .append("'") 
	                        .append(currentValue) 
	                .append("'"); 
	      } else { 
	        sb.append(NL + "[" + nextLevel + "] " + str + " = null"); 
	      } 
	    } 
	    cursor.destroy(); 
	  } 
	// --- <<IS-END-SHARED>> ---
}

