package wx.platformMonitoring.services_1.impl.processes;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:12 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.regex.Pattern;
import java.util.regex.Matcher;
// --- <<IS-END-IMPORTS>> ---

public final class misc

{
	// ---( internal utility methods )---

	final static misc _instance = new misc();

	static misc _newInstance() { return new misc(); }

	static misc _cast(Object o) { return (misc)o; }

	// ---( server methods )---




	public static final void excelStringTableToDocument (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(excelStringTableToDocument)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required in
		// [i] field:1:required documentFields
		// [o] record:1:required docList
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	in = IDataUtil.getString( pipelineCursor, "in" );
		String[]	documentFields = IDataUtil.getStringArray( pipelineCursor, "documentFields" );
		pipelineCursor.destroy();
		
		String[] lines = in.split("\n");
		IData[] docList = new IData[lines.length];
		for( int l=0; l<lines.length; l++ ) {
			String[] cells = lines[l].split("\t");
			IData doc = IDataFactory.create();
			IDataCursor docCursor = doc.getCursor();
			for( int c=0; c<cells.length; c++ ) {
				IDataUtil.put(docCursor, documentFields[c], cells[c]);
			}
			docCursor.destroy();
			docList[l] = doc;
		}
		
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		
		IDataUtil.put( pipelineCursor_1, "docList", docList );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void int2string (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(int2string)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] object:0:required int
		// [o] field:0:required string
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			Object	int_1 = IDataUtil.get( pipelineCursor, "int" );
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "string", "" + int_1 );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void splitNewLineStringToStringList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(splitNewLineStringToStringList)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required in
		// [o] field:1:required out
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	in = IDataUtil.getString( pipelineCursor, "in" );
		pipelineCursor.destroy();
		
		String lines[] = in.split("\\r?\\n"); 
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "out", lines );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}
}

