/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.server.contexts.NamespaceManager;

public class VistaTestDBSetup {

    private static ServerSideConfiguration initOnce = null;

    public static synchronized void init() {
        if (initOnce == null) {
            boolean testOnMySQL = true;
            initOnce = new VistaTestsServerSideConfiguration(testOnMySQL);
            ServerSideConfiguration.setInstance(initOnce);
        }
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
    }

}
