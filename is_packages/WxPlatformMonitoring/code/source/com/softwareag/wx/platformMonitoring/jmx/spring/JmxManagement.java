package com.softwareag.wx.platformMonitoring.jmx.spring;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.jmx.support.RegistrationPolicy;

public class JmxManagement {

	public void init() {
		org.springframework.jmx.export.MBeanExporter e = new org.springframework.jmx.export.MBeanExporter();
		System.out.println("------------ setting policy to ignore");
		e.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
		
		org.springframework.jmx.support.MBeanServerFactoryBean mServer = new MBeanServerFactoryBean();
		mServer.setLocateExistingServerIfPossible(true);
		mServer.afterPropertiesSet();
		
		e.setAllowEagerInit(true);
		e.setServer(mServer.getObject());

		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		System.out.println("------------ registering bean3");
		e.setBeanFactory(context.getParentBeanFactory());

		System.out.println("------------ registering bean count " + e.getServer().getMBeanCount() + ", default: "
				+ e.getServer().getDefaultDomain());
		try {
			ObjectName o = new ObjectName("bean:name=testDynBean1");
			MBeanServer s = e.getServer();
			if( s.queryMBeans(o, null).size() != 0) {
				s.unregisterMBean(o);
			}
			s.registerMBean(new SimpleDynamic(), o);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException | InstanceNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("NOT COMPLIANT: " + e1);
		}
	}
	

	
}
