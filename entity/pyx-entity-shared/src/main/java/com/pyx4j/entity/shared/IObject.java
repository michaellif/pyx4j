/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import com.pyx4j.entity.shared.meta.MemberMeta;

@SuppressWarnings("unchecked")
public interface IObject<OBJECT_TYPE extends IObject, VALUE_TYPE> {

    public boolean isNull();

    public void set(OBJECT_TYPE entity);

    public void setValue(VALUE_TYPE value);

    public VALUE_TYPE getValue();

    public Path getPath();

    public Class<? extends IObject> getObjectClass();

    //Owned by parent
    public IEntity<?> getParent();

    //In parent's map
    public String getFieldName();

    public MemberMeta getMeta();

}
