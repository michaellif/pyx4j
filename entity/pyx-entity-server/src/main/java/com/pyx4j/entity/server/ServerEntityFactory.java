/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.impl.IEntityFactoryImpl;

public class ServerEntityFactory implements IEntityFactoryImpl {

    private static final Logger log = LoggerFactory.getLogger(ServerEntityFactory.class);

    @SuppressWarnings("unchecked")
    public <T extends IEntity<?>> T create(Class<T> clazz) {
        String handlerClassName = clazz.getName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        Class<?> handlerClass;
        try {
            handlerClass = Class.forName(handlerClassName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            handlerClass = EntityImplGenerator.instance().generateImplementation(clazz.getName());
        }
        try {
            return (T) handlerClass.newInstance();
        } catch (Throwable e) {
            log.error(handlerClassName + " instantiation error", e);
            throw new Error(e.getMessage());
        }
    }

}
