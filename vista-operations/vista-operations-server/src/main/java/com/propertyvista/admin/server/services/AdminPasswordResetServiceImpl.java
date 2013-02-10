/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.rpc.services.AdminPasswordResetService;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;

public class AdminPasswordResetServiceImpl extends VistaPasswordResetServiceImpl<AdminUserCredential> implements AdminPasswordResetService {

    public AdminPasswordResetServiceImpl() {
        super(AdminUserCredential.class);
    }

    @Override
    protected AuthenticationResponse authenticate(AdminUserCredential credentials) {
        return new AdminAuthenticationServiceImpl().authenticate(credentials, null);
    }

}
