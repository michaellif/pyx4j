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
 * Created on Oct 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ TYPE, FIELD, METHOD })
@Retention(RetentionPolicy.CLASS)
public @interface I18nComment {

    String value();

    public static enum I18nCommentTarget {

        All,

        /**
         * Only the @I18n (IEntity/Enum) class name and do not affect all its members,
         */
        This,

        /**
         * Only add this comment to members
         */
        Memebers

    }

    /**
     * Where to add this comment, On member or filed this has no effect.
     */
    I18nCommentTarget target() default I18nCommentTarget.All;

}
