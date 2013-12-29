/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Dec 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.entity.core.IEntity;

/**
 * Define the join table between entities for many(one) to many(one) relationship.
 * 
 * @OrderBy Required for IList collections
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {

    Class<? extends IEntity> value();

    /**
     * The operations that must be cascaded to the the association table (value).
     * The operations are not cascaded to member unless they are @Owned
     * 
     * By default no operations are cascaded.
     */
    CascadeType[] cascade() default {};

    /**
     * Optional, Should match value of @JoinColumn when defined.
     * By default Entity class match is used. Or first column of the same type with @JoinColumn annotation without value.
     */
    Class<? extends ColumnId> mappedBy() default ColumnId.class;

    //?
    //Class<? extends ColumnId> mapsTo() default ColumnId.class;
}
