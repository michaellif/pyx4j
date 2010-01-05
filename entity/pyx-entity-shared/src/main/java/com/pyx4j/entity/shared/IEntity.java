/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.Map;
import java.util.Set;

public interface IEntity<E extends IObject<?, ?>> extends IObject<E, Map<String, Object>> {

    public String getPrimaryKey();

    public void setPrimaryKey(String pk);

    public Set<String> getMemberNames();

    public IObject<?, ?> getMember(String name);

    public Object getMemberValue(String name);

    public void setMemberValue(String name, Object value);
}
