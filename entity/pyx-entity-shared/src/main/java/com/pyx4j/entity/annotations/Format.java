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
 * Created on Feb 18, 2010
 * @author vlads
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nAnnotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@I18nAnnotation(element = "")
public @interface Format {

    /**
     * If messageFormat set to 'true' then value use as argument SimpleMessageFormat.format.
     * 
     * @see java.text.MessageFormat.format
     */
    @I18n(javaFormatFlag = true)
    String value();

    boolean messageFormat() default false;

    /**
     * String presentations for 'null' values
     */
    @I18n
    String nil() default "";

}
