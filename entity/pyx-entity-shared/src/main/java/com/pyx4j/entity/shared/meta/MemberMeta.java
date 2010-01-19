/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.meta;

import java.lang.annotation.Annotation;
import java.util.List;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.validator.Validator;

/**
 * Represent the Meta data denoted using annotation. For consistency it is better to use
 * this class instead of using annotations directly. Implementation of this class is
 * generated for GWT runtime.
 */
public interface MemberMeta {

    public String getFieldName();

    /**
     * See com.pyx4j.entity.annotations.Transient
     */
    public boolean isTransient();

    /**
     * See com.pyx4j.entity.annotations.Detached
     */
    public boolean isDetached();

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

    public boolean isValidatorAnnotationPresent(Class<? extends Annotation> annotationClass);

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
