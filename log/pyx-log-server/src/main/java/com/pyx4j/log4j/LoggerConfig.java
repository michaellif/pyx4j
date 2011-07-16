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
 * Created on 2011-02-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4j;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.MDC;

import com.pyx4j.commons.CommonsStringUtils;

public class LoggerConfig {

    public static final String MDC_sessionNum = "sessionNum";

    public static final String MDC_userID = "userID";

    public static final String MDC_namespace = "namespace";

    static Map<String, String> nameVariables = new HashMap<String, String>();

    static {
        String containerHome = ".";
        if (CommonsStringUtils.isStringSet(System.getProperty("catalina.base"))) {
            containerHome = System.getProperty("catalina.base");
        } else if (CommonsStringUtils.isStringSet(System.getProperty("catalina.home"))) {
            containerHome = System.getProperty("catalina.home");
        } else if (CommonsStringUtils.isStringSet(System.getProperty("jetty.home"))) {
            containerHome = System.getProperty("jetty.home");
        } else if (CommonsStringUtils.isStringSet(System.getProperty("container.home"))) {
            containerHome = System.getProperty("container.home");
        }
        setVariable("container.home", containerHome);
    }

    public static void setContextName(String name) {
        nameVariables.put("contextName", name);
    }

    public static String getContextName() {
        return nameVariables.get("contextName");
    }

    public static String getContainerHome() {
        return nameVariables.get("container.home");
    }

    public static void setVariable(String name, String value) {
        nameVariables.put(name, value);
    }

    public static void shutdown() {
        try {
            LogManager.shutdown();
        } catch (NoClassDefFoundError ignore) {
        }
    }

    public static void mdcPut(String key, Object o) {
        try {
            MDC.put(key, o);
        } catch (NoClassDefFoundError ignore) {
        }
    }

    public static void mdcRemove(String key) {
        try {
            MDC.remove(key);
        } catch (NoClassDefFoundError ignore) {
        }
    }
}
