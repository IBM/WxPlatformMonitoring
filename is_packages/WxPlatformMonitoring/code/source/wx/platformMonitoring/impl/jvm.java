package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 12:41:48 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.List;
import com.wm.data.IData;
import com.wm.data.IDataUtil;
// --- <<IS-END-IMPORTS>> ---

public final class jvm

{
	// ---( internal utility methods )---

	final static jvm _instance = new jvm();

	static jvm _newInstance() { return new jvm(); }

	static jvm _cast(Object o) { return (jvm)o; }

	// ---( server methods )---




	public static final void getPermGen (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPermGen)>> ---
		// @sigtype java 3.5
		// [o] field:0:required percentageUsed
		// [o] field:0:required resultTxt
		MemoryPoolMXBean permgenBean = null;
		    List<MemoryPoolMXBean> beans = 
		            ManagementFactory.getMemoryPoolMXBeans();
		    for(MemoryPoolMXBean bean : beans) {
		        if(bean.getName().toLowerCase().indexOf("perm gen") >= 0) {
		            permgenBean = bean;
		            break;
		        }
		    }
		
		    MemoryUsage currentUsage = permgenBean.getUsage();
		    int percentageUsed = (int)((currentUsage.getUsed() * 100) 
		            / currentUsage.getMax());
		    IDataUtil.put(pipeline.getCursor(), "resultTxt", new Date() + ": Permgen " + 
		            currentUsage.getUsed() +
		            " of " + currentUsage.getMax() +
		            " (" + percentageUsed + "%)");
		    IDataUtil.put(pipeline.getCursor(), "maxPermGen", currentUsage.getMax() + "");
		    IDataUtil.put(pipeline.getCursor(), "usedPermGen", currentUsage.getUsed() + "");
		    IDataUtil.put(pipeline.getCursor(), "percentageUsed", percentageUsed + "");
			
		// --- <<IS-END>> ---

                
	}
}

