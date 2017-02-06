package wx.platformMonitoring.impl.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-06 16:54:46 CET
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

public final class string

{
	// ---( internal utility methods )---

	final static string _instance = new string();

	static string _newInstance() { return new string(); }

	static string _cast(Object o) { return (string)o; }

	// ---( server methods )---




	public static final void mapToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(mapToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:required in
		// [o] field:0:required out
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			Object	in = IDataUtil.get( pipelineCursor, "in" );
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "out", in.toString() );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void regexMatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(regexMatch)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inString
		// [i] field:0:required pattern
		// [o] field:0:required doesMatch
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	inString = IDataUtil.getString( pipelineCursor, "inString" );
		String	pattern = IDataUtil.getString( pipelineCursor, "pattern" );
		
		if( pattern == null)
			throw new ServiceException( "Es wurde kein Pattern angegeben! Pattern ist null.");
		
		if( inString == null)
			throw new ServiceException( "inString ist null. Matching gegen Regex nicht m\u00F6glich!");
		
		boolean doesMatch = inString.matches( pattern);
		
		IDataUtil.put( pipelineCursor, "doesMatch", doesMatch?"true":"false" );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

