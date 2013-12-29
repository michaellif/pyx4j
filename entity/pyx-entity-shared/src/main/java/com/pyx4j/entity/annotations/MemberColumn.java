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
 * Created on 2010-08-12
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.entity.core.adapters.IndexAdapter;
import com.pyx4j.entity.core.adapters.MemberModificationAdapter;
import com.pyx4j.entity.core.adapters.PersistenceAdapter;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberColumn {

    String name() default "";

    /**
     * Add NOT NULL CONSTRAINT to database.
     */
    boolean notNull() default false;

    @SuppressWarnings("rawtypes")
    Class<? extends PersistenceAdapter> persistenceAdapter() default PersistenceAdapter.class;

    /**
     * TODO Use Facade to for business value change events.
     * TODO add Security adapters
     */
    Class<? extends MemberModificationAdapter<?>>[] modificationAdapters() default {};

    @SuppressWarnings("rawtypes")
    Class<? extends IndexAdapter> sortAdapter() default IndexAdapter.class;

    Class<? extends ColumnId> value() default ColumnId.class;

    /**
     * The precision for a decimal (exact numeric) column. (Applies only if a decimal column is used.)
     * 
     * For example, precision = 7 and scale 2 is a number that has 5 digits before the decimal and 2 digits after the decimal
     */
    public int precision() default -1;

    /**
     * The scale for a decimal (exact numeric) column. (Applies only if a decimal column is used.)
     * Number of digits after the decimal.
     */
    public int scale() default -1;

}
