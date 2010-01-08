/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.client;

import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * 
 * You need to declare new interface to use in application with your service.
 * RemoteServiceTarget annotation is required.
 * 
 * Example:
 * 
 * <pre>
 * &#064;RemoteServiceTarget(MyRemoteService.class)
 * public interface MyRPCSerializer extends RemoteServiceSerializer {
 * }
 * </pre>
 */
public abstract interface RemoteServiceSerializer {

    public Serializer getSerializer();

}
