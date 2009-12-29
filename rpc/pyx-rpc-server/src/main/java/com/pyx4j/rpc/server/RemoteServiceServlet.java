/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.rpc.shared.RemoteService;

@SuppressWarnings("serial")
public class RemoteServiceServlet extends com.google.gwt.user.server.rpc.RemoteServiceServlet implements RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceServlet.class);

    private RemoteService implementation;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IServiceFactory serviceFactory = null;
        if (config != null) {
            String serviceFactoryClassName = config.getInitParameter("serviceFactoryClass");
            if (CommonsStringUtils.isStringSet(serviceFactoryClassName)) {
                try {
                    serviceFactory = (IServiceFactory) Class.forName(serviceFactoryClassName).newInstance();
                } catch (Throwable e) {
                    log.error("GWT RPC ServiceFactory creation error", e);
                    throw new ServletException("RPC ServiceFactory not avalable");
                }
            }
        }
        if (serviceFactory == null) {
            serviceFactory = new ReflectionServiceFactory();
        }
        implementation = new RemoteServiceImpl("GWT", serviceFactory);
    }

    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest) throws RuntimeException {
        return implementation.execute(serviceInterfaceClassName, serviceRequest);
    }

}
