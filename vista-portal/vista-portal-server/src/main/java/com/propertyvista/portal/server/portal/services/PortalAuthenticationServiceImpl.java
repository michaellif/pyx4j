/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.Set;

import com.pyx4j.security.shared.Behavior;

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class PortalAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<TenantUser, TenantUserCredential> implements PortalAuthenticationService {

    public PortalAuthenticationServiceImpl() {
        super(TenantUser.class, TenantUserCredential.class);
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.TenantPortal;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaTenantBehavior.PasswordChangeRequired;
    }

    @Override
    protected void addBehaviors(TenantUserCredential userCredential, Set<Behavior> behaviors) {
        behaviors.addAll(userCredential.behaviors());
    }
}
