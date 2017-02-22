package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-25 13:33:22 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nNamedObject;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.nAdminAPI.nContainer;
import com.pcbsys.nirvana.nAdminAPI.nLeafNode;
import com.pcbsys.nirvana.nAdminAPI.nNode;
import com.pcbsys.nirvana.nAdminAPI.nRealmNode;
import java.util.Enumeration;
// --- <<IS-END-IMPORTS>> ---

public final class nAdmin

{
	// ---( internal utility methods )---

	final static nAdmin _instance = new nAdmin();

	static nAdmin _newInstance() { return new nAdmin(); }

	static nAdmin _cast(Object o) { return (nAdmin)o; }

	// ---( server methods )---




	public static final void getQueuedElements (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElements)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required queueName
		// [o] field:0:required outstandingEvents
		// [o] record:1:required sharedDurableOutstandingEvents
		// [o] - field:0:required outstandingEvents
		// [o] - field:0:required namedObject
		// [o] field:0:required outstandingEventsSharedDurableMax
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
		    com.pcbsys.nirvana.client.nFindResult[] findResult = mySession.find(new nChannelAttributes[] {attrib});
		    if( findResult.length == 0 ) {
		    	throw new ServiceException("Qeueu" + queueName+ " was not found on realm " + RNAME);
		    }
		    if( findResult[0].isChannel() ) {
		    	// if it is a channel, then it is a jms topic
		    	IDataUtil.put( pipelineCursor, "channelName", queueName);
		    	getQueuedElementsChannel(pipeline);
		    } else if( findResult[0].isQueue() ) {
		    	// this is a jms queue
		    	getQueuedElementsJmsQueue(pipeline);
		    } else {
		    	throw new ServiceException("Queue " + queueName + " on realm " + RNAME + " is neither queue nor channel.");
		    }
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsChannel (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsChannel)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required channelName
		// [o] field:0:required outstandingEvents
		// [o] record:1:required sharedDurableOutstandingEvents
		// [o] - field:0:required outstandingEvents
		// [o] - field:0:required namedObject
		// [o] field:0:required outstandingEventsSharedDurableMax
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
		    // check for shared durable
		
		    if( channel.getNamedObjects().length != 0 ) {
		    	/*
		    	 * A standard wM messaging trigger has a shared durable connection and does "concurrent" processing.
		    	 * For such a trigger hidden queues are created, which are Named Objects in UM. 
		    	 */
		    	IData[] sharedDurableOutstandingEvents = new IData[channel.getNamedObjects().length];
		    	long max = 0;
		    	nNamedObject[] namedObjects = channel.getNamedObjects();
		    	for( int i=0; i<sharedDurableOutstandingEvents.length; i++ ) {
		    		nNamedObject no = namedObjects[i];
		    		sharedDurableOutstandingEvents[i] = IDataFactory.create();
		    		IDataCursor sharedDurableOutstandingEventsC = sharedDurableOutstandingEvents[i].getCursor();
		    		long outstandingEvents = no.getSharedNamedObjectOutstandingEvents();
		    		IDataUtil.put(sharedDurableOutstandingEventsC, "outstandingEvents", outstandingEvents + "");
		    		IDataUtil.put(sharedDurableOutstandingEventsC, "namedObject", no.getName());
		    		sharedDurableOutstandingEventsC.destroy();
		    		if( max < outstandingEvents ) {
		    			max = outstandingEvents;
		    		}
		    	}
		    	IDataUtil.put(pipelineCursor, "outstandingEventsSharedDurableMax", max + "");
		    	IDataUtil.put(pipelineCursor, "sharedDurableOutstandingEvents",sharedDurableOutstandingEvents);
		    } 
		    
