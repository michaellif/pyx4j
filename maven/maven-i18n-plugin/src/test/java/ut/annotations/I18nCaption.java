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
 * Created on Oct 7, 2011
 * @author vlads
 * @version $Id$
 */
package ut.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.i18n.annotations.I18nAnnotation;
import com.pyx4j.i18n.annotations.I18n;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@I18nAnnotation(element = "name")
public @interface I18nCaption {

    //TODO change when moving to Java 7
    public static String DEFAULT_VALUE = I18nAnnotation.DEFAULT_VALUE;

    String name() default DEFAULT_VALUE;

    @I18n
    String description() default "";

}
