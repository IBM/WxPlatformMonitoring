package wx.platformMonitoring.impl.internal.utility;

// -----( IS Java Code Template v1.2

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

public final class list

{
	// ---( internal utility methods )---

	final static list _instance = new list();

	static list _newInstance() { return new list(); }

	static list _cast(Object o) { return (list)o; }

	// ---( server methods )---




	public static final void contains (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(contains)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:1:required stringArray
		// [i] field:0:required value
		// [o] field:0:required isIn
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String[]       stringArray = IDataUtil.getStringArray( pipelineCursor, "stringArray" );
		String               value = IDataUtil.getString( pipelineCursor, "value" );
		
		String contains;
		if( stringArray == null ) {
			contains = "true";
		} else {
			contains = "false";
			for( String stringElem : stringArray ) {
				if( stringElem.equals(value) )  {
					contains = "true";
					break;
				}
			}
		}
		IDataUtil.put( pipelineCursor, "isIn", contains );
		//		
		//		Set<String> s = new HashSet<String>();
		//		s.addAll( Arrays.asList( stringArray) );
		//		
		//		IDataUtil.put( pipelineCursor, "isIn", "" + s.contains( value ) );
		pipelineCursor.destroy();
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void docListToStringList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(docListToStringList)>> ---
		// @sigtype java 3.5
		// [i] record:1:required docList
		// [i] field:0:required fieldName
		// [o] field:1:required stringList
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	fieldName = IDataUtil.getString( pipelineCursor, "fieldName" );
		IData[]	docList = IDataUtil.getIDataArray( pipelineCursor, "docList" );
		java.util.List<String> tmpList = new java.util.ArrayList<String>();
		if ( docList != null) {
			for ( int i = 0; i < docList.length; i++ ) {
				String field = IDataUtil.getString(docList[i].getCursor(), fieldName);
				if( field != null && !("".equals(field)) ) {
					tmpList.add(field);
				}
			}
		}
		if( tmpList.size() == 0 ) {
			return;
		}
		IDataUtil.put( pipelineCursor, "stringList", tmpList.toArray(new String[0]));
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void documentListContains (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(documentListContains)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required docList
		// [i] field:0:required docListKey
		// [i] field:0:required value
		// [o] field:0:required isIn
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IData[]       docList = IDataUtil.getIDataArray( pipelineCursor, "docList" );
		String               value = IDataUtil.getString( pipelineCursor, "value" );
		String               docListKey = IDataUtil.getString( pipelineCursor, "docListKey" );
		
		if( docList == null ) {
			docList = new IData[0];
		}
		String contains = "false";
		for( IData doc : docList ) {
			if( doc == null ) {
				continue;
			}
			String docValue = IDataUtil.getString(doc.getCursor(), docListKey);
			if( docValue == null ) {
				continue;
			}
			if( docValue.equals(value) ) {
				contains = "true";
				break;
				
			}
		}
		IDataUtil.put( pipelineCursor, "isIn", contains );
		pipelineCursor.destroy();
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void filterDuplicates (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(filterDuplicates)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required docList
		// [i] field:0:required duplicateKey
		// [o] record:1:required docList
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IData[]       docList = IDataUtil.getIDataArray( pipelineCursor, "docList" );
		String               duplicateKey = IDataUtil.getString( pipelineCursor, "duplicateKey" );
		
		if( docList == null ) {
			return;
		}
		
		java.util.Map<String, IData> keyMap = new java.util.HashMap<String, IData>();
		
		for( IData doc : docList ) {
			if( doc == null ) {
				continue;
			}
			String docValue = IDataUtil.getString(doc.getCursor(), duplicateKey);
			if( docValue == null ) {
				continue;
			}
			// duplicates get filtered in the hashMap automatically by the duplicateKey
			keyMap.put(docValue, doc);
		}
		IDataUtil.put( pipelineCursor, "docList", keyMap.values().toArray(new IData[0]) );
		pipelineCursor.destroy();
			
			
			
		// --- <<IS-END>> ---

                
	}
}

