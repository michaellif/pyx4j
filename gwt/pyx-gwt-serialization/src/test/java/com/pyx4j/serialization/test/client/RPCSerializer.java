/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import com.pyx4j.serialization.client.RemoteServiceSerializer;
import com.pyx4j.serialization.client.RemoteServiceTarget;

@RemoteServiceTarget(SomeRemoteService.class)
public interface RPCSerializer extends RemoteServiceSerializer {

}
