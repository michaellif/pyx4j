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
 * Created on Dec 1, 2011
 * @author vlads
 */
package com.pyx4j.rpc.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pyx4j.i18n.annotations.I18nAnnotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@I18nAnnotation(element = "waitCaption")
public @interface ServiceExecution {

    public static enum OperationType {

        NonBlocking,

        Transparent,

        SemiTransparent

    }

    OperationType operationType() default OperationType.Transparent;

    String waitCaption() default "";

    /**
     * Single file processing of Requests.
     * TODO We can add multiple queues.
     */
    Class<? extends ServiceQueueId> queue() default ServiceQueueId.class;

    /**
     * Allow to Cache the service call results in client, forever
     * 
     * @return
     */
    boolean cacheable() default false;

}
