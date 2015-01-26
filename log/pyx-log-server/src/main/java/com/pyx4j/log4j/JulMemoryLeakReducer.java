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
 * Created on May 24, 2014
 * @author vlads
 */
package com.pyx4j.log4j;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JulMemoryLeakReducer {

    /**
     * JDK-6543126 : Level.known can leak memory, http://bugs.java.com/view_bug.do?bug_id=6543126
     * 
     * example OracleLog$OracleLevel, com.bea.logging.LogLevel
     */
    public static void shutdown() {
        clearClassLoaderRefferences(JulMemoryLeakReducer.class.getClassLoader());
    }

    public static void clearClassLoaderRefferences(ClassLoader classLoader) {
        try {
            Class<?> type = Class.forName(java.util.logging.Level.class.getName() + "$KnownLevel");
            Field levelObjectField = type.getDeclaredField("levelObject");
            levelObjectField.setAccessible(true);

            Field nameToLevelsField = type.getDeclaredField("nameToLevels");
            nameToLevelsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<Object>> nameToLevels = (Map<String, List<Object>>) nameToLevelsField.get(null);
            for (Map.Entry<String, List<Object>> me : nameToLevels.entrySet()) {
                Iterator<Object> it = me.getValue().iterator();
                while (it.hasNext()) {
                    Object knownLevel = it.next();
                    Object customeLevel = levelObjectField.get(knownLevel);
                    if (customeLevel.getClass().getClassLoader() == classLoader) {
                        it.remove();
                    }
                }
            }

            Field intToLevelsField = type.getDeclaredField("intToLevels");
            intToLevelsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<Object>> intToLevels = (Map<String, List<Object>>) intToLevelsField.get(null);
            for (Map.Entry<String, List<Object>> me : intToLevels.entrySet()) {
                Iterator<Object> it = me.getValue().iterator();
                while (it.hasNext()) {
                    Object knownLevel = it.next();
                    Object customeLevel = levelObjectField.get(knownLevel);
                    if (customeLevel.getClass().getClassLoader() == classLoader) {
                        it.remove();
                    }
                }
            }
        } catch (Throwable ignore) {
            System.err.println("Unable to clear custom java.util.logging.Level references, expect Memory leaks");
            ignore.printStackTrace();
        }
    }
}
