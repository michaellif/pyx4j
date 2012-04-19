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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class PtAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CustomerUser, CustomerUserCredential> implements PtAuthenticationService {

    private static final I18n i18n = I18n.get(PtAuthenticationServiceImpl.class);

    public PtAuthenticationServiceImpl() {
        super(CustomerUser.class, CustomerUserCredential.class);
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
    protected boolean isDynamicBehaviours() {
        return true;
    }

    @Override
    protected boolean isSessionValid() {
        return SecurityController.checkAnyBehavior(getApplicationBehavior(), getPasswordChangeRequiredBehavior(),
                VistaCustomerBehavior.ApplicationSelectionRequired);
    }

    @Override
    public String beginSession(CustomerUser user, Set<Behavior> behaviors) {
        Set<Behavior> actualBehaviors = new HashSet<Behavior>();
        OnlineApplication selectedApplication = null;

        // See if active Application exists
        List<OnlineApplication> applications = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(user);
        if (applications.size() == 0) {
            if (ApplicationMode.isDevelopment()) {
                throw new Error("Application not found for user" + user.getStringView());
            } else {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
        } else if (applications.size() == 1) {
            selectedApplication = applications.get(0);
            VistaCustomerBehavior behavior = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationBehavior(selectedApplication);
            if (behavior == null) {
                if (ApplicationMode.isDevelopment()) {
                    throw new Error("User Not Authorized to access application, " + user.getStringView());
                } else {
                    throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
                }
            }
            actualBehaviors.add(behavior);
            actualBehaviors.addAll(behaviors);
        } else {
            actualBehaviors.add(VistaCustomerBehavior.ApplicationSelectionRequired);
        }

        String sessionToken = super.beginSession(user, actualBehaviors);

        // set application in context here:
        if (selectedApplication != null) {
            PtAppContext.setCurrentUserApplication(selectedApplication);
            MasterOnlineApplication masterOnlineApplication = Persistence.service().retrieve(MasterOnlineApplication.class,
                    selectedApplication.masterOnlineApplication().getPrimaryKey());
            PtAppContext.setCurrentUserLease(masterOnlineApplication.leaseApplication().lease());
        }

        return sessionToken;
    }

    @Override
    protected void sendPasswordRetrievalToken(CustomerUser user) {

        // See if active Application exists
        List<OnlineApplication> applications = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(user);
        if (applications.size() == 0) {
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }

        ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(applications.get(0).customer());
    }

}
