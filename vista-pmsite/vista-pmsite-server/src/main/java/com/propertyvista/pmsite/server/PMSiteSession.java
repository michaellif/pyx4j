/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;

public class PMSiteSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(PMSiteSession.class);

    public PMSiteSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(final String username, final String password) {
        AuthenticationRequest request = EntityFactory.create(AuthenticationRequest.class);
        request.email().setValue(username);
        request.password().setValue(password);

        try {
            VistaAuthenticationServicesImpl.beginSession(request);
            return true;
        } catch (Throwable e) {
            // TODO What to do with Error messages ?
            log.error("Error", e);
            return false;
        }
    }

    @Override
    public void signOut() {
        Lifecycle.endSession();
        super.signOut();
    }

    @Override
    public Roles getRoles() {
        if (isSignedIn()) {
            return new Roles(Roles.USER);
        }
        return null;
    }

}
