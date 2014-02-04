/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Feb 4, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.Arrays;
import java.util.List;

/**
 * Compile time trace settings.
 */
public final class PersistenceTrace {

    public static final boolean trace = false;

    public static final boolean traceSql = false;

    public static final boolean traceOpenSession = false;

    public static final boolean traceTransaction = false;

    public static final boolean traceWarnings = false;

    public static final boolean traceEntity = false;

    public static final List<String> traceEntities = Arrays.asList("SampleShortClassName1", "SampleShotClassName2");

    private PersistenceTrace() {
    }

}
