/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import com.pyx4j.entity.shared.IEntity;

public class EntityFactory {

    private static IEntityFactory impl;

    public static void setImplementation(IEntityFactory impl) {
        EntityFactory.impl = impl;
    }

    public static <T extends IEntity<?>> T create(Class<T> clazz) {
        return impl.create(clazz);
    }
}
