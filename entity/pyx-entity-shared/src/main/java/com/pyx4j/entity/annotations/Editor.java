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

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Editor {

    public static enum EditorType {

        text, label,

        password, passwordCreator,

        textarea, richtextarea,

        combo, captcha,

        monthyearpicker, yearpicker, timepicker,

        email, phone,

        money, moneylabel, percentage, percentagelabel,

        radiogroup, entityselector,

        @Deprecated color,

        @Deprecated hue,

        // Form should provide the  Editor for this component, there are no default factory.
        custom
    }

    /**
     * UI Editor type of the member.
     */
    EditorType type();

    /**
     * Define additional editor descriptor used by component factory to create editor.
     */
    String descriptor() default "";

}
