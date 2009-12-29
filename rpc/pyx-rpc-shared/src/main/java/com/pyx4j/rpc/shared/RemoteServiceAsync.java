/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Do not use directly in code.
 * 
 * Use RPCManager.execute(...) in client code;
 */
public interface RemoteServiceAsync {

    public void execute(String serviceInterfaceClassName, Serializable serviceRequest, AsyncCallback<? extends Serializable> callback);

}
