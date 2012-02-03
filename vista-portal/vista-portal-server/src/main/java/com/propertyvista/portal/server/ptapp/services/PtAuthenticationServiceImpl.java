/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.util.Set;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class PtAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<TenantUser, TenantUserCredential> implements PtAuthenticationService {

    public PtAuthenticationServiceImpl() {
        super(TenantUser.class, TenantUserCredential.class);
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.ProspectiveApp;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.ProspectiveAppPasswordChangeRequired;
    }

    @Override
    protected void addBehaviors(TenantUserCredential userCredential, Set<Behavior> behaviors) {
        behaviors.addAll(userCredential.behaviors());

    }

    @Override
    public String beginSession(AbstractUser user, TenantUserCredential userCredential) {
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), userCredential.user()));
        Application application = Persistence.service().retrieve(criteria);
        if (application == null) {
            throw new Error("Application not found for user" + userCredential.user().getDebugExceptionInfoString());
        }
        String sessionToken = super.beginSession(user, userCredential);
        // set application in context here:
        PtAppContext.setCurrentUserApplication(application);
        return sessionToken;
    }

}
