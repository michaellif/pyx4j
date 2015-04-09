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
 * Created on 2011-02-08
 * @author antonk
 */
package com.pyx4j.site.rpc.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@I18nAnnotation(element = "caption")
public @interface PlaceProperties {

    /**
     * If not specified the caption value is automatically generated from Place class name.
     */
    @I18n
    String caption() default I18nAnnotation.DEFAULT_VALUE;

    /**
     * If not specified the navigLabel is taken from @PlaceProperties.caption
     */
    @I18n
    String navigLabel() default I18nAnnotation.DEFAULT_VALUE;
}
