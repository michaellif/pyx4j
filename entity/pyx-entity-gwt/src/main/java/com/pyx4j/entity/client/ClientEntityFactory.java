/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client;

import com.google.gwt.core.client.GWT;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;

public class ClientEntityFactory implements IEntityFactoryImpl {

    private static IEntityFactoryImpl singleFactory = null;

    /**
     * Call this function in client code before compiler reached RPC RemoteService to
     * ensure IEntityImplementations are serialized.
     */
    public static void ensureIEntityImplementations() {
    }

    public <T extends IEntity<?>> T create(Class<T> clazz) {
        if (singleFactory == null) {
            synchronized (ClientEntityFactory.class) {
                singleFactory = GWT.create(IEntityFactoryImpl.class);
            }
        }
        return singleFactory.create(clazz);
    }
}
