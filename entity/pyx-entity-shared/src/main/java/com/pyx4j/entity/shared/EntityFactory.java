/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;

public class EntityFactory {

    private static IEntityFactoryImpl impl;

    public static void setImplementation(IEntityFactoryImpl impl) {
        EntityFactory.impl = impl;
    }

    public static <T extends IEntity<?>> T create(Class<T> clazz) {
        return impl.create(clazz);
    }
}
