package wx.platformMonitoring.impl.internal.utility.file;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-03-14 12:49:18 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.Closeable;
// --- <<IS-END-IMPORTS>> ---

public final class impl

{
	// ---( internal utility methods )---

	final static impl _instance = new impl();

	static impl _newInstance() { return new impl(); }

	static impl _cast(Object o) { return (impl)o; }

	// ---( server methods )---




	public static final void deleteFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(deleteFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [o] field:0:required deleted {"false","true"}
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		pipelineCursor.destroy();
		
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		if( baseDir.exists() && !baseDir.isDirectory() ) {
			throw new ServiceException("Provide a valid directory which exists.");
		} else if( !baseDir.exists() ) {
			baseDir.mkdir();
		}
		
		File contextDir = new File(baseDir, context);
		if( !contextDir.exists() ) {
			contextDir.mkdir();
		}
		
		String deleted = "false";
		File targetFile = new File(contextDir, filename);
		if( targetFile.exists() ) {
			targetFile.delete();
			deleted = "true";
		}
		
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "deleted", deleted );
		pipelineCursor_1.destroy();			
		// --- <<IS-END>> ---

                
	}



	public static final void getFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [i] field:0:optional encoding
		// [o] field:0:required stringOut
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		String	encoding = IDataUtil.getString( pipelineCursor, "encoding" );
		pipelineCursor.destroy();
		
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		if( baseDir.exists() && !baseDir.isDirectory() ) {
			throw new ServiceException("Provide a valid directory which exists.");
		} else if( !baseDir.exists() ) {
			baseDir.mkdir();
		}
		
		File contextDir = new File(baseDir, context);
		if( !contextDir.exists() ) {
			contextDir.mkdir();
		}
		
		File targetFile = new File(contextDir, filename);
		if( !targetFile.exists() ) {
			return;
		}
		
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = null;
		try {
			if( encoding != null ) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), encoding));
			} else {
				reader = new BufferedReader(new FileReader(targetFile));
			}
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1){
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
		} catch( FileNotFoundException fnfe ) {
			throw new ServiceException("Exception when reading file " + targetFile + ": " + fnfe);
		} catch( IOException ioe )  {
			throw new ServiceException("Exception when reading file " + targetFile + ": " + ioe);
		} finally {
			try {
			reader.close();
			} catch( IOException e ) {
				// ignore
			}
		}
		String stringOut = fileData.toString();
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "stringOut", stringOut );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getFileDetails (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFileDetails)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [o] field:0:required path
		// [o] object:0:required modificationDate
		// [o] field:0:required size
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		File contextDir = new File(baseDir, context);
		File targetFile = new File(contextDir, filename);
		if( !targetFile.exists() ) {
			return;
		}
		
		// pipeline
		IDataUtil.put( pipelineCursor, "path", targetFile.getAbsolutePath() );
		IDataUtil.put( pipelineCursor, "modificationDate", new java.util.Date(targetFile.lastModified()) );
		IDataUtil.put( pipelineCursor, "size", targetFile.length() );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getJavaTmpDir (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getJavaTmpDir)>> ---
		// @sigtype java 3.5
		// [o] field:0:required tmpDir
		IDataUtil.put(pipeline.getCursor(), "tmpDir", System.getProperty("java.io.tmpdir"));
		// --- <<IS-END>> ---

                
	}



	public static final void getPathForNewFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPathForNewFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [o] field:0:required path
		// [o] field:0:required fileExists {"false","true"}
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		pipelineCursor.destroy();
		
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		if( baseDir.exists() && !baseDir.isDirectory() ) {
			throw new ServiceException("Provide a valid directory which exists.");
		} else if( !baseDir.exists() ) {
			baseDir.mkdirs();
		}
		
		File contextDir = new File(baseDir, context);
		if( !contextDir.exists() ) {
			contextDir.mkdirs();
		}
		
		File targetFile = new File(contextDir, filename);
		String fileExists = "false";
		if( targetFile.exists() ) {
			fileExists = "true";
		//			throw new ServiceException("File '" + filename + "' for context '" + context + "'  already exists in tmpDir.");
		}
		
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "path", targetFile.getAbsolutePath() );
		IDataUtil.put( pipelineCursor_1, "fileExists", fileExists );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getPathToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPathToFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [o] field:0:required path
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		pipelineCursor.destroy();
		
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		if( baseDir.exists() && !baseDir.isDirectory() ) {
			throw new ServiceException("Provide a valid directory which exists.");
		} else if( !baseDir.exists() ) {
			baseDir.mkdir();
		}
		
		File contextDir = new File(baseDir, context);
		if( !contextDir.exists() ) {
			contextDir.mkdir();
		}
		
		File targetFile = new File(contextDir, filename);
		if( !targetFile.exists() ) {
			return;
		}
		
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "path", targetFile.getAbsolutePath() );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void moveFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(moveFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filenameFrom
		// [i] field:0:optional contextFrom
		// [i] field:0:optional filenameTo
		// [i] field:0:optional contextTo
		// [i] field:0:required dir
		// [o] field:0:required success
		IDataMap p = new IDataMap(pipeline);
		
		String dir = p.getAsString("dir"),
				filenameFrom = p.getAsString("filenameFrom"),
				filenameTo = p.getAsString("filenameTo", filenameFrom),
				contextFrom = p.getAsString("contextFrom", "default"),
				contextTo = p.getAsString("contextTo", contextFrom);
		
		if(dir == null || filenameFrom == null) {
			p.put("success", "false");
			return;
		}
		
		File fromFile = new File(dir, contextFrom + "/" + filenameFrom);
		if(!fromFile.isFile()) {
			p.put("success", "false");
			return;
		}
		
		File toPath = new File(dir, contextTo);
		File toFile = new File(toPath, filenameTo);
		
		try {
			if(!toPath.exists()) {
				toPath.mkdirs();
			}
			fromFile.renameTo(toFile);
			p.put("success", "true");
		} catch(Exception e) {
			p.put("success", "false");
		}
		// --- <<IS-END>> ---

                
	}



	public static final void touchFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(touchFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:optional context
		// [i] field:0:required dir
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		pipelineCursor.destroy();
		
		if( filename == null || dir == null ) {
			throw new ServiceException("Both filename and directory must be provided.");
		}
		if( context == null ) {
			context = "default";
		}
		
		File targetFile = new File(dir, context + "/" + filename);
		if( !targetFile.exists() ) {
			try {
				// first create all relevant directories
				new File(dir, context).mkdirs();
				// now create the new file
				targetFile.createNewFile();
			} catch (IOException e) {
				throw new ServiceException("Error when creating file with name '" + filename + "' in context '" + context + "': " + e);
			}
		}
		// set the modification date to now 
		targetFile.setLastModified(System.currentTimeMillis());
			
		// --- <<IS-END>> ---

                
	}



	public static final void writeStringToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(writeStringToFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required string
		// [i] field:0:required filename
		// [i] field:0:required append {"true","false"}
		// [i] field:0:optional addNewLine
		// [i] field:0:optional context
		// [i] field:0:required dir
		// [i] field:0:optional encoding
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	string = IDataUtil.getString( pipelineCursor,"string" );
		String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		String	append = IDataUtil.getString( pipelineCursor, "append" );
		String	context = IDataUtil.getString( pipelineCursor, "context" );
		String	dir = IDataUtil.getString( pipelineCursor, "dir" );
		String	encoding = IDataUtil.getString( pipelineCursor, "encoding" );
		String	addNewLine = IDataUtil.getString( pipelineCursor, "addNewLine" );
		pipelineCursor.destroy();
		
		if( string == null ) {
			throw new ServiceException("Input string must not be null (but can be empty)");
		}
		if( filename == null ) {
			throw new ServiceException("Filename must be provided.");
		}
		if( append == null ) {
			append = "true";
		}
		if( !(append.equals("true") || append.equals("false")) ) {
			append = "true";
		}
		if( context == null ) {
			context = "default";
		}
		if( addNewLine == null ) {
			addNewLine = "false";
		}
		if( dir == null ) {
			throw new ServiceException("Directory must be provided.");
		}
		
		File baseDir = new File(dir);
		if( baseDir.exists() && !baseDir.isDirectory() ) {
			throw new ServiceException("Provide a valid directory which exists.");
		} else if( !baseDir.exists() ) {
			baseDir.mkdir();
		}
		
		File contextDir = new File(baseDir, context);
		if( !contextDir.exists() ) {
			contextDir.mkdirs();
		}
		
		File targetFile = new File(contextDir, filename);
		if( targetFile.exists() && append.equals("false") ) {
			targetFile.delete();
		}
		//		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		try {
			boolean appendBool = append.equals("true") ? true : false;
			boolean addNewLineBool = addNewLine.equals("true") ? true : false;
		//			FileWriter fw = new FileWriter(targetFile, appendBool);
		//			bw = new BufferedWriter(fw);
		//			bw.write(string);			
			FileOutputStream fos = new FileOutputStream(targetFile, appendBool);
			if( encoding == null || "".equals(encoding) ) {
				osw = new OutputStreamWriter(fos);	
			} else {
				osw = new OutputStreamWriter(fos, Charset.forName(encoding).newEncoder());
			}
			if( addNewLineBool ) {
				string = string + System.getProperty("line.separator");
			}
			osw.write(string);
		} catch (IOException e) {
		    throw new ServiceException("Exception occured when writing to file " + targetFile + ": " + e);
		} finally {
			try {
				osw.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
			
		// --- <<IS-END>> ---

                
	}
}

