/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public interface IEntity<TYPE, E extends IEntity<?, ?>> {

    boolean isNull();

    void set(E entity);

    void setValue(TYPE value);

    TYPE getValue();

    Path getPath();

}
