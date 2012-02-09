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
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.entity.shared.IEntity;

/**
 * Maps to javax.persistence.OneToOne or javax.persistence.OneToMany with CascadeType.ALL
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Owned {

    Class<? extends IEntity> joinTable() default IEntity.class;

    /**
     * Optional, Should match value of @JoinColumn when defined.
     * By default Entity class match is used. Or first column of the same type with @JoinColumn annotation without value.
     */
    Class<? extends ColumnId> mappedby() default ColumnId.class;

    /**
     * Required for IList collections
     */
    Class<? extends ColumnId> orderColumn() default ColumnId.class;

}
