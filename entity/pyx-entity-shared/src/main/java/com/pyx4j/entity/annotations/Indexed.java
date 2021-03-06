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
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.entity.core.adapters.IndexAdapter;

/**
 * Define a index in RDBMS or indexed property in App Engine persistent storage.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {

    // Name of this index in DB
    String name() default "";

    boolean uniqueConstraint() default false;

    //Create case insensitive index on LOWER(value), works when FunctionIndexesSupported.
    boolean ignoreCase() default false;

    // GAE heritage: add column value to index
    boolean indexPrimaryValue() default true;

    Class<? extends IndexAdapter<?>>[] adapters() default {};

    // GAE heritage:
    int keywordLength() default 0;

    // GAE heritage:
    char global() default 0;

    /**
     * Allow for multi-column indexes, And multiple indexes on the same column.
     *
     * Unique per hierarchy: start with discriminator
     */
    String[] group() default {};

    // PostgreSQL:  btree(default), hash, gist, and gin
    // Oracle:  btree(default), bitmap
    // Ignored for other database types.
    String method() default "";
}
