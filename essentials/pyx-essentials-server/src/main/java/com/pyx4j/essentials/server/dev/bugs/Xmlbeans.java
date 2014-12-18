/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-05-13
 * @author vlads
 */
package com.pyx4j.essentials.server.dev.bugs;

import java.lang.reflect.Field;

import org.apache.xmlbeans.impl.common.SystemCache;

public class Xmlbeans {

    public static void fixMemoryLeaks() {
        fixCharUtilLeaks();
        fixXmlBeansLeaks();
        fixXmlBeansLocaleLeaks();
        fixSchemaTypeLoaderCache();
    }

    /**
     * ThreadLocal with key of type [org.apache.xmlbeans.impl.store.CharUtil$1] (value [org.apache.xmlbeans.impl.store.CharUtil$1@1e9cd576]) and a value of type
     * [java.lang.ref.SoftReference] (value [java.lang.ref.SoftReference@4a8245ad]) but failed to remove it
     */
    public static void fixCharUtilLeaks() {
        Class<?> type = org.apache.xmlbeans.impl.store.CharUtil.class;
        try {
            Field field = type.getDeclaredField("tl_charUtil");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
            if (threadLocal != null) {
                threadLocal.remove();
            }
        } catch (Throwable e) {
            System.out.println("Failed to clear CharUtil ThreadLocal " + e);
        }
    }

    public static void fixXmlBeansLeaks() {
        Class<?> type = org.apache.xmlbeans.XmlBeans.class;
        try {
            Field field = type.getDeclaredField("_threadLocalLoaderQNameCache");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
            if (threadLocal != null) {
                threadLocal.remove();
            }
        } catch (Throwable e) {
            System.out.println("Failed to clear XmlBeans ThreadLocal " + e);
        }
    }

    public static void fixXmlBeansLocaleLeaks() {
        Class<?> type = org.apache.xmlbeans.impl.store.Locale.class;
        try {
            Field field = type.getDeclaredField("tl_scrubBuffer");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
            if (threadLocal != null) {
                threadLocal.remove();
            }
        } catch (Throwable e) {
            System.out.println("Failed to clear Locale ThreadLocal " + e);
        }
    }

    public static void fixSchemaTypeLoaderCache() {
        try {

            Class<?> type = Class.forName("org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl$SchemaTypeLoaderCache");
            Field field = type.getDeclaredField("_cachedTypeSystems");
            field.setAccessible(true);
            ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(SystemCache.get());
            if (threadLocal != null) {
                threadLocal.remove();
            }
        } catch (Throwable e) {
            System.out.println("Failed to clear SchemaTypeLoaderCache ThreadLocal " + e);
        }
    }
}
