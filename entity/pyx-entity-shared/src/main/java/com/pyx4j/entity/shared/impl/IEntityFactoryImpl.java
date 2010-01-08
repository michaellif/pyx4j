/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import com.pyx4j.entity.shared.IEntity;


public interface IEntityFactoryImpl {

    public <T extends IEntity<?>> T create(Class<T> clazz);

}
