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
 * Created on 2011-03-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

public class Trace {

    private static class TraceId {

        String id;

        TraceId() {
            id = "";
        }
    }

    private static final ThreadLocal<TraceId> traceId = new ThreadLocal<TraceId>() {
        @Override
        protected TraceId initialValue() {
            return new TraceId();
        }
    };

    public static String enter() {
        TraceId tid = traceId.get();
        String ret = tid.id;
        tid.id += ".";
        return ret + "{";
    }

    public static String id() {
        return traceId.get().id;
    }

    public static String returns() {
        TraceId tid = traceId.get();
        if (tid.id.length() > 0) {
            tid.id = tid.id.substring(0, tid.id.length() - 1);
        }
        return tid.id + "}";
    }

    public static String getCallOrigin(Class<?> apiEntryClass) {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = ste.length - 1; i > 2; i--) {
            if (ste[i].getClassName().equals(apiEntryClass.getName())) {
                return clickableLocation(ste[i + 1]);
            }
        }
        return "";
    }

    /**
     * Make Line# clickable in eclipse
     */
    public static String clickableLocation(StackTraceElement ste) {
        if (ste == null) {
            return "";
        }
        return "\t{ " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")}";
    }

    public static String clickableClassLocation(Class<?> sourceClass) {
        return "\t{ " + sourceClass.getName() + ".<>(" + sourceClass.getSimpleName() + ".java:0)}";
    }
}
