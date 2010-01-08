/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.rpc.shared.Service;

public class ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

    @SuppressWarnings("unchecked")
    private static Map<String, Class<? extends Service>> services = new HashMap<String, Class<? extends Service>>();

    private ServiceRegistry() {

    }

    @SuppressWarnings("unchecked")
    public static void register(String name, Class<? extends Service> srv) {
        Class<? extends Service> srvOrig = services.get(name);
        if (srvOrig != null) {
            log.warn("redefine service {} of class {}", name, srvOrig.getName());
        }
        // validate 
        try {
            Class interfaceClass = Class.forName(name);
            if (!Service.class.isAssignableFrom(interfaceClass)) {
                throw new RuntimeException("Interface " + name + " is not a service");
            }
            if (!interfaceClass.isAssignableFrom(srv)) {
                throw new RuntimeException("Service " + srv.getName() + " is not an implementation for service " + name);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No such interface " + name);
        }

        services.put(name, srv);
        log.debug("registered service {}", name);
    }

    @SuppressWarnings("unchecked")
    public static void register(Class<? extends Service> srv) {
        for (Class interfaceClass : srv.getInterfaces()) {
            if (!Service.class.isAssignableFrom(interfaceClass)) {
                continue;
            }
            String name = interfaceClass.getName();
            Class<? extends Service> srvOrig = services.get(name);
            if (srvOrig != null) {
                log.warn("redefine service {} of class {}", name, srvOrig.getName());
            }
            services.put(name, srv);
            log.debug("registered service {}", name);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerGroup(Class<? extends IsServiceGroup> srvs) {
        Class<?>[] classes = srvs.getDeclaredClasses();
        int cnt = 0;
        for (Class<?> c : classes) {
            if (Service.class.isAssignableFrom(c)) {
                register((Class<Service>) c);
                cnt++;
            }
        }
        if (cnt == 0) {
            log.warn("service group {} is empty", srvs.getName());
        }
    }

    @SuppressWarnings("unchecked")
    static Class<? extends Service> getServiceClass(String serviceDescriptor) {
        return services.get(serviceDescriptor);
    }

    public static int size() {
        return services.size();
    }

    public List<String> getServices() {
        return getServiceNames();
    }

    public static List<String> getServiceNames() {
        Vector<String> clone = new Vector<String>();
        clone.addAll(services.keySet());
        return clone;
    }

    @SuppressWarnings("unchecked")
    static void initAllService() {
        for (Class<? extends Service> clazz : services.values()) {
            try {
                clazz.newInstance();
            } catch (Throwable e) {
                log.error("Fail to create service class " + clazz.getName(), e);
            }
        }
    }
}
