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
package com.propertyvista.portal.server.portal.services;

import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.portal.rpc.portal.services.PortalPasswordResetService;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class PortalPasswordResetServiceImpl extends VistaPasswordResetServiceImpl<TenantUserCredential> implements PortalPasswordResetService {

    public PortalPasswordResetServiceImpl() {
        super(TenantUserCredential.class);
    }

    @Override
    protected AuthenticationResponse authenticate(TenantUserCredential credentials) {
        return new PortalAuthenticationServiceImpl().authenticate(credentials);
    }

}
