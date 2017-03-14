package wx.platformMonitoring.impl.internal.utility;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-03-14 12:49:16 CET
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

public final class date

{
	// ---( internal utility methods )---

	final static date _instance = new date();

	static date _newInstance() { return new date(); }

	static date _cast(Object o) { return (date)o; }

	// ---( server methods )---




	public static final void calculateDateDifferenceByDate (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(calculateDateDifferenceByDate)>> ---
		// @sigtype java 3.5
		// [i] object:0:required startDate
		// [i] object:0:required endDate
		// [o] field:0:required dateDifferenceSeconds
		// [o] field:0:required dateDifferenceMinutes
		// [o] field:0:required dateDifferenceHours
		// [o] field:0:required dateDifferenceDays
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		Date	startDate = (Date)IDataUtil.get( pipelineCursor, "startDate" );
		Date	endDate = (Date)IDataUtil.get( pipelineCursor, "endDate" );
		
		long diff = endDate.getTime() - startDate.getTime();
		
		// pipeline
		IDataUtil.put( pipelineCursor, "dateDifferenceSeconds", diff / 1000 );
		IDataUtil.put( pipelineCursor, "dateDifferenceMinutes", diff / 1000 / 60 );
		IDataUtil.put( pipelineCursor, "dateDifferenceHours",  	diff / 1000 / 60 / 60 );
		IDataUtil.put( pipelineCursor, "dateDifferenceDays",	 diff / 1000 / 60 / 60 / 24 );
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void convertStringToDateByPattern (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(convertStringToDateByPattern)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required stringDate
		// [i] field:0:required datePattern {"yyyyMMdd","dd.MM.yyyy","yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd'Z'","yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"}
		// [o] object:0:required date
		// accessing pipeline for retrieving input parameters
		IDataCursor pipelineCursorInput = pipeline.getCursor();
			String	stringDate = IDataUtil.getString( pipelineCursorInput, "stringDate" );
			String	pattern = IDataUtil.getString( pipelineCursorInput, "datePattern" );
		pipelineCursorInput.destroy();
		// accessing pipeline for filling output parameters 
		IDataCursor pipelineCursorOutput = pipeline.getCursor();
		
		if(pattern==null){
			throw new ServiceException("The patter for parsing the date must not be null");
		}
		if(stringDate==null || stringDate.length()==0){
			IDataUtil.put( pipelineCursorOutput, "date", null );
			pipelineCursorOutput.destroy();
			return;
		}
		
		try {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date date = simpleDateFormat.parse(stringDate);
			IDataUtil.put( pipelineCursorOutput, "date", date );
			pipelineCursorOutput.destroy();
		
		} catch (final ParseException parseException) {
			throw new ServiceException(parseException);
		}
			
			
			
		// --- <<IS-END>> ---

                
	}
}

