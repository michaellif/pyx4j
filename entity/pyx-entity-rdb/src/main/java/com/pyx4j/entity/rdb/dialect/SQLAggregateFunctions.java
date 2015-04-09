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
 * Created on Jan 2, 2011
 * @author vlads
 */
package com.pyx4j.entity.rdb.dialect;

public enum SQLAggregateFunctions {

    /**
     * Returns the average value
     */
    AVG,

    /**
     * Returns the number of rows
     */
    COUNT,

    /**
     * Returns the first value
     */
    FIRST,

    /**
     * Returns the last value
     */
    LAST,

    /**
     * Returns the largest value
     */
    MAX,

    /**
     * Returns the smallest value
     */
    MIN,

    /**
     * Returns the sum
     */
    SUM

}
