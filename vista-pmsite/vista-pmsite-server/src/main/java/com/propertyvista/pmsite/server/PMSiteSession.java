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
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.portal.server.portal.services.PortalAuthenticationServiceImpl;

public class PMSiteSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(PMSiteSession.class);

    public static final String PasswordChangeRequiredRole = "PWDCHANGE";

    public PMSiteSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(final String username, final String password) {
        return true;
    }

    @Override
    public void signOut() {
        Lifecycle.endSession();
        super.signOut();
    }

    @Override
    public Roles getRoles() {
        Roles roles = null;

        // try token authentication first
        StringValue token = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(AuthenticationService.AUTH_TOKEN_ARG);
        if (!token.isEmpty()) {
            try {
                new PortalAuthenticationServiceImpl().authenticateWithToken(new AsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        // success; start wicket session
                        signIn(null, null);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        // failure
                        log.info(caught.getMessage());
                    }
                }, null, token.toString());
            } catch (Exception e) {
                // failure
                log.info(e.getMessage());
            }
        }

        if (isSignedIn()) {
            roles = new Roles();
            if (Context.getVisit().getAcl().checkBehavior(VistaBasicBehavior.TenantPortalPasswordChangeRequired)) {
                roles.add(PasswordChangeRequiredRole);
            } else {
                roles.add(Roles.USER);
            }
        }
        return roles;
    }

}
