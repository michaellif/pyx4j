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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
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
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.CRMPasswordChangeRequired;
    }

    @Override
    protected Set<Behavior> getBehaviors(CrmUserCredential userCredential) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        addAllBehaviors(behaviors, userCredential.roles(), new HashSet<CrmRole>());

        if (userCredential.accessAllBuildings().isBooleanTrue()) {
            behaviors.add(VistaDataAccessBehavior.BuildingsAll);
        } else {
            behaviors.add(VistaDataAccessBehavior.BuildingsAssigned);
            BuildingDatasetAccessBuilder.updateAccessList(userCredential.user());
        }

        return behaviors;
    }

    private void addAllBehaviors(Set<Behavior> behaviors, Collection<CrmRole> roles, Set<CrmRole> processed) {
        for (CrmRole role : roles) {
            if (!processed.contains(role)) {
                Persistence.service().retrieve(role);
                processed.add(role);
                behaviors.addAll(role.behaviors());
                addAllBehaviors(behaviors, role.roles(), processed);
            }
            if (role.requireSecurityQuestionForPasswordReset().isBooleanTrue()) {
                behaviors.add(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion);
            }
        }
    }

    @Override
    protected void sendPasswordRetrievalToken(CrmUser user) {
        ServerSideFactory.create(CommunicationFacade.class).sendCrmPasswordRetrievalToken(user);
    }

}
