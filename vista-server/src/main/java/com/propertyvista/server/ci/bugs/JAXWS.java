/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci.bugs;

import java.lang.reflect.Field;

import com.sun.xml.ws.api.server.ContainerResolver;

public class JAXWS {

    /**
     * SEVERE: The web application [/vista] created a ThreadLocal with key of type [com.sun.xml.ws.api.server.ThreadLocalContainerResolver$1]
     * 
     * VladS: I have no better idea how to fix it.
     */
    public static void fixMemoryLeaks() {
        Class<?> type = com.sun.xml.ws.api.server.ThreadLocalContainerResolver.class;
        if ((type.getClassLoader() == JAXWS.class.getClassLoader()) || (type.getClassLoader() == Thread.currentThread().getContextClassLoader())) {
            try {
                Field containersField = type.getDeclaredField("containers");
                containersField.setAccessible(true);
                ThreadLocal<?> threadLocal = (ThreadLocal<?>) containersField.get(ContainerResolver.getDefault());
                if (threadLocal != null) {
                    threadLocal.remove();
                }
            } catch (Throwable e) {
                System.out.println("Failed to clear JAXWS ThreadLocalContainerResolver " + e);
            }
        }
    }
}
