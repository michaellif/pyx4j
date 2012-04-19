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
package com.propertyvista.portal.server.portal.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.server.security.VistaCustomerContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class PortalAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CustomerUser, CustomerUserCredential> implements
        PortalAuthenticationService {

    private static final I18n i18n = I18n.get(PortalAuthenticationServiceImpl.class);

    public PortalAuthenticationServiceImpl() {
        super(CustomerUser.class, CustomerUserCredential.class);
    }

    @Override
    protected boolean isDynamicBehaviours() {
        return true;
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.TenantPortal;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.TenantPortalPasswordChangeRequired;
    }

    @Override
    protected boolean isSessionValid() {
        return SecurityController.checkAnyBehavior(getApplicationBehavior(), getPasswordChangeRequiredBehavior(), VistaCustomerBehavior.LeaseSelectionRequired);
    }

    @Override
    public String beginSession(CustomerUser user, Set<Behavior> behaviors) {
        Set<Behavior> actualBehaviors = new HashSet<Behavior>();
        Lease selectedLease = null;

        // See if active Lease exists
        List<Lease> leases = ServerSideFactory.create(CustomerFacade.class).getActiveLeases(user);
        if (leases.size() == 0) {
            if (ApplicationMode.isDevelopment()) {
                throw new Error("Application not found for user" + user.getDebugExceptionInfoString());
            } else {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
        } else if (leases.size() == 1) {
            selectedLease = leases.get(0);
            actualBehaviors.add(ServerSideFactory.create(CustomerFacade.class).getLeaseBehavior(user, selectedLease));
            actualBehaviors.addAll(behaviors);
        } else {
            actualBehaviors.add(VistaCustomerBehavior.LeaseSelectionRequired);
        }

        String sessionToken = super.beginSession(user, actualBehaviors);

        if (selectedLease != null) {
            VistaCustomerContext.setCurrentUserLease(selectedLease);
        }
        return sessionToken;
    }

    @Override
    protected void sendPasswordRetrievalToken(CustomerUser user) {
        {
            // See if active Lease exists
            List<Lease> leases = ServerSideFactory.create(CustomerFacade.class).getActiveLeases(user);
            if (leases.size() == 0) {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
        }

        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
            Customer customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
            ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(customer);
        }

    }
}
