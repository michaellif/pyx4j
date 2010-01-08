/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 11, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.RemoteServiceAsync;
import com.pyx4j.rpc.shared.Service;

public class RPCManager {

    private static final RemoteServiceAsync service;

    static {
        service = (RemoteServiceAsync) GWT.create(RemoteService.class);
    }

    public static void setServiceEntryPointURL(String url) {
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint(url);
    }

    public static <I extends Serializable, O extends Serializable> void execute(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        service.execute(serviceInterface.getName(), request, callback);
    }

}
