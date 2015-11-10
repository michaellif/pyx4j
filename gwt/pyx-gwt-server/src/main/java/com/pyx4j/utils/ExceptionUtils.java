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
 * Created on Nov 10, 2015
 * @author vlads
 */
package com.pyx4j.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtils {

    private final static Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    public static String safeStackTrace(Throwable t) {
        try {
            StringWriter sf = new StringWriter();
            t.printStackTrace(new PrintWriter(sf));
            return sf.toString();
        } catch (Throwable ignore) {
            // probably class not found, try to extract more information.
            log.warn("Can't completely extract stackTrace {}", getSafeErrorMessage(ignore));
        }
        return forceSafeStackTrace(t);
    }

    public static String forceSafeStackTrace(Throwable t) {
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        StringBuilder b = new StringBuilder();
        try {
            printStackTrace(t, b, dejaVu);
        } catch (Throwable e) {
            log.warn("Can't completely extract stackTrace {}", getSafeErrorMessage(e));
            b.append("StackTrace extraction terminated:").append(getSafeErrorMessage(e));
        }
        return b.toString();
    }

    private static void printStackTrace(Throwable t, StringBuilder b, Set<Throwable> dejaVu) {
        if (dejaVu.contains(t)) {
            return;
        }
        b.append(t.toString()).append("\n");
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            b.append("\tat " + traceElement).append("\n");
        }

        Throwable cause = t.getCause();
        if ((cause != null) && (cause != t)) {
            b.append("Caused by: \n");
            printStackTrace(cause, b, dejaVu);
        }

        dejaVu.add(t);
    }

    public static String getSafeErrorMessage(Throwable e) {
        String className = "n/a";
        try {
            className = e.getClass().getName();
            String msg = e.getMessage();
            if (msg != null) {
                if (className.equals(msg) || msg.startsWith(className)) {
                    return msg;
                } else {
                    return className + ": " + msg;
                }
            } else {
                return e.toString();
            }
        } catch (Throwable ignore) {
            return className;
        }
    }
}
