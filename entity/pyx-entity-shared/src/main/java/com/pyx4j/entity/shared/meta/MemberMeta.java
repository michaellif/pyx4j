/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.meta;

import java.util.List;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.validator.Validator;

public interface MemberMeta {

    public String getFieldName();

    public boolean isOwnedRelationships();

    public Class<?> getValueClass();

    public Class<? extends IObject<?, ?>> getObjectClass();

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    public String getCaption();

    /**
     * See com.pyx4j.entity.annotations.Caption
     */
    public String getDescription();

    /**
     * See com.pyx4j.entity.annotations.StringLength
     */
    public int getStringLength();

    public List<Validator> getValidators();

    // --- TODO ---
    //    public boolean isPrimitive();
    //
    //    /**
    //     * Returns <code>true</code> if this field represents Collection type.
    //     */
    //    public boolean isCollection();
    //
    //    /**
    //     * If this field represents an array, Set or List, this method returns the component
    //     * type of the array or Parameterized Type Argument for collections. Otherwise, it
    //     * returns <code>null</code>.
    //     */
    //    public Class<?> getComponentType();    

}
