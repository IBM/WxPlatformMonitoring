package wx.platformMonitoring.impl.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-06 16:54:10 CET
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
		
		Set<String> s = new HashSet<String>();
		s.addAll( Arrays.asList( stringArray) );
		
		IDataUtil.put( pipelineCursor, "isIn", "" + s.contains( value ) );
		pipelineCursor.destroy();
			
			
		// --- <<IS-END>> ---

                
	}
}

