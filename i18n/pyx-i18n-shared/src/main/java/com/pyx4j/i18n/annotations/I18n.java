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
 * Created on 2011-05-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to include class , interface or enum members to translation table.
 */
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {

    boolean capitalize() default true;

    public static enum I18nStrategy {

        TranslateAll,

        /**
         * Only class and interface that are inherited from annotated one are translatable.
         * This does not affect classes that implement the annotated interface.
         * 
         * We don't use @Inherited because we do bytecode level extraction of text and it does not affect the bytecode of child classes
         */
        DerivedOnly,

        IgnoreMemeber,

        IgnoreAll

    }

    /**
     * Extract and Translate strategy
     */
    I18nStrategy strategy() default I18nStrategy.TranslateAll;

}
