package wx.platformMonitoring.services_1.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:21 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class test

{
	// ---( internal utility methods )---

	final static test _instance = new test();

	static test _newInstance() { return new test(); }

	static test _cast(Object o) { return (test)o; }

	// ---( server methods )---




	public static final void generateIDataFromTracePipeline (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(generateIDataFromTracePipeline)>> ---
		// @sigtype java 3.5
		// [i] field:0:required tracePipelineAsString
		IDataCursor idc = pipeline.getCursor();
		String tracePipelineAsString = IDataUtil.getString(idc, "tracePipelineAsString");
		
		
		String[] lines = tracePipelineAsString.split("\\n");
		
		parseLine(pipeline, lines, 0, 0);
		
		
		IDataUtil.remove(idc, "tracePipelineAsString");
		// --- <<IS-END>> ---

                
	}



	public static final void invokeService (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeService)>> ---
		// @sigtype java 3.5
		// [i] field:0:required serviceFQN
		IDataCursor idc = pipeline.getCursor();
		String serviceFQN = IDataUtil.getString(idc, "serviceFQN");
		
		String[] service = serviceFQN.split(":");
		IData 	output = IDataFactory.create();
		try {
			output = Service.doInvoke( service[0], service[1], pipeline );
		} catch( Exception e){
			throw new ServiceException();
		}
		pipeline = output;
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static int parseLine(IData curretDoc, String[] lines, int lineIndex, int level) {
		
		IDataCursor curretDocC = curretDoc.getCursor();
	
		for( ; lineIndex < lines.length; ) {
	
			String line = lines[lineIndex];
			
			String[] lineSplit = line.split(" ");
			int currentLevel = Integer.parseInt(lineSplit[0]);
			if( currentLevel < level ) {
				return lineIndex;
			}
			lineIndex++;
			String key = lineSplit[1];
			String type = lineSplit[2];
			if( type.equals("=") && line.endsWith("null")) {
				IDataUtil.put(curretDocC, key, null);
			} else if( type.contains("com.wm.data.IData") || type.contains("com.wm.data.ISMemDataImpl") || type.contains("com.wm.app.b2b.services.CValues") ) {
				IData childDoc = IDataFactory.create();
				IDataUtil.put(curretDocC, key, childDoc);
				lineIndex = parseLine(childDoc, lines, lineIndex, level+1);
			} else {
				if( type.contains("java.lang.String") ) {
					String value = lineSplit[4];
					value = value.substring(1, value.length()-1);
					IDataUtil.put(curretDocC, key, value);
				}
			}
		
		}
		curretDocC.destroy();
		return lineIndex;
	}
		
	// --- <<IS-END-SHARED>> ---
}

