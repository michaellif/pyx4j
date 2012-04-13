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
 * Created on 2012-04-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

/**
 * Defines the set of operations that are propagated to the associated entity.
 * The value cascade=ALL is equivalent to cascade={PERSIST,REMOVE}.
 */
public enum CascadeType {

    /**
     * Cascade all operations
     */
    ALL,

    /**
     * Cascade persist/merge operations
     */
    PERSIST,

    /**
     * Cascade remove operations
     */
    DELETE
}
