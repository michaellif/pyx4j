/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public interface IObject<OBJECT_TYPE extends IObject, VALUE_TYPE> {

    boolean isNull();

    void set(OBJECT_TYPE entity);

    void setValue(VALUE_TYPE value);

    VALUE_TYPE getValue();

    Path getPath();

    Class<? extends IObject> getObjectClass();

    //Owned by parent
    IEntity<?> getParent();

    //In parent's map
    String getFieldName();

}
