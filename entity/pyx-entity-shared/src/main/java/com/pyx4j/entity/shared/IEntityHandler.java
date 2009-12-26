/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public interface IEntityHandler<T extends IObject<?, ?>> {

    public Class<T> getEntityClass();

    public String getFieldName();

    public IEntityHandler<?> getParentHandler();

}
