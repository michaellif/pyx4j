/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.concurrent.Callable;

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;

public class NamespaceUtils {

    public static <T> T runInAdminNamespace(final Callable<T> task) {
        final String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            try {
                return task.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new Error(e);
                }
            }
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }
}
