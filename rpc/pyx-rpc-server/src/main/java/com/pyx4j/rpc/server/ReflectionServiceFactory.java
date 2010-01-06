/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.rpc.shared.Service;

/**
 * Default factory. Finds Service implementation in '.server.' package and adds 'Impl' to
 * interface name.
 */
public class ReflectionServiceFactory implements IServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(ReflectionServiceFactory.class);

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Service<?, ?>> getServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException {
        String serviceImplClassName;
        if (serviceInterfaceClassName.contains(".shared.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".shared.", ".server.") + "Impl";
        } else if (serviceInterfaceClassName.contains(".client.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".client.", ".server.") + "Impl";
        } else if (serviceInterfaceClassName.contains(".rpc.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".rpc.", ".server.") + "Impl";
        } else {
            serviceImplClassName = serviceInterfaceClassName + "Impl";
        }
        serviceImplClassName = serviceImplClassName.replace("$", "Impl$");

        Class<? extends Service<?, ?>> serviceClass = null;
        try {
            serviceClass = (Class<? extends Service<?, ?>>) Class.forName(serviceImplClassName);
        } catch (Throwable e) {
            log.error("RPC Service Class load error", e);
            throw new ClassNotFoundException("RPC Service " + serviceImplClassName + " not avalable");
        }
        return serviceClass;
    }

}
