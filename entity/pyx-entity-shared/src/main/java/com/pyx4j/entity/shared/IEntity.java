/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public interface IEntity<E extends IObject<?, ?>> extends IObject<E, Map<String, Object>> {

    public static String PRIMARY_KEY = "id";

    public String getPrimaryKey();

    public void setPrimaryKey(String pk);

    public Set<String> getMemberNames();

    public IObject<?, ?> getMember(String memberName);

    public Object getMemberValue(String memberName);

    public void setMemberValue(String memberName, Object value);

    /**
     * A single instance of MemeberMeta is shared between all instances of the IEntity.
     */
    public MemberMeta getMemberMeta(String memberName);

    public List<Validator> getValidators(Path memberPath);
}
