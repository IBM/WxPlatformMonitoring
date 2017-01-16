package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-16 10:01:48 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.List;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.InvokeState;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;
// --- <<IS-END-IMPORTS>> ---

public final class server

{
	// ---( internal utility methods )---

	final static server _instance = new server();

	static server _newInstance() { return new server(); }

	static server _cast(Object o) { return (server)o; }

	// ---( server methods )---




	public static final void getCurrentlyRunningServices (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCurrentlyRunningServices)>> ---
		// @sigtype java 3.5
		// [o] field:0:required currentlyRunningServicesCount
		// [o] field:1:required currentlyRunningServicesList
		IDataCursor pc = pipeline.getCursor();
		
		String currentPackageName = InvokeState.getCurrentService().getPackage().getName();
		
		// input
		IData input = IDataFactory.create();
		
		// output
		IData 	output = IDataFactory.create();
		try{
			output = Service.doInvoke( "wm.server.query", "getServiceStats", input );
		}catch( Exception e){
			throw new ServiceException("Cannot get list of currently running services from service wm.server.query:getServiceStats: "  
					 + e);
		}
		
		IDataCursor outputC = output.getCursor();
		IData[] SvcStats = IDataUtil.getIDataArray(outputC, "SvcStats");
		List<String> runningServiceList = new ArrayList<String>();
		int counter = 0;
		for( IData singleService : SvcStats ) {
			IDataCursor sc = singleService.getCursor();
			try {
				String running = IDataUtil.getString(sc, "sRunning");
				if( !running.equals("&nbsp;") ) {
					int runningThreads = Integer.valueOf(IDataUtil.getString(sc, "sRunning"));
					if( runningThreads > 0 ) {
						// get the name of the serivce
						String serviceName = IDataUtil.getString(sc, "name");
						// get the folder of this service (ifc)
						com.wm.util.Name ifc = (com.wm.util.Name)IDataUtil.get(sc, "ifc");
						// get the name of the service (svc)
						com.wm.util.Name svc = (com.wm.util.Name)IDataUtil.get(sc, "svc");
						// create a NSName object by folder and name
						NSName name = NSName.create(ifc.toString(), svc.toString());
						// create a base service from the nsname
						BaseService bs = com.wm.app.b2b.server.ns.Namespace.getService(name);
						// check if the package of the base service is the same as for this currently running service
						if( !bs.getPackage().getName().equals(currentPackageName) ) {
							// it is not the same, so we want to count this service run stats
							runningServiceList.add(serviceName);
							counter++;
						}
					}
				}				
			} catch( NumberFormatException nfe ) {
				// ignore
			}
			sc.destroy();
		}
		outputC.destroy();
		
		IDataUtil.put( pc, "currentlyRunningServicesCount", counter + "" );
		IDataUtil.put(pc, "currentlyRunningServicesList", runningServiceList.toArray(new String[runningServiceList.size()]));
		
		pc.destroy();
		
			
		// --- <<IS-END>> ---

                
	}
}

