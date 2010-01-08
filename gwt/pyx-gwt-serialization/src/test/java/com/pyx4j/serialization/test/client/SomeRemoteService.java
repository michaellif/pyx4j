/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import java.io.Serializable;

public interface SomeRemoteService extends com.google.gwt.user.client.rpc.RemoteService {

    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest) throws RuntimeException;
}
