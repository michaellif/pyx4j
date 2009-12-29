/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 11, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;

/**
 * Base interface for Remote Service implementations.
 * 
 * Use RPCManager.execute(...) in client code;
 */
public interface Service<I extends Serializable, O extends Serializable> {

    public O execute(I request);

}
