package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-23 16:46:34 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.Enumeration;
import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nNamedObject;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.nAdminAPI.nContainer;
import com.pcbsys.nirvana.nAdminAPI.nLeafNode;
import com.pcbsys.nirvana.nAdminAPI.nNode;
import com.softwareag.util.IDataMap;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.pcbsys.nirvana.server.store.nNamedSubscriber;
import com.pcbsys.nirvana.client.nSessionFactory;
// --- <<IS-END-IMPORTS>> ---

public final class nAdmin

{
	// ---( internal utility methods )---

	final static nAdmin _instance = new nAdmin();

	static nAdmin _newInstance() { return new nAdmin(); }

	static nAdmin _cast(Object o) { return (nAdmin)o; }

	// ---( server methods )---




	public static final void getQueuedElementsJmsQueue (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getQueuedElementsJmsQueue)>> ---
		// @sigtype java 3.5
		try {
			
			// pipeline
			IDataCursor pipelineCursor = pipeline.getCursor();
			String	RNAME = IDataUtil.getString( pipelineCursor, "RNAME" );
			pipelineCursor.destroy();
		
			// pipeline
		
		
			nSessionAttributes nsa=new nSessionAttributes(RNAME);
		
		//			nRealmNode realm = new nRealmNode(nsa);			
		//			
		//			realm.waitForEntireNameSpace();
			
			ArrayList<IData> nodeList = new ArrayList<IData>();
			nSession      mySession = nSessionFactory.create(nsa);
					mySession.init();
		//					traverseNodes(realm, nodeList, mySession);
							for(nChannelAttributes ncaint :mySession.getChannels()){
						    	IDataMap idm = new IDataMap(IDataFactory.create());
						    	idm.put("Name", ncaint.getName());
						    	if(ncaint.getChannelMode()==nChannelAttributes.CHANNEL_MODE){
						    		nChannel nc = mySession.findChannel(ncaint);
						    		idm.put("Type", "Topic");
						    		idm.put("Size", "0");
						    		
									nodeList.add(idm.getIData());
		
									
								    
								    nNamedObject[] durableSubscribers = nc.getNamedObjects();
									System.err.println("UM:    "+nc.getName()+" - "+durableSubscribers.length);	    
							    for(nNamedObject nNamedObject : durableSubscribers){
							    	idm = new IDataMap(IDataFactory.create());
									idm.put("Name", nc.getName()+"/"+nNamedObject.getName());
									idm.put("Type", "Durable");
									idm.put("Size", findHiddenQueue(nc.getName(),nNamedObject.getName(),mySession));
		//									idm.put("Stored", findHiddenQueue(nc.getName(),nNamedObject.getName(),mySession));
		//									idm.put("Current", nNamedObject.getEID());
									nodeList.add(idm.getIData());
							    }
				
						    	}else{
						    		nQueue nq = mySession.findQueue(ncaint);
						    		idm.put("Type", "Queue");
						    		idm.put("Size", nq.size());
									nodeList.add(idm.getIData());
		
						    		}
				//				    	nChannel nc = mySession.findChannel(ncaint);
				//				    	if(nc!=null && ncaint.getChannelMode()==nChannelAttributes.QUEUE_MODE)
				//				    		idm.put("Size", nc.getQueueSize());
								
				//						idm.put("Stored", chan.getQueueSize());
		//										idm.put("Current", chan.getLastEID());
						    }
					
					
		//				idm.put("con", "Leaf Node "+leaf.getName()+" is a channel"+leaf.getCurrentNumberOfEvents());
					
					
			mySession.close();
			
			IDataUtil.put(pipeline.getCursor(), "RealmOverview", nodeList.toArray(new IData[nodeList.size()]));
			
			
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

