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
import com.pyx4j.security.server.AclRevalidator;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.crm.server.services.pub.CrmAuthenticationServiceImpl;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.ob.server.services.OnboardingAuthenticationServiceImpl;
import com.propertyvista.operations.server.services.OperationsAuthenticationServiceImpl;
import com.propertyvista.portal.server.portal.prospect.services.ProspectAuthenticationServiceImpl;
import com.propertyvista.portal.server.portal.resident.services.ResidentAuthenticationServiceImpl;

public class VistaAclRevalidator implements AclRevalidator {

    private AclRevalidator getApplicationAclRevalidator(Set<Behavior> behaviours) {
        VistaApplication app = VistaApplication.getVistaApplication(behaviours);
        if (app == null) {
            return null;
        }
        switch (app) {
        case crm:
            return new CrmAuthenticationServiceImpl();
        case operations:
            return new OperationsAuthenticationServiceImpl();
        case prospect:
            return new ProspectAuthenticationServiceImpl();
        case resident:
            return new ResidentAuthenticationServiceImpl();
        case onboarding:
            return new OnboardingAuthenticationServiceImpl();
        default:
            return null;
        }
    }

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        AclRevalidator aclRevalidator = getApplicationAclRevalidator(currentBehaviours);
        if (aclRevalidator == null) {
            return null;
        } else {
            return aclRevalidator.getCurrentBehaviours(principalPrimaryKey, currentBehaviours, aclTimeStamp);
        }
    }

    @Override
    public void reAuthorizeCurrentVisit(Set<Behavior> behaviours) {
        getApplicationAclRevalidator(behaviours).reAuthorizeCurrentVisit(behaviours);
    }
}
