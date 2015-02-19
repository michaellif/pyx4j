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
 */
package com.propertyvista.portal.server.portal.prospect.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.config.VistaSystemMaintenance;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.ProspectUserVisit;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectAuthenticationService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.shared.exceptions.LoginTokenExpiredUserRuntimeException;

public class ProspectAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CustomerUser, ProspectUserVisit, CustomerUserCredential> implements
        ProspectAuthenticationService {

    private static final I18n i18n = I18n.get(ProspectAuthenticationServiceImpl.class);

    public ProspectAuthenticationServiceImpl() {
        super(CustomerUser.class, ProspectUserVisit.class, CustomerUserCredential.class);
    }

    @Override
    protected ProspectUserVisit createUserVisit(CustomerUser user) {
        return new ProspectUserVisit(getVistaApplication(), user);
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.prospect;
    }

    @Override
    protected VistaAccessGrantedBehavior getApplicationAccessGrantedBehavior() {
        return VistaAccessGrantedBehavior.ProspectPortal;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.ProspectPortalPasswordChangeRequired;
    }

    @Override
    protected boolean applicationLoginDisabled() {
        return VistaSystemMaintenance.getApplicationsState().tenantsLoginDisabled().getValue();
    }

    @Override
    protected Collection<Behavior> getAccountSetupRequiredBehaviors() {
        return Arrays.asList(new Behavior[] { getPasswordChangeRequiredBehavior(), PortalProspectBehavior.ApplicationSelectionRequired });
    }

    @Override
    public Set<Behavior> getBehaviors(CustomerUserCredential credentials, ProspectUserVisit visit) {
        Set<Behavior> behaviors = new HashSet<Behavior>();

        OnlineApplication selectedApplicationId = visit.getOnlineApplicationId();

        // See if active Application exists
        List<OnlineApplication> applications = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(visit.getCurrentUser());
        if ((selectedApplicationId != null) && !applications.contains(selectedApplicationId)) {
            selectedApplicationId = null;
        } else if (applications.size() == 1) {
            // Auto Select first, But do not auto switch to this lease if such condition will occur
            selectedApplicationId = applications.get(0);
        }
        ProspectPortalContext.setOnlineApplication(visit, selectedApplicationId);

        if (selectedApplicationId != null) {
            Collection<PortalProspectBehavior> applicationBehaviors = ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplicationBehavior(
                    selectedApplicationId);
            if (!applicationBehaviors.isEmpty()) {
                behaviors.add(getApplicationAccessGrantedBehavior());
                behaviors.addAll(applicationBehaviors);
                if (applications.size() > 1) {
                    behaviors.add(PortalProspectBehavior.HasMultipleApplications);
                }
            }

        } else if (applications.size() > 0) {
            behaviors.add(PortalProspectBehavior.ApplicationSelectionRequired);
        }

        return behaviors;
    }

    @Override
    protected void sendPasswordRetrievalToken(CustomerUser user) {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        Customer customer = Persistence.service().retrieve(criteria);
        if (customer == null) {
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }
        // See if Application exists
        if (ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(user).size() > 0) {
            ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(customer);
        } else if (ServerSideFactory.create(CustomerFacade.class).getActiveLeasesId(user).size() > 0) {
            ServerSideFactory.create(CommunicationFacade.class).sendTenantPasswordRetrievalToken(customer);
        } else {
            throw new UserRuntimeException(
                    i18n.tr("This account has been deactivated or moved to a new web address. Please contact your landlord for more information."));
        }
        Persistence.service().commit();
    }

    @Override
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken) {
        try {
            super.authenticateWithToken(callback, clientSystemInfo, accessToken);
        } catch (LoginTokenExpiredUserRuntimeException e) {
            throw new LoginTokenExpiredUserRuntimeException(
                    i18n.tr("You have been logged out of your account for security reasons.\nTo continue/complete your application you must sign in with your email and your newly generated password.\nPressing the OK button below will redirect you to the login page"));
        }
    }

}
