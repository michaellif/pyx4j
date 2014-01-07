/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.crm.rpc.services.security.CrmPasswordResetService;
import com.propertyvista.crm.server.services.pub.CrmAuthenticationServiceImpl;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmPasswordResetServiceImpl extends VistaPasswordResetServiceImpl<CrmUserCredential> implements CrmPasswordResetService {

    public CrmPasswordResetServiceImpl() {
        super(CrmUserCredential.class);
    }

    @Override
    protected AuthenticationResponse authorize(CrmUserCredential credentials) {
        return new CrmAuthenticationServiceImpl().authenticate(credentials, null);
    }

}
