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

import com.propertyvista.admin.server.services.AdminAuthenticationServiceImpl;
import com.propertyvista.crm.server.services.pub.CrmAuthenticationServiceImpl;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.portal.server.portal.services.PortalAuthenticationServiceImpl;
import com.propertyvista.portal.server.ptapp.services.PtAuthenticationServiceImpl;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;

public class VistaAclRevalidator implements AclRevalidator {

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        VistaAuthenticationServicesImpl<?, ?> authenticationServices;
        if (currentBehaviours.contains(VistaBasicBehavior.CRM) || currentBehaviours.contains(VistaBasicBehavior.CRMPasswordChangeRequired)) {
            authenticationServices = new CrmAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.Admin) || currentBehaviours.contains(VistaBasicBehavior.AdminPasswordChangeRequired)) {
            authenticationServices = new AdminAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.ProspectiveApp)
                || currentBehaviours.contains(VistaBasicBehavior.ProspectiveAppPasswordChangeRequired)) {
            authenticationServices = new PtAuthenticationServiceImpl();
        } else if (currentBehaviours.contains(VistaBasicBehavior.TenantPortal)
                || currentBehaviours.contains(VistaBasicBehavior.TenantPortalPasswordChangeRequired)) {
            authenticationServices = new PortalAuthenticationServiceImpl();
        } else {
            return null;
        }
        return authenticationServices.getCurrentBehaviours(principalPrimaryKey, currentBehaviours);
    }
}
