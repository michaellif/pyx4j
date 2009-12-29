/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;

public abstract class AbstractClientEntityFactoryImpl implements IEntityFactoryImpl {

    private final Map<Class<? extends IEntity<?>>, IEntityFactoryImpl> implementationsMap;

    protected AbstractClientEntityFactoryImpl() {
        implementationsMap = new HashMap<Class<? extends IEntity<?>>, IEntityFactoryImpl>();
    }

    @Override
    public <T extends IEntity<?>> T create(Class<T> clazz) {
        IEntityFactoryImpl implCreator = implementationsMap.get(clazz);
        if (implCreator == null) {
            throw new Error("Class " + clazz.getName() + " implementation not found");
        }
        return implCreator.create(clazz);
    }

    protected void addClassFactory(Class<? extends IEntity<?>> clazz, IEntityFactoryImpl implCreator) {
        implementationsMap.put(clazz, implCreator);
    }
}
