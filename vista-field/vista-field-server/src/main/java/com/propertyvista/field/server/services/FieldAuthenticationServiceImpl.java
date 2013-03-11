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
package com.propertyvista.field.server.services;

import com.pyx4j.security.shared.Behavior;

import com.propertyvista.domain.security.FieldUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.field.rpc.services.FieldAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.FieldUserCredential;

public class FieldAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<FieldUser, FieldUserCredential> implements FieldAuthenticationService {

    public FieldAuthenticationServiceImpl() {
        super(FieldUser.class, FieldUserCredential.class);
    }

    @Override
    protected boolean honorSystemState() {
        return false;
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.field;
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.Field;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.FieldPasswordChangeRequired;
    }

    @Override
    protected void sendPasswordRetrievalToken(FieldUser user) {
//        ServerSideFactory.create(CommunicationFacade.class).sendAdminPasswordRetrievalToken(user);
//        Persistence.service().commit();
    }

}
