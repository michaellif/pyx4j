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
 * Created on Nov 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.log4gwt.shared.LogEvent;

/**
 * This class is used in Hosted mode to format console output
 */
public class HostedStackTraceFormater implements AppenderStdOut.StackTraceFormater {

    private static final String FQCN = ClientLogger.class.getName();

    private static final Set<String> fqcnSet = new HashSet<String>();

    private static final Set<String> logFunctionsSet = new HashSet<String>();

    private static boolean locationEnabled = true;

    static {
        fqcnSet.add(FQCN);
        logFunctionsSet.add("trace");
        logFunctionsSet.add("debug");
        logFunctionsSet.add("log");
        logFunctionsSet.add("error");
        logFunctionsSet.add("fatal");
        logFunctionsSet.add("info");
        logFunctionsSet.add("warn");
    }

    @Override
    public String format(LogEvent event) {
        return " " + formatLocation(getLocation());
    }

    private static StackTraceElement getLocation() {
        if (!locationEnabled) {
            return null;
        }
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            boolean wrapperFound = false;
            for (int i = 0; i < ste.length - 1; i++) {
                if (fqcnSet.contains(ste[i].getClassName())) {
                    wrapperFound = false;
                    String nextClassName = ste[i + 1].getClassName();
                    if (nextClassName.startsWith("java.") || nextClassName.startsWith("sun.")) {
                        continue;
                    }
                    if (!fqcnSet.contains(nextClassName)) {
                        if (logFunctionsSet.contains(ste[i + 1].getMethodName())) {
                            wrapperFound = true;
                        } else {
                            // if dynamic proxy classes
                            if (nextClassName.startsWith("$Proxy")) {
                                return ste[i + 2];
                            } else {
                                return ste[i + 1];
                            }
                        }
                    }
                } else if (wrapperFound) {
                    if (!logFunctionsSet.contains(ste[i].getMethodName())) {
                        return ste[i];
                    }
                }
            }
            return ste[ste.length - 1];
        } catch (Throwable ignore) {
        }
        return null;
    }

    public static String formatLocation(StackTraceElement ste) {
        if (ste == null) {
            return "";
        }
        // Make Line# clickable in eclipse
        return "\t{ " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")}";
    }
}
