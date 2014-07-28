/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services;

import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.portal.shared.services.PortalPasswordResetService;
import com.propertyvista.portal.server.portal.prospect.services.ProspectAuthenticationServiceImpl;
import com.propertyvista.portal.server.portal.resident.services.ResidentAuthenticationServiceImpl;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;

public class PortalPasswordResetServiceImpl extends VistaPasswordResetServiceImpl<CustomerUserCredential> implements PortalPasswordResetService {

    public PortalPasswordResetServiceImpl() {
        super(CustomerUserCredential.class);
    }

    @Override
    protected AuthenticationResponse authorize(CustomerUserCredential credentials) {
        // Actually do logout to return user to login page
        if (SecurityController.check(VistaBasicBehavior.ProspectPortalPasswordChangeRequired)) {
            Lifecycle.endSession();
            return new ProspectAuthenticationServiceImpl().createAuthenticationResponse(null);
        } else {
            Lifecycle.endSession();
            return new ResidentAuthenticationServiceImpl().createAuthenticationResponse(null);
        }
    }

}
