package wx.platformMonitoring.impl.utility.file;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-02-06 16:56:55 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class pub

{
	// ---( internal utility methods )---

	final static pub _instance = new pub();

	static pub _newInstance() { return new pub(); }

	static pub _cast(Object o) { return (pub)o; }

	// ---( server methods )---




	public static final void stringToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(stringToFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required fileName
		// [i] field:0:required data
		// [i] field:0:optional append {"false","true"}
		// [i] field:0:optional encoding
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	fileName = IDataUtil.getString( pipelineCursor, "fileName" );
		String	data = IDataUtil.getString( pipelineCursor, "data" );
		String	append = IDataUtil.getString( pipelineCursor, "append" );
		String	encoding = IDataUtil.getString( pipelineCursor, "encoding" );
		pipelineCursor.destroy();
		
		if( fileName == null || fileName.equals("") ) {
			throw new ServiceException("WxUtilities.stringToFile: fileName must not be empty.");
		}
		if( append == null ) {
			append = "false"; 
		}
		if( !(append.equalsIgnoreCase("true") || append.equalsIgnoreCase("false") ) ) {
			throw new ServiceException("WxUtilities.stringToFile: append must either be 'true' or 'false'. If not set, default is 'false'.");
		}
		boolean boolAppend = false;
		if( append.equalsIgnoreCase("true") ) {
			boolAppend = true;
		}
		PrintWriter out = null; 
		try {
		    File file = new File(fileName);
		    FileOutputStream fos = new FileOutputStream(file, boolAppend );
		    OutputStreamWriter osw = null;
		    if( encoding != null ) {
		    	osw = new OutputStreamWriter(fos, encoding);
		    } else {
			    osw = new OutputStreamWriter(fos);
		    }
		    out = new PrintWriter(osw);
		    out.print(data);
		} catch (IOException e) {
		    throw new ServiceException("WxUtilities.stringToFile: an exception occured when trying to write to file '" + fileName + "': " + e);
		} finally {
			if( out != null ) {
				out.close();
			}
		}
		// --- <<IS-END>> ---

                
	}
}

