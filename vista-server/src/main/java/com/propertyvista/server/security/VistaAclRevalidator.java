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

import com.propertyvista.operations.server.services.AdminAuthenticationServiceImpl;
import com.propertyvista.crm.server.services.pub.CrmAuthenticationServiceImpl;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.ob.server.services.OnboardingAuthenticationServiceImpl;
import com.propertyvista.portal.server.portal.services.PortalAuthenticationServiceImpl;
import com.propertyvista.portal.server.ptapp.services.PtAuthenticationServiceImpl;

public class VistaAclRevalidator implements AclRevalidator {

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        AclRevalidator aclRevalidator;
        if (currentBehaviours.contains(VistaBasicBehavior.CRM) || currentBehaviours.contains(VistaBasicBehavior.CRMPasswordChangeRequired)) {
            aclRevalidator = new CrmAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.Operations) || currentBehaviours.contains(VistaBasicBehavior.OperationsPasswordChangeRequired)) {
            aclRevalidator = new AdminAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.ProspectiveApp)
                || currentBehaviours.contains(VistaBasicBehavior.ProspectiveAppPasswordChangeRequired)) {
            aclRevalidator = new PtAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.TenantPortal)
                || currentBehaviours.contains(VistaBasicBehavior.TenantPortalPasswordChangeRequired)) {
            aclRevalidator = new PortalAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.Onboarding)) {
            aclRevalidator = new OnboardingAuthenticationServiceImpl();
        } else {
            return null;
        }
        return aclRevalidator.getCurrentBehaviours(principalPrimaryKey, currentBehaviours, aclTimeStamp);
    }
}
