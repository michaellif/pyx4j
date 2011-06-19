/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.Set;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Lifecycle;

public class VistaLifecycle {

    public static String beginSession(UserVisit userVisit, Set<Behavior> behaviours) {
        String sessionToken = Lifecycle.beginSession(userVisit, behaviours);
        return sessionToken;
    }

    public static void endSession() {
        Lifecycle.endSession();
    }
}
