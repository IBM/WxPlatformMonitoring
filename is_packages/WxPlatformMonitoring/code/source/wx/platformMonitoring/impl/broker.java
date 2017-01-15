package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 12:48:51 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import COM.activesw.api.client.*;
// --- <<IS-END-IMPORTS>> ---

public final class broker

{
	// ---( internal utility methods )---

	final static broker _instance = new broker();

	static broker _newInstance() { return new broker(); }

	static broker _cast(Object o) { return (broker)o; }

	// ---( server methods )---




	public static final void getQueueLengthForAllClientIds (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueueLengthForAllClientIds)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required brokerConnectionData reiff_administration.documents.broker:BrokerConnectionData
		// [o] recref:1:required brokerClientQueueLengthList reiff_administration.documents.broker:BrokerClientQueueLength
		// init params
		String brokerURI = null;
		String brokerClientGroup = null;
		String brokerAppName = null;
		String brokerName = null;
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// connectionData
		IData	connectionData = IDataUtil.getIData( pipelineCursor, "brokerConnectionData" );
		if ( connectionData != null)
		{
			IDataCursor connectionDataCursor = connectionData.getCursor();
			brokerURI = IDataUtil.getString( 
						connectionDataCursor, "brokerURI" );
			brokerClientGroup = IDataUtil.getString( 
						connectionDataCursor, "brokerClientGroup" );
			brokerAppName = IDataUtil.getString( 
						connectionDataCursor, "brokerAppName" );
			brokerName = IDataUtil.getString( 
					connectionDataCursor, "brokerName" );
			connectionDataCursor.destroy();
		}
		pipelineCursor.destroy();
		
		BrokerAllQueueStat[] stats;
		Object[][] cq = {{"clientID",null},{"queueLength",null}};
		ArrayList<IData> brokerClientQueueLengthList = new ArrayList<IData>();
		BrokerAdminClient brokerAdminClient = null;
		try {
			brokerAdminClient = new BrokerAdminClient(
					brokerURI, brokerName, null, brokerClientGroup, 
					brokerAppName, null);
			stats = brokerAdminClient.getAllQueueStats();
			for (int i = 0; i < stats.length; i++) {
				cq[0][1] = stats[i].getClientID();
				cq[1][1] = String.valueOf(stats[i].getQueueLength());
				brokerClientQueueLengthList.add(
						IDataFactory.create( cq));
			}
		} catch ( BrokerException be) {
			throw new ServiceException( be);
		} finally {
			if( brokerAdminClient != null ) {
				try {
					brokerAdminClient.disconnect();
				} catch (BrokerException e) {
					// do nothing, we are in the finally block
				}
			}
		}
		
		IDataCursor pcOutput = pipeline.getCursor();
		IDataUtil.put( pcOutput, "brokerClientQueueLengthList", 
				brokerClientQueueLengthList.toArray( 
				new IData[0]));
		pcOutput.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getQueueSizeForAllClientIds (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueueSizeForAllClientIds)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required brokerConnectionData reiff_administration.documents.broker:BrokerConnectionData
		// [o] recref:1:required brokerClientQueueSizeList reiff_administration.documents.broker:BrokerClientQueueSize
		// [o] field:0:required totalSize
		// init params
		String brokerURI = null;
		String brokerClientGroup = null;
		String brokerAppName = null;
		String brokerName = null;
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		// connectionData
		IData	connectionData = IDataUtil.getIData( pipelineCursor, "brokerConnectionData" );
		if ( connectionData != null)
		{
			IDataCursor connectionDataCursor = connectionData.getCursor();
			brokerURI = IDataUtil.getString( 
						connectionDataCursor, "brokerURI" );
			brokerClientGroup = IDataUtil.getString( 
						connectionDataCursor, "brokerClientGroup" );
			brokerAppName = IDataUtil.getString( 
						connectionDataCursor, "brokerAppName" );
			brokerName = IDataUtil.getString( 
					connectionDataCursor, "brokerName" );
			connectionDataCursor.destroy();
		}
		pipelineCursor.destroy();
		
		BrokerAllQueueStat[] stats;
		Object[][] cq = {{"clientID",null},{"queueSize",null}};
		ArrayList<IData> brokerClientQueueSizeList = new ArrayList<IData>();
		BrokerAdminClient brokerAdminClient = null;
		long totalSize = 0;
		try {
			brokerAdminClient = new BrokerAdminClient(brokerURI, brokerName,
					null, brokerClientGroup, brokerAppName, null);
			stats = brokerAdminClient.getAllQueueStats();
			for (int i = 0; i < stats.length; i++) {
				long size = stats[i].getQueueStat().getLongField(
						"queueByteSize");
				totalSize += size;
				cq[0][1] = stats[i].getClientID();
				cq[1][1] = String.valueOf(size);
				brokerClientQueueSizeList.add(IDataFactory.create(cq));
			}
			brokerClientQueueSizeList.add(
					IDataFactory.create( cq));
			Collections.sort(brokerClientQueueSizeList, new Comparator<IData>() {
		
				@Override
				public int compare(IData a, IData b) {
					IDataCursor curA = a.getCursor();
					long sizeA = IDataUtil.getLong(curA, "queueSize", 0);
					curA.destroy();
					
					IDataCursor curB = b.getCursor();
					long sizeB = IDataUtil.getLong(curB, "queueSize", 0);
					curB.destroy();
					return sizeA < sizeB ? 1 : sizeA > sizeB ? -1 : 0;
				}
			});
		} catch ( BrokerException be) {
			throw new ServiceException( be);
		} finally {
			if( brokerAdminClient != null ) {
				try {
					brokerAdminClient.disconnect();
				} catch (BrokerException e) {
					// do nothing, we are in the finally block
				}
			}
		}
		
		IDataCursor pcOutput = pipeline.getCursor();
		IDataUtil.put( pcOutput, "brokerClientQueueSizeList", 
				brokerClientQueueSizeList.toArray( 
				new IData[0]));
		IDataUtil.put( pcOutput, "totalSize", 
				String.valueOf(totalSize));
		pcOutput.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

