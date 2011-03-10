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

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

public class VistaLifecycle {

    public static void beginSession(UserVisit userVisit, Set<Behavior> behaviours) {

        //OpenId
        if (SecurityController.checkBehavior(CoreBehavior.USER)) {
            behaviours.add(CoreBehavior.USER);
        }

        String hasOpenIdEmail = null;
        if (Context.getVisit() != null) {
            hasOpenIdEmail = (String) Context.getVisit().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE);
        }

        Lifecycle.beginSession(userVisit, behaviours);

        if (hasOpenIdEmail != null) {
            Context.getVisit().setAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE, hasOpenIdEmail);
        }
    }

    public static void endSession() {
        boolean hasOpenIdSession = SecurityController.checkBehavior(CoreBehavior.USER);
        String hasOpenIdEmail = (String) Context.getVisit().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE);
        Lifecycle.endSession();
        if (hasOpenIdSession) {
            Set<Behavior> behaviours = new HashSet<Behavior>();
            behaviours.add(CoreBehavior.USER);
            Lifecycle.beginSession(null, behaviours);
            if (hasOpenIdEmail != null) {
                Context.getVisit().setAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE, hasOpenIdEmail);
            }
        }
    }
}
