/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security;

import java.util.Set;

import com.pyx4j.commons.Key;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.crm.server.services.pub.CrmAuthenticationServiceImpl;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.ob.server.services.OnboardingAuthenticationServiceImpl;
import com.propertyvista.operations.server.services.OperationsAuthenticationServiceImpl;
import com.propertyvista.portal.server.portal.web.services.ResidentAuthenticationServiceImpl;
import com.propertyvista.portal.server.ptapp.services.PtAuthenticationServiceImpl;

public class VistaAclRevalidator implements AclRevalidator {

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        AclRevalidator aclRevalidator;

        VistaApplication app = VistaApplication.getVistaApplication(currentBehaviours);
        if (app == null) {
            return null;
        }
        switch (app) {
        case crm:
            aclRevalidator = new CrmAuthenticationServiceImpl();
            break;
        case operations:
            aclRevalidator = new OperationsAuthenticationServiceImpl();
            break;
        case prospect:
            aclRevalidator = new PtAuthenticationServiceImpl();
            break;
        case resident:
            aclRevalidator = new ResidentAuthenticationServiceImpl();
            break;
        case onboarding:
            aclRevalidator = new OnboardingAuthenticationServiceImpl();
            break;
        default:
            return null;
        }
        return aclRevalidator.getCurrentBehaviours(principalPrimaryKey, currentBehaviours, aclTimeStamp);
    }
}
