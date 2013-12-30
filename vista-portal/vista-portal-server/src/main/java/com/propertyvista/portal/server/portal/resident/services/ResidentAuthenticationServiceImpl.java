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
package com.propertyvista.portal.server.portal.resident.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.CustomerUserCredential;
import com.propertyvista.shared.VistaUserVisit;

public class ResidentAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CustomerUser, CustomerUserCredential> implements
        ResidentAuthenticationService {

    private static final I18n i18n = I18n.get(ResidentAuthenticationServiceImpl.class);

    public ResidentAuthenticationServiceImpl() {
        super(CustomerUser.class, CustomerUserCredential.class);
    }

    @Override
    protected boolean isDynamicBehaviours() {
        return true;
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.resident;
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.ResidentPortal;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.ResidentPortalPasswordChangeRequired;
    }

    @Override
    protected Collection<Behavior> getAccountSetupRequiredBehaviors() {
        return Arrays.asList(new Behavior[] { getPasswordChangeRequiredBehavior(), PortalResidentBehavior.LeaseSelectionRequired });
    }

    @Override
    protected VistaUserVisit<CustomerUser> createUserVisit(CustomerUser user) {
        return new ResidentUserVisit(getVistaApplication(), user);
    }

    @Override
    public String beginSession(CustomerUser user, CustomerUserCredential credentials, Set<Behavior> behaviors, IEntity additionalConditions) {
        Set<Behavior> actualBehaviors = new HashSet<Behavior>();
        actualBehaviors.add(getVistaApplication());

        // See if active Lease exists
        List<Lease> leases = ServerSideFactory.create(CustomerFacade.class).getActiveLeases(user);
        Lease selectedLease = null;
        if ((additionalConditions instanceof Lease) && (leases.contains(additionalConditions))) {
            selectedLease = leases.get(leases.indexOf(additionalConditions));
        } else if (leases.size() == 1) {
            selectedLease = leases.get(0);
        }

        if (leases.size() == 0) {
            if (ApplicationMode.isDevelopment()) {
                throw new Error("Lease not found for user " + user.getDebugExceptionInfoString());
            } else {
                throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
            }
        } else if (selectedLease != null) {
            actualBehaviors.add(ServerSideFactory.create(CustomerFacade.class).getLeaseBehavior(user, selectedLease));
            actualBehaviors.addAll(behaviors);
            if (leases.size() > 1) {
                actualBehaviors.add(PortalResidentBehavior.HasMultipleLeases);
            }
        } else {
            actualBehaviors.add(PortalResidentBehavior.LeaseSelectionRequired);
        }

        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        Customer customer = Persistence.service().retrieve(criteria);
        if (!customer.registeredInPortal().getValue(Boolean.FALSE)) {
            customer.registeredInPortal().setValue(Boolean.TRUE);
            Persistence.service().persist(customer);
            Persistence.service().commit();
        }

        // check if terms have been signed
        if (ServerSideFactory.create(CustomerFacade.class).hasToAcceptTerms(user)) {
            actualBehaviors.add(VistaBasicBehavior.VistaTermsAcceptanceRequired);
        }

        if (true) {
            actualBehaviors.add(PortalResidentBehavior.LeaseSigningRequired);
        }

        if (selectedLease != null) {
            Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(selectedLease.billingAccount(),
                    VistaApplication.resident);
            for (PaymentType paymentType : allowedPaymentTypes) {
                switch (paymentType) {
                case CreditCard:
                    actualBehaviors.add(VistaCustomerPaymentTypeBehavior.CreditCardPaymentsAllowed);
                    break;
                case Echeck:
                    actualBehaviors.add(VistaCustomerPaymentTypeBehavior.EcheckPaymentsAllowed);
                    break;
                case DirectBanking:
                    actualBehaviors.add(VistaCustomerPaymentTypeBehavior.DirectBankingPaymentsAllowed);
                    break;
                case Interac:
                    actualBehaviors.add(VistaCustomerPaymentTypeBehavior.InteracPaymentsAllowed);
                    break;
                default:
                    break;
                }
            }
        }

        String sessionToken = super.beginSession(user, credentials, actualBehaviors, additionalConditions);

        if (selectedLease != null) {
            ResidentPortalContext.setLease(selectedLease);
        }
        return sessionToken;
    }

    @Override
    protected void sendPasswordRetrievalToken(CustomerUser user) {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        Customer customer = Persistence.service().retrieve(criteria);
        if (customer == null) {
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }
        // See if active Lease exists
        List<Lease> leases = ServerSideFactory.create(CustomerFacade.class).getActiveLeases(user);
        if (leases.size() > 0) {
            ServerSideFactory.create(CommunicationFacade.class).sendTenantPasswordRetrievalToken(customer);
        } else {
            ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(customer);
        }
        Persistence.service().commit();
    }

}
