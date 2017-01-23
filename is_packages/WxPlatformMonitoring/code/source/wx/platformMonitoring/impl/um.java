package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-23 16:34:59 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.Enumeration;
import com.pcbsys.nirvana.client.nIllegalArgumentException;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.nAdminAPI.nBaseAdminException;
import com.pcbsys.nirvana.nAdminAPI.nLeafNode;
import com.pcbsys.nirvana.nAdminAPI.nNode;
import com.pcbsys.nirvana.nAdminAPI.nRealmNode;
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class um

{
	// ---( internal utility methods )---

	final static um _instance = new um();

	static um _newInstance() { return new um(); }

	static um _cast(Object o) { return (um)o; }

	// ---( server methods )---




	public static final void getOutstandingJmsQueueEvents (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getOutstandingJmsQueueEvents)>> ---
		// @sigtype java 3.5
		String queueName = "jmsQueue";
		
		String[] RNAME={"nsp://localhost:9000"};
		nSessionAttributes nsa;
		try {
			nsa = new nSessionAttributes(RNAME);
			nRealmNode realm = new nRealmNode(nsa);
			Enumeration children = realm.getNodes();
			while (children.hasMoreElements()) {
				nNode child = (nNode)children.nextElement();
				if (child instanceof nLeafNode) {
					nLeafNode leaf = (nLeafNode)child;
					if( leaf.getName().equals(queueName))  {				
						System.out.println("Leaf node contains "+leaf.getCurrentNumberOfEvents());
						return;
					}
				}
			}
			throw new ServiceException("did not find queue with name: "  + queueName);
		} catch (nIllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (nBaseAdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// --- <<IS-END>> ---

                
	}
}

