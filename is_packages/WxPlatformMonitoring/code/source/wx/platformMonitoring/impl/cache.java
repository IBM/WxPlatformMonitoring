package wx.platformMonitoring.impl;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 12:30:08 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.expression.Criteria;
import com.wm.app.b2b.server.cache.CacheManagerUtil;
import com.softwareag.cache.admin.TestCacheEntry;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.FifoPolicy;
// --- <<IS-END-IMPORTS>> ---

public final class cache

{
	// ---( internal utility methods )---

	final static cache _instance = new cache();

	static cache _newInstance() { return new cache(); }

	static cache _cast(Object o) { return (cache)o; }

	// ---( server methods )---




	public static final void enableStatistics (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(enableStatistics)>> ---
		// @sigtype java 3.5
		// [i] field:0:required cacheManagerName
		// [i] field:0:required cacheName
		IDataCursor idc = pipeline.getCursor();
		String cacheManagerName = IDataUtil.getString(idc, "cacheManagerName");
		String cacheName = IDataUtil.getString(idc, "cacheName");
		idc.destroy();
		
		Cache cache = CacheManagerUtil.getCacheManager(cacheManagerName).getCache(cacheName);
		cache.getCacheConfiguration().setStatistics(true);
		// TODO: class Statistics does not exist any more. cache.setStatisticsEnabled neither.
		//		cache.setStatisticsEnabled(true);
		cache.getCacheConfiguration().setMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.FIFO.toString());
		cache.setMemoryStoreEvictionPolicy(new FifoPolicy());
		cache.getMemoryStoreEvictionPolicy().getName();
		//cache.setStatisticsAccuracy(Statistics.STATISTICS_ACCURACY_GUARANTEED);
		// --- <<IS-END>> ---

                
	}



	public static final void getStatistics (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getStatistics)>> ---
		// @sigtype java 3.5
		// [i] field:0:required cacheManagerName
		// [i] field:0:required cacheName
		IDataCursor idc = pipeline.getCursor();
		String cacheManagerName = IDataUtil.getString(idc, "cacheManagerName");
		String cacheName = IDataUtil.getString(idc, "cacheName");
		
		Cache cache = CacheManagerUtil.getCacheManager(cacheManagerName).getCache(cacheName);
		String stats = cache.getStatistics().toString();
		
		String config = "getMaxEntriesLocalHeap: " + cache.getCacheConfiguration().getMaxEntriesLocalHeap() + 
				",  getMaxElementsInMemory: " + cache.getCacheConfiguration().getMaxElementsInMemory() + ", evictionPolicy: " + cache.getMemoryStoreEvictionPolicy().getName();
		
		String nrEntries = cache.getKeys().size() + "";
		
		IDataUtil.put(idc, "stats", stats);
		IDataUtil.put(idc, "config", config);
		IDataUtil.put(idc, "nrEntries", nrEntries);
		idc.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getTestValue (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getTestValue)>> ---
		// @sigtype java 3.5
		// [o] field:0:required testValue
		Cache cache = getTestCache();
		
		TestCacheEntry tce = null;
		
		if (cache.isKeyInCache(CACHE_KEY)){
			tce = (TestCacheEntry) cache.get(CACHE_KEY).getObjectValue();
		} else {
			throw new ServiceException("There is not test value stored in the TestCache for key " + CACHE_KEY);
		}
		
		IDataUtil.put(pipeline.getCursor(), "testValue", tce.getTestValue());
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void putTestValue (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(putTestValue)>> ---
		// @sigtype java 3.5
		// [i] field:0:required testValue
		Cache cache = getTestCache(); 
		
		IDataCursor pipelineC = pipeline.getCursor();
		String testValue = IDataUtil.getString(pipelineC, "testValue");
		if( testValue == null || testValue.equals("") ) {
			throw new ServiceException("testValue must not be null nor empty");
		}
		pipelineC.destroy();
		
		TestCacheEntry tce = new TestCacheEntry(testValue);
					 
		putInCache(cache, tce);
		// --- <<IS-END>> ---

                
	}



	public static final void removeTestValue (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(removeTestValue)>> ---
		// @sigtype java 3.5
		Cache cache = getTestCache();
		
		cache.remove(CACHE_KEY);
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static final String CACHE_KEY = "testValue";
	
	public static Cache getTestCache() {
		String cacheManagerName = "OrderCacheManager";
		String cacheName = "TestCache";
		
		return CacheManagerUtil.getCacheManager(cacheManagerName).getCache(cacheName);
	}
	
	static void putInCache(Cache cache, TestCacheEntry tce) {
		Element e = new Element(CACHE_KEY, tce);
		cache.put(e);
	}
		
	// --- <<IS-END-SHARED>> ---
}

