/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SomeRemoteServiceAsync {

    public void execute(String serviceInterfaceClassName, Serializable serviceRequest, AsyncCallback<Serializable> callback);

}
