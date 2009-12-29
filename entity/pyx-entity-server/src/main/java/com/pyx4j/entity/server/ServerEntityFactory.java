/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.lang.reflect.Proxy;

import com.pyx4j.entity.server.proxies.EntityHandler;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.impl.IObjectFactoryImpl;

public class ServerEntityFactory implements IObjectFactoryImpl {

    @SuppressWarnings("unchecked")
    public <T extends IEntity<?>> T create(Class<T> clazz) {
        Class<?>[] interfaces = new Class[] { clazz };
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new EntityHandler(clazz));
    }

}
