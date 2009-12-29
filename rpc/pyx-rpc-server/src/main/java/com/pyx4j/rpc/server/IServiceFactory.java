/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import com.pyx4j.rpc.shared.Service;

public interface IServiceFactory {

    public Class<? extends Service<?, ?>> getServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException;

}