			// non shard durable,
		    // as well as IS Native Messaging Trigger with Serial Processing on Shared Durable Connection 
		    /*
		     * If a wM messaging trigger has a shared durable connection, but does serial processing,
		     * then the event is stored directly in the channel, therefore we can just get the event count
		     * for the topic 
		     */
			IDataUtil.put(pipelineCursor, "outstandingEvents", channel.getEventCount() + "");
		    
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	@SuppressWarnings("unchecked")
	public static final void getQueuedElementsForMessagingTrigger (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsForMessagingTrigger)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required triggerName
		// [o] field:0:required outstandingEvents
		IDataCursor pipelineCursor = pipeline.getCursor();
		String triggerName = IDataUtil.getString(pipelineCursor, "triggerName");
		String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
		
		if( triggerName == null || "".equals(triggerName) ) {
			throw new ServiceException("triggerName cannot be empty");
		}		
		if( RNAME == null || "".equals(RNAME) )  {
			throw new ServiceException("RNAME must not be empty. Provide like: 'nsp://host:port'.");
		}
		/*
		 * first invoke internal service wm.server.triggers:getTriggerReport
		 * - this report returns the "principal", which is reflects the UM Named Object
		 * - and this report returns the IS document type, which reflects the the folder/tree structure in UM which is auto-generated by IS when a new publishable document is created
		 */
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wm.server.triggers", "getTriggerReport", pipeline );
		} catch( Exception e){
			throw new ServiceException("Exception when invoking wm.server.triggers:getTriggerReport: " + e);
		}
		IData triggerReport = IDataUtil.getIDataArray(output.getCursor(), "triggers")[0];
		IData properties = IDataUtil.getIData(triggerReport.getCursor(), "properties");
		// the principal (client id) reflects the UM Named Object. We know that this is the second element in the array of principals by looking at the DSP trigger-managemente-details.dsp
		String principal = IDataUtil.getStringArray(properties.getCursor(), "principals")[1];
		
		// we asume that we have only one condition
		IData condition = IDataUtil.getIDataArray(triggerReport.getCursor(), "conditions")[0];
		// we assume that we have only one filter
		IData documentTypeFilterPair = IDataUtil.getIDataArray(condition.getCursor(), "documentTypeFilterPairs")[0];
		String documentType = IDataUtil.getString(documentTypeFilterPair.getCursor(),"documentType" );
		/*
		 * The complete documentType name is something like wx.platformMonitoring_Test.pub.trigger.messaging:publishableDoc
		 * IS creates a tree structure with "wm/is/" as the root in UM
		 * We cannot use the documentType name (the part after the ":"), because it is possible that there have been duplicates in UM, therefore we only use the folder to create the tree structure. We will iterate over all children.
		 */
		// documentLeafNodeName for the IS DocType "wx.platformMonitoring_Test.pub.trigger.messaging:publishableDoc" == "wm/is/wx/platformMonitoring_Test/pub/trigger/messaging"
		String documentLeafNodeName = "wm/is/" + documentType.split(":")[0].replaceAll("\\.", "/");
		
		try {
			nSessionAttributes nsa = new nSessionAttributes(RNAME);		
			nSession mySession = nSessionFactory.create(nsa);
			mySession.init();
			
		    nRealmNode realmNode = new nRealmNode(nsa);
		    nNode documentLeafNode = realmNode.findNode(documentLeafNodeName);
		    Enumeration<nNode> nodes = ((nContainer)documentLeafNode).getNodes();
		    nChannel channel = null;
		    long outstandingEvents = -1;
		    for( ; nodes.hasMoreElements(); ) {
		    	nNode node = nodes.nextElement();
		    	 if( node instanceof nLeafNode ) {
		    		 // we are only interested in channels
		    	      if( ((nLeafNode) node).isChannel() ) {
		    	    	  nLeafNode leaf = (nLeafNode) node;
		    	    	  channel = mySession.findChannel(leaf.getAttributes());
							for (nNamedObject nno : channel.getNamedObjects()) {
								String nnoName = nno.getName();
								/*
								 * the name of the named object differs slightly from the clientId
								 * E.g., the NamedObject's name is "J_jRQtEolDEurgAVIwwwADXaZQs_##wx__platformMonitoring_Test__pub__trigger__messaging__messagingTrigger"
								 * The ClientId (principal) from the trigger report is "J_jRQtEolDEurgAVIwwwADXaZQs__wx_platformMonitoring_Test_pub_trigger_messaging_messagingTrigger"
								 * Therefore we have to replace the double underscores with single underscores, and the ## is to be replace with a single underscore
								 */
								String clientId = nnoName.replaceAll("__", "_").replaceFirst("##", "_");
								if( clientId.equals(principal) ) {
									outstandingEvents = nno.getSharedNamedObjectOutstandingEvents();
									break;
								}
							}
		    	      }
		    	 }
		    }
		    if( channel != null && outstandingEvents == -1 ) {
		    	outstandingEvents = channel.getEventCount();
		    }
		    IDataUtil.put(pipelineCursor, "outstandingEvents",
		    		outstandingEvents + "");
		} catch (Exception e) {
			throw new ServiceException("Could not get the nr of queued events for trigger " + triggerName + " on realm " + RNAME + ": " + e);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getQueuedElementsJms (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsJms)>> ---
		// @sigtype java 3.5
		// [i] field:0:required RNAME
		// [i] field:0:required jmsQueueName
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
		    com.pcbsys.nirvana.client.nFindResult[] findResult = mySession.find(new nChannelAttributes[] {attrib});
		    if( findResult.length == 0 ) {
		    	throw new ServiceException("JMS queue " + jmsQueueName + " was not found on realm " + RNAME);
		    }
		    if( findResult[0].isChannel() ) {
		    	// if it is a channel, then it is a jms topic
		    	IDataUtil.put( pipelineCursor, "topicName", jmsQueueName );
		    	getQueuedElementsJmsTopic(pipeline);
		    } else if( findResult[0].isQueue() ) {
		    	// this is a jms queue
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
		    com.pcbsys.nirvana.client.nQueueDetails details = queue.getDetails();
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
		// [o] field:0:required queueStorageSize
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
		
		    nRealmNode realmNode = new nRealmNode(nsa);
		    nNode node = realmNode.findNode(topicName);
		    if (node instanceof nLeafNode) {
				if (((nLeafNode) node).isChannel() || ((nLeafNode) node).isQueue()) {
					nLeafNode leafNode = (nLeafNode)node;
					IDataUtil.put(pipelineCursor, "queueStorageSize", leafNode.getUsedSpace() + "");
		//					IDataUtil.put(pipelineCursor, "getCurrentNumberOfEvents", leafNode.getCurrentNumberOfEvents() + "");
				}
			}
		    IDataUtil.put(pipelineCursor, "outstandingEvents", channel.getEventCount() + "");
		    pipelineCursor.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getRNAMEByJmsConnectionAlias (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getRNAMEByJmsConnectionAlias)>> ---
		// @sigtype java 3.5
		// [i] field:0:required jmsConnectionAlias
		// [o] field:0:required RNAME
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		String jmsConnectionAlias = IDataUtil.getString(pipelineCursor, "jmsConnectionAlias");
		
		if( jmsConnectionAlias == null || "".equals(jmsConnectionAlias)) {
			throw new ServiceException("jmsConnectionAlias must not be empty");
		}
		
		// input
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put( inputCursor, "aliasName", jmsConnectionAlias );
		inputCursor.destroy();
		
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wm.server.jms", "getConnectionAliasReport", input );
		}catch( Exception e){
			throw new ServiceException("Could not get connection alias " + jmsConnectionAlias + " with service wm.server.jms:getConnectionAliasReport: " + e) ;
		}
		
		String jndi_jndiAliasName = IDataUtil.getString(output.getCursor(), "jndi_jndiAliasName");
		String jndi_connectionFactoryLookupName = IDataUtil.getString(output.getCursor(), "jndi_connectionFactoryLookupName");
		
		// input
		input = IDataFactory.create();
		inputCursor = input.getCursor();
		IDataUtil.put( inputCursor, "jndiAliasName", jndi_jndiAliasName );
		inputCursor.destroy();
		
		// output
		output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wm.server.jndi", "getJNDIAliasData", input );
		}catch( Exception e){
			throw new ServiceException("Could not get jndi alias " + jndi_jndiAliasName + " with service wm.server.jndi:getJNDIAliasData: " + e);
		}
		String providerURL = IDataUtil.getString(output.getCursor(), "providerURL");
		String initialContextFactory = IDataUtil.getString(output.getCursor(), "initialContextFactory");
		
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			env.put(Context.PROVIDER_URL, providerURL);
			InitialContext initialContext = new InitialContext(env);
			com.pcbsys.nirvana.nJMS.ConnectionFactoryImpl connectionFactory = (com.pcbsys.nirvana.nJMS.ConnectionFactoryImpl) initialContext
					.lookup(jndi_connectionFactoryLookupName);
			String cfRname = connectionFactory.getRNAME();
			IDataUtil.put(pipeline.getCursor(), "RNAME", cfRname);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			throw new ServiceException("NamingException when looking up RNAME for JMS Connection Alias " + jmsConnectionAlias + ": " + e1);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getRNAMEByMessagingConnectionAlias (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getRNAMEByMessagingConnectionAlias)>> ---
		// @sigtype java 3.5
		// [i] field:0:required useDefaultMessagingConnectionAlias {"true","false"}
		// [i] field:0:required messagingConnectionAlias
		// [o] field:0:required RNAME
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	useDefaultMessagingConnectionAlias = IDataUtil.getString( pipelineCursor, "useDefaultMessagingConnectionAlias" );
		String	messagingConnectionAlias = IDataUtil.getString( pipelineCursor, "messagingConnectionAlias" );
		
		if( useDefaultMessagingConnectionAlias == null || "".equals(useDefaultMessagingConnectionAlias) ) {
			useDefaultMessagingConnectionAlias = "false";
		}
		if(( messagingConnectionAlias == null || "".equals(messagingConnectionAlias) ) && useDefaultMessagingConnectionAlias.equals("false")) {
			throw new ServiceException("Either provide a messagingConnectionAlias, or set useDefaultMessagingConnectionAlias=true");
		}
		
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wm.server.messaging", "getConnectionAliasReport", IDataFactory.create() );
		}catch( Exception e){
			throw new ServiceException("Could not invoke wm.server.messaging:getConnectionAliasReport: " + e);
		}
		
		IData[] aliasDataList = IDataUtil.getIDataArray(output.getCursor(), "aliasDataList");
		String um_rname = null;
		for( IData aliasData : aliasDataList ) {
		//			aliasDataList/aliasDataList[0]/defaultAlias
			IDataCursor aliasDataC = aliasData.getCursor();
			boolean isDefaultAlias = IDataUtil.getBoolean(aliasDataC, "defaultAlias");
			String aliasName = IDataUtil.getString(aliasDataC, "aliasName");
			if( useDefaultMessagingConnectionAlias.equals("true") && isDefaultAlias ) {
				um_rname = IDataUtil.getString(aliasDataC, "um_rname");
				break;
			} else if(aliasName.equals(messagingConnectionAlias) ) {
				um_rname = IDataUtil.getString(aliasDataC, "um_rname");
				break;
			}
		}
		if ( um_rname == null ) {
			throw new ServiceException("Did not find an messaging connection alias. Parameters: useDefaultMessagingConnectionAlias='"  + useDefaultMessagingConnectionAlias + "', messagingConnectionAlias='" + messagingConnectionAlias + "'.");
		}
		
		IDataUtil.put( pipelineCursor, "RNAME", um_rname );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

