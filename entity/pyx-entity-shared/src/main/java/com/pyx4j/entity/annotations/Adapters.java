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
 * Created on Nov 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.entity.adapters.EntityModificationAdapter;
import com.pyx4j.entity.adapters.MemberModificationAdapter;

/**
 * Collection of Entity behaviors that are used on back-end.
 * 
 * This works like database triggers and are triggered/called only when Persistence.service().merge(...) is called.
 * 
 * TODO find a better name.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapters {

    Class<? extends MemberModificationAdapter<?>>[] memberModificationAdapters() default {};

    /**
     * EntityModificationAdapters are fired after all MemberModificationAdapters
     */
    Class<? extends EntityModificationAdapter<?>>[] entityModificationAdapters() default {};
}
