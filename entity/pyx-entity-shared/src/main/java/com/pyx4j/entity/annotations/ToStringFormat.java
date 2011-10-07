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

import com.pyx4j.i18n.shared.IsTranslation;
import com.pyx4j.i18n.shared.Translatable;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@IsTranslation(element = "")
public @interface ToStringFormat {

    /**
     * All arguments are converted to Strings, used as argument SimpleMessageFormat.format
     * 
     * @see java.text.MessageFormat.format
     */
    @Translatable
    String value();

    /**
     * String presentations for 'isNull()' IEntity values
     */
    @Translatable
    String nil() default "";
}
