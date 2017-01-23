package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-23 19:37:52 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.Enumeration;
import javax.jms.Destination;
import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nFindResult;
import com.pcbsys.nirvana.client.nNamedObject;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nQueueDetails;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.nAdminAPI.nContainer;
import com.pcbsys.nirvana.nAdminAPI.nLeafNode;
import com.pcbsys.nirvana.nAdminAPI.nNode;
import com.pcbsys.nirvana.server.store.nNamedSubscriber;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.dispatcher.DispatchFacade;
import com.wm.app.b2b.server.jms.ConnectionAlias;
import com.wm.app.b2b.server.jms.JMSSubsystem;
import com.wm.app.b2b.server.jms.RuntimeConfiguration;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class nAdmin

{
	// ---( internal utility methods )---

	final static nAdmin _instance = new nAdmin();

	static nAdmin _newInstance() { return new nAdmin(); }

	static nAdmin _cast(Object o) { return (nAdmin)o; }

	// ---( server methods )---




	public static final void getQueuedElementsChannel (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsChannel)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required topicName
		// [o] field:0:required outstandingEvents
		// [o] field:0:required isSharedDurable {"true","false"}
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			String	channelName = IDataUtil.getString( pipelineCursor, "channelName" );
			
			if( RNAME == null || "".equals(RNAME) )  {
				throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
			}
			if( channelName == null || "".equals(channelName) ) {
				throw new ServiceException("channelName must not be null.");
			}
			
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
			nSession      mySession = nSessionFactory.create(nsa);
			mySession.init();
		
			nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(channelName);
		    nChannel channel = mySession.findChannel(attrib);
		    if( channel.getNamedObjects().length != 0 ) {
		    	IDataUtil.put(pipelineCursor, "outstandingEvents", channel.getNamedObjects()[0].getSharedNamedObjectOutstandingEvents() + "");
		    	IDataUtil.put(pipelineCursor, "isSharedDurable", "true");
		    } else {
		    	IDataUtil.put(pipelineCursor, "outstandingEvents", channel.getEventCount() + "");
		    	IDataUtil.put(pipelineCursor, "isSharedDurable", "false");
		    }
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsJms (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsJms)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required topicName
		// [o] field:0:required outstandingEvents
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			String	jmsQueueName = IDataUtil.getString( pipelineCursor, "jmsQueueName" );
			
			if( RNAME == null || "".equals(RNAME) )  {
				throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
			}
			if( jmsQueueName == null || "".equals(jmsQueueName) ) {
				throw new ServiceException("jmsQueueName must not be null.");
			}
			
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
			nSession mySession = nSessionFactory.create(nsa);
			mySession.init();
		
			nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(jmsQueueName);
		    nFindResult[] findResult = mySession.find(new nChannelAttributes[] {attrib});
		    if( findResult.length == 0 ) {
		    	throw new ServiceException("JMS queue " + jmsQueueName + " was not found on realm " + RNAME);
		    }
		    if( findResult[0].isChannel() ) {
		    	IDataUtil.put( pipelineCursor, "topicName", jmsQueueName );
		    	getQueuedElementsChannel(pipeline);
		    } else if( findResult[1].isQueue() ) {
		    	IDataUtil.put( pipelineCursor, "queueName", jmsQueueName );
		    	getQueuedElementsJmsQueue(pipeline);
		    } else {
		    	throw new ServiceException("JMS queue " + jmsQueueName + " on realm " + RNAME + " is neither queue nor channel.");
		    }
		    pipelineCursor.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsJmsQueue (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsJmsQueue)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required queueName
		// [o] field:0:required outstandingEvents
		// [o] field:0:required queueStorageSize
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			String	queueName = IDataUtil.getString( pipelineCursor, "queueName" );
			
			if( RNAME == null || "".equals(RNAME) )  {
				throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
			}
			if( queueName == null || "".equals(queueName) ) {
				throw new ServiceException("queueName must not be null.");
			}
			
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
			nSession      mySession = nSessionFactory.create(nsa);
			mySession.init();
		
			nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(queueName);
		    nQueue queue = mySession.findQueue(attrib);
		    nQueueDetails details = queue.getDetails();
		    IDataUtil.put(pipelineCursor, "outstandingEvents", details.getNoOfEvents() + "");
		    IDataUtil.put(pipelineCursor, "queueStorageSize", details.getTotalMemorySize() + "");
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsJmsTopic (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsJmsTopic)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required topicName
		// [o] field:0:required outstandingEvents
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			String	topicName = IDataUtil.getString( pipelineCursor, "topicName" );
			
			if( RNAME == null || "".equals(RNAME) )  {
				throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
			}
			if( topicName == null || "".equals(topicName) ) {
				throw new ServiceException("topicName must not be null.");
			}
			
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
			nSession      mySession = nSessionFactory.create(nsa);
			mySession.init();
		
			nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(topicName);
		    nChannel channel = mySession.findChannel(attrib);
		    IDataUtil.put(pipelineCursor, "outstandingEvents", channel.getEventCount() + "");
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsSharedDurable (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsSharedDurable)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required channelName
		// [o] field:0:required outstandingEvents
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			String	channelName = IDataUtil.getString( pipelineCursor, "channelName" );
			
			if( RNAME == null || "".equals(RNAME) )  {
				throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
			}
			if( channelName == null || "".equals(channelName) ) {
				throw new ServiceException("channelName must not be null.");
			}
			
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
			nSession      mySession = nSessionFactory.create(nsa);
			mySession.init();
		
			nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(channelName);
		    nChannel channel = mySession.findChannel(attrib);
		    for( nNamedObject no : channel.getNamedObjects() ) {
		    	// we expect that a native messaging trigger (and therefore UM queue only has one named object, i.e. the trigger client id
		    	IDataUtil.put(pipelineCursor, "outstandingEvents", no.getSharedNamedObjectOutstandingEvents() + "");
		    }
		    pipelineCursor.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static void traverseNodes(nContainer container, ArrayList<IData> pNodeList, nSession pSession) throws Exception{
		
	Enumeration<nNode> children = container.getNodes();
	
	ArrayList<nLeafNode> topicList = new ArrayList<nLeafNode>();
	ArrayList<nLeafNode> qList = new ArrayList<nLeafNode>();
	while (children.hasMoreElements()) {
		nNode child = (nNode)children.nextElement();
		if (child instanceof nContainer) {
			traverseNodes((nContainer)child, pNodeList, pSession);
		} 
		else if (child instanceof nLeafNode) {
			nLeafNode leaf = (nLeafNode)child;
			if (leaf.isChannel()) {
				topicList.add(leaf);
				
			} else if (leaf.isQueue()) {
				qList.add(leaf);
				
				
				
			}
		}
		}
	
	try{Thread.sleep(5000);}catch(Exception e){}
	for(nLeafNode leaf : topicList){
		IDataMap idm = new IDataMap(IDataFactory.create());
		idm.put("Name", leaf.getAbsolutePath());
		idm.put("Stored", leaf.getCurrentNumberOfEvents());
		idm.put("Current", leaf.getLastEID());
			pNodeList.add(idm.getIData());
			
			System.err.println("UM:    "+leaf.getAbsolutePath());
			
		    nChannelAttributes attrib = new nChannelAttributes();
		    attrib.setName(leaf.getAbsolutePath());
		    nChannel channel = pSession.findChannel(attrib);
		    nNamedObject[] durableSubscribers = channel.getNamedObjects();
			System.err.println("UM:    "+leaf.getAbsolutePath());
			System.err.println("UM:    "+leaf.getAbsolutePath()+" - "+durableSubscribers.length);	    
	    for(nNamedObject nNamedObject : durableSubscribers){
	    	idm = new IDataMap(IDataFactory.create());
			idm.put("Name", leaf.getAbsolutePath()+"/"+nNamedObject.getName());
			idm.put("Stored", findHiddenQueue(leaf.getAbsolutePath(),nNamedObject.getName(),pSession));
			idm.put("Current", nNamedObject.getEID());
			pNodeList.add(idm.getIData());
	    }
		
		
	}
	for(nLeafNode leaf : qList){
		IDataMap idm = new IDataMap(IDataFactory.create());
		idm.put("Name", leaf.getAbsolutePath());
		idm.put("Stored", leaf.getCurrentNumberOfEvents());
		idm.put("Current", leaf.getLastEID());
		pNodeList.add(idm.getIData());
	}
	
	
	
	}
	
	private static long findHiddenQueue(String parent, String name, nSession session)
	{
	            try
	            {
	                        nChannelAttributes attrib = new nChannelAttributes();
	                        attrib.setName(parent);
	                        nChannel channel = session.findChannel(attrib);
	
	
	                        com.pcbsys.nirvana.base.events.nManageNamedSub.nNamedObjectHelper 
	helper = 
	(com.pcbsys.nirvana.base.events.nManageNamedSub.nNamedObjectHelper)channel.getNamedObject(name);
	                        long id = helper.getId();
	
	                        attrib.setName("/" + nNamedSubscriber.sharedQueueFolder + "/" + parent + "/" 
	+ "NamedSubscriber" + id);
	                        nQueue hidden = session.findQueue(attrib);
	                        return hidden.size();
	            }
	            catch (Exception e)
	            {
	                        System.err.println(e.getMessage());
	                        return -1;
	            }
	
	}
	// --- <<IS-END-SHARED>> ---
}

