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
package com.propertyvista.crm.server.services.pub;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.crm.rpc.services.pub.CrmAuthenticationService;
import com.propertyvista.crm.server.security.BuildingDatasetAccessBuilder;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CrmUser, CrmUserCredential> implements CrmAuthenticationService {

    public CrmAuthenticationServiceImpl() {
        super(CrmUser.class, CrmUserCredential.class);
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.CRM;
    }

    @Override
    protected void addBehaviors(CrmUserCredential userCredential, Set<Behavior> behaviors) {

        //TODO remove when switched to roles
        behaviors.addAll(userCredential.behaviors());

        addAllBehaviors(behaviors, userCredential.roles(), new HashSet<CrmRole>());

        if (userCredential.accessAllBuildings().isBooleanTrue()) {
            behaviors.add(VistaDataAccessBehavior.BuildingsAll);
        } else {
            behaviors.add(VistaDataAccessBehavior.BuildingsAssigned);
            BuildingDatasetAccessBuilder.updateAccessList(userCredential.user());
        }

    }

    private void addAllBehaviors(Set<Behavior> behaviors, Collection<CrmRole> roles, Set<CrmRole> processed) {
        for (CrmRole role : roles) {
            if (!processed.contains(role)) {
                Persistence.service().retrieve(role);
                processed.add(role);
                behaviors.addAll(role.behaviors());
                addAllBehaviors(behaviors, role.roles(), processed);
            }
        }
    }

    @Override
    public void requestPasswordReset(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request) {

        // TODO implement this method

        if (request.email().getValue().startsWith("jerry")) {
            callback.onSuccess(new VoidSerializable());
        } else if (request.email().getValue().startsWith("george")) {
            callback.onFailure(new Error("no soup for you!"));
        } else {

        }
    }

    @Override
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken) {
        if ("jerry".equals(accessToken)) {
            callback.onSuccess(new AuthenticationResponse());
        } else if ("george".equals(accessToken)) {
            callback.onFailure(new Error("no soup for you!"));
        } else {

        }
    }
}
