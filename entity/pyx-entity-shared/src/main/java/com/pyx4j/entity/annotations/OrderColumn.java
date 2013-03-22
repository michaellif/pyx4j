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
 * Created on Jan 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Member of type IPrimitive<Integer> for sorting IList collections.
 * The order of Collection is materialized in the database.
 * 
 * If you don't need Persistence to maintain order then use <tt>@OrderBy(PrimaryKey.class)</tt> in owned IList declaration,
 * order would be unchangeable and governed by insertion order,
 * 
 * Analog of org.hibernate.annotations.IndexColumn
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderColumn {

    Class<? extends ColumnId> value() default ColumnId.class;

}
