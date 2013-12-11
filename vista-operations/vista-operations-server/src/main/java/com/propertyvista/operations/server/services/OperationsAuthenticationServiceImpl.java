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
package com.propertyvista.operations.server.services;

import java.util.HashSet;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.rpc.OperationsUserVisit;
import com.propertyvista.operations.rpc.services.OperationsAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.shared.VistaUserVisit;

public class OperationsAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<OperationsUser, OperationsUserCredential> implements
        OperationsAuthenticationService {

    public OperationsAuthenticationServiceImpl() {
        super(OperationsUser.class, OperationsUserCredential.class);
    }

    @Override
    protected boolean honorSystemState() {
        return false;
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.operations;
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.Operations;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.OperationsPasswordChangeRequired;
    }

    @Override
    protected VistaUserVisit<OperationsUser> createUserVisit(OperationsUser user) {
        return new OperationsUserVisit(getVistaApplication(), user);
    }

    @Override
    protected Set<Behavior> getBehaviors(OperationsUserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(userCredential.behaviors());
        return behaviors;
    }

    @Override
    protected void sendPasswordRetrievalToken(OperationsUser user) {
        ServerSideFactory.create(CommunicationFacade.class).sendOperationsPasswordRetrievalToken(user);
        Persistence.service().commit();
    }

}
