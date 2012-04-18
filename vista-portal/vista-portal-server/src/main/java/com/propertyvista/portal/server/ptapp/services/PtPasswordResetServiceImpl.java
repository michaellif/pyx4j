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
package com.propertyvista.portal.server.ptapp.services;

import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.portal.rpc.ptapp.services.PtPasswordResetService;
import com.propertyvista.server.common.security.VistaPasswordResetServiceImpl;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class PtPasswordResetServiceImpl extends VistaPasswordResetServiceImpl<CustomerUserCredential> implements PtPasswordResetService {

    public PtPasswordResetServiceImpl() {
        super(CustomerUserCredential.class);
    }

    @Override
    protected AuthenticationResponse authenticate(CustomerUserCredential credentials) {
        return new PtAuthenticationServiceImpl().authenticate(credentials);
    }

}
