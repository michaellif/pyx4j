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
 */
package com.pyx4j.entity.rdb;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;

/**
 * Compile time trace settings.
 */
public final class PersistenceTrace {

    public static final boolean trace = false;

    public static final boolean traceSql = false;

    public static final boolean traceOpenSession = false;

    public static final boolean traceTransaction = false;

    public static final boolean traceWarnings = false;

    public static final boolean traceWrite = false;

    public static final boolean traceCache = false;

    public static final boolean traceEntity = false;

    public static final List<String> traceEntities = Arrays.asList("SampleShortClassName1", "SampleShortClassName2");

    private PersistenceTrace() {
    }

    public static boolean traceEntityFilter(IEntity entity) {
        if (!traceEntity) {
            return false;
        } else {
            for (String name : traceEntities) {
                if (entity.getEntityMeta().getEntityClass().getName().endsWith("." + name)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String getCallOrigin() {
        return Trace.getCallOrigin(EntityPersistenceServiceRDB.class, UnitOfWork.class, Persistence.class);
    }

    public static String traceTime(long startTime) {
        if (startTime > 0) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(startTime)) + " (" + TimeUtils.secSince(startTime) + ")";
        } else {
            return "n/a";
        }
    }

}
