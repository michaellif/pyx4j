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

import com.pyx4j.entity.server.proxies.ObjectHandler;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.IEntityFactory;

public class ServerEntityFactory implements IEntityFactory {

    @SuppressWarnings("unchecked")
    public <T extends IObject<?>> T create(Class<T> clazz) {
        Class<?>[] interfaces = new Class[] { clazz };
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new ObjectHandler(clazz));
    }

}
