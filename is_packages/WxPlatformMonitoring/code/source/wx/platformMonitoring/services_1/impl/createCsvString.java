package wx.platformMonitoring.services_1.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:06 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.services.CValues;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class createCsvString

{
	// ---( internal utility methods )---

	final static createCsvString _instance = new createCsvString();

	static createCsvString _newInstance() { return new createCsvString(); }

	static createCsvString _cast(Object o) { return (createCsvString)o; }

	// ---( server methods )---




	public static final void createCsvStringFromTemplate (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(createCsvStringFromTemplate)>> ---
		// @sigtype java 3.5
		// [i] record:0:required template
		// [i] record:0:required document
		// [o] field:0:required csvString
			
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
			// template
			IData	template = IDataUtil.getIData( pipelineCursor, "template" );
			if ( template == null)
			{
				throw new ServiceException("template must not be null");
			}
		
			// document
			IData	document = IDataUtil.getIData( pipelineCursor, "document" );
			if ( document == null)
			{
				throw new ServiceException("document must not be null");
			}
		pipelineCursor.destroy();
		
		StringBuffer csvString = new StringBuffer();
		iterateDoc(csvString, template, document);
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1,"csvString", csvString.toString() );
		pipelineCursor_1.destroy();
		
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	public static String DELIMITER = ";";
	
	private static void iterateDoc(StringBuffer csvString, IData template, IData document) {
		
		IDataCursor templateC = template.getCursor();
		IDataCursor documentC = document.getCursor();
		
		templateC.first();
		
		while( true ) {
		try {
		String templateKey = templateC.getKey();
		Object templateValue = templateC.getValue();
		Object documentValue = IDataUtil.get(documentC, templateKey);
	
		if( documentValue == null ) 
			System.out.println("null");
		
		if( templateValue instanceof String ) {
			csvString.append(documentValue);
			csvString.append(DELIMITER);
		} else if( templateValue instanceof IData[] ) { 
	        IData[] castTemplateValue = (IData[])templateValue;
	        IData[] castDocumentValue = new IData[castTemplateValue.length];
	        if( documentValue instanceof CValues ) {
	        	castDocumentValue[0] = (IData)documentValue;
	        } else if( documentValue instanceof IData[] ) {
	        	castDocumentValue = (IData[])documentValue;
	        }
	        for (int j = 0; j < castTemplateValue.length; j++) {
	        	if( castDocumentValue[j] == null ) {
	        		castDocumentValue[j] = IDataFactory.create();
	        	}
				iterateDoc(csvString, (IData)castTemplateValue[j], castDocumentValue[j]); 
	        } 
	      } else if( templateValue instanceof IData ) {
		        IData castTemplateValue = (IData)templateValue;
		        if( documentValue == null ) {
		        	documentValue = IDataFactory.create();
		        }
		        IData castDocumentValue = (IData)documentValue;
				iterateDoc(csvString, (IData)castTemplateValue, castDocumentValue); 
		         
	      }
		
			if( templateC.hasMoreData() ) {
				templateC.next();
			}
		} catch( NullPointerException np ) {
			System.out.println(np);
		}
		}
	
		
		
	}
		
	// --- <<IS-END-SHARED>> ---
}

