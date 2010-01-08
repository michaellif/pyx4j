/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Single service call definition.
 * 
 * Use RPCManager.execute(...) in client code;
 */
@RemoteServiceRelativePath("service")
public interface RemoteService extends com.google.gwt.user.client.rpc.RemoteService {

    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest) throws RuntimeException;

}
