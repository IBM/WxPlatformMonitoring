package com.softwareag.wx.platformMonitoring.jmx;

import java.util.ArrayList;
import java.util.List;

import com.wm.app.b2b.server.ns.Interface;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSService;

public class IDataUtil {

	public static List<String> getServicesForFolder(String folderName) {
		List<String> services = new ArrayList<String>();
		NSNode node = com.wm.app.b2b.server.ns.Namespace.current().getNode(folderName);
		if( node instanceof com.wm.app.b2b.server.ns.Interface ) {
			Interface ifc = (com.wm.app.b2b.server.ns.Interface)node;
			NSNode[] childNodes = ifc.getNodes();
			for( NSNode child : childNodes ) {
				if( child instanceof Interface) {
					
				} else if( child instanceof NSService ) {
					services.add(child.getNSName().getFullName());
				}
			}
		}
		
		return services;
	}
	
}
