package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.InvokeState;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.lang.ns.NSName;
import java.util.Collections;
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



	public static final void getThreadCpuUsage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getThreadCpuUsage)>> ---
		// @sigtype java 3.5
		IDataCursor pipelineCursor = pipeline.getCursor();
		int numberTopThreads = IDataUtil.getInt(pipelineCursor, "numberOfTopThreads", numberTopThreadsDefault);
		int measurementIntervalSeconds = IDataUtil.getInt(pipelineCursor, "measurementIntervalSeconds", measurementIntervalSecondsDefault);
		
		Map<Long, Long> threadInitialCPU = new HashMap<Long, Long>();
		// check if this is the first run, i.e. we do not have any baseline. Create the baseline and wait 1 second
		ThreadInfo[] initialThreadInfos = threadMxBean.dumpAllThreads(false, false);
		for (ThreadInfo info : initialThreadInfos) {
		    threadInitialCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
		}
		// get the current uptime
		long initialUptime = runtimeMxBean.getUptime();
		try {
			Thread.sleep(measurementIntervalSeconds*1000);
		} catch (InterruptedException e) {
			// ignore
		}
		// get current uptime after wait
		long upTime = runtimeMxBean.getUptime();
		// elapsedTime is in ms.
		long elapsedTime = (upTime - initialUptime);
		// now get the current threads cpu reading
		Map<Long, Long> threadCurrentCPU = new HashMap<Long, Long>();
		ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
		for (ThreadInfo info : threadInfos) {
		    threadCurrentCPU.put(info.getThreadId(), threadMxBean.getThreadCpuTime(info.getThreadId()));
		}		
		// CPU over all processes
		int nrCPUs = osMxBean.getAvailableProcessors();
		// total CPU: CPU % can be more than 100% (devided over multiple cpus)
		// caclulate the cpu usage in percent and store in array list which can be sorted
		List<Entry<ThreadInfo, Float>> results = new ArrayList<Entry<ThreadInfo, Float>>();
		for (ThreadInfo info : threadInfos) {
		    Long initialCPU = threadInitialCPU.get(info.getThreadId());
		    if (initialCPU != null) {
		        long elapsedCpu = threadCurrentCPU.get(info.getThreadId()) - initialCPU;
		        float cpuUsage = elapsedCpu / (elapsedTime * 1000000F * nrCPUs);
		        results.add(new java.util.AbstractMap.SimpleEntry<ThreadInfo, Float>(info, cpuUsage));
		    }
		}
		// sort our list so that we get the top threads
		Collections.sort(results, new EntryComparator());
		// add top threads to output
		if( results.size() > numberTopThreads ) {
			List<Entry<ThreadInfo, Float>> rsList = results.subList(0, numberTopThreads);
			// threads
			IData[]	threads = new IData[numberTopThreads];
			int i=0;
			for (Entry<ThreadInfo, Float> entry : rsList) {
				threads[i] = IDataFactory.create();
				IDataCursor threadsCursor = threads[i].getCursor();
				IDataUtil.put( threadsCursor, "threadId", entry.getKey().getThreadId() + "" );
				IDataUtil.put( threadsCursor, "threadName", entry.getKey().getThreadName() );
				IDataUtil.put( threadsCursor, "cpuUsagePercent", entry.getValue()*100 + "");
				IDataUtil.put( threadsCursor, "threadDump", getDump(entry.getKey()));
				threadsCursor.destroy();
				i++;
			}
			IDataUtil.put( pipelineCursor, "threads", threads );
		}
		// store thread cpu reading for next iteration
		threadInitialCPU = threadCurrentCPU;
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
	private static RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
	private static OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
	private static int numberTopThreadsDefault = 5;
	private static int measurementIntervalSecondsDefault = 5;
	
	/**
	 * Formats a ThreadInfo object to a thread dump like string
	 */
	private static String getDump(ThreadInfo thread) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\"%s\" tid=%d %s\njava.lang.Thread.State: %s\n",
				thread.getThreadName(), thread.getThreadId(),
				Thread.State.WAITING.equals(thread.getThreadState()) ? "in Object.wait()"
						: thread.getThreadState().name().toLowerCase(),
				(thread.getThreadState().equals(Thread.State.WAITING) ? "WAITING (on object monitor)" : thread.getThreadState())));
	
		final StackTraceElement[] stackTraceElements = thread.getStackTrace();
		for (final StackTraceElement stackTraceElement : stackTraceElements) {
			builder.append("\tat ");
			builder.append(stackTraceElement);
		}
		builder.append("\n");
		return builder.toString();
	}
	
	private static class EntryComparator implements Comparator<Entry<ThreadInfo, Float>> {
		
		/**
		 * Implements descending order.
		 */
		@Override
		public int compare(Entry<ThreadInfo, Float> o1, Entry<ThreadInfo, Float> o2) {
			if (o1.getValue() < o2.getValue()) {
				return 1;
			} else if (o1.getValue() > o2.getValue()) {
				return -1;
			}
			return 0;
		}
	
	}
	// --- <<IS-END-SHARED>> ---
}

