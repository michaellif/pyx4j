/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 1, 2016
 * @author vlads
 */
package com.pyx4j.rpc.shared;

/**
 * Allow to Cache the service call results in client.
 *
 * Values are stored in @see ClientCache with the service type as key
 *
 * @see also ServiceCacheKey to anotate the argumens
 */
public @interface ServiceCacheable {

    /**
     * Cache expiry time in minutes.
     *
     * 0 indefinite.
     *
     */
    int timeoutMin() default 0;

}
