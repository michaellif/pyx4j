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

import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.field.rpc.services.FieldAuthenticationService;
import com.propertyvista.server.common.security.CrmUserBuildingDatasetAccessBuilder;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class FieldAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CrmUser, CrmUserCredential> implements FieldAuthenticationService {

    public FieldAuthenticationServiceImpl() {
        super(CrmUser.class, CrmUserCredential.class);
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
        return VistaBasicBehavior.CRM;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.FieldPasswordChangeRequired;
    }

    @Override
    protected void sendPasswordRetrievalToken(CrmUser user) {
//        ServerSideFactory.create(CommunicationFacade.class).sendAdminPasswordRetrievalToken(user);
//        Persistence.service().commit();
    }

    @Override
    protected Set<Behavior> getBehaviors(CrmUserCredential userCredential) {
        Set<Behavior> behaviors = ServerSideFactory.create(UserManagementFacade.class).getBehaviors(userCredential);
        if (!userCredential.accessAllBuildings().isBooleanTrue()) {
            CrmUserBuildingDatasetAccessBuilder.updateAccessList(userCredential.user());
        }
        return behaviors;
    }

}
