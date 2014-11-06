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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.Behavior;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.ResidentPortalPolicy;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentAuthenticationService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.shared.exceptions.LoginTokenExpiredUserRuntimeException;

public class ResidentAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<CustomerUser, ResidentUserVisit, CustomerUserCredential> implements
        ResidentAuthenticationService {

    private final static Logger log = LoggerFactory.getLogger(ResidentAuthenticationServiceImpl.class);

    private static final I18n i18n = I18n.get(ResidentAuthenticationServiceImpl.class);

    public ResidentAuthenticationServiceImpl() {
        super(CustomerUser.class, ResidentUserVisit.class, CustomerUserCredential.class);
    }

    @Override
    protected ResidentUserVisit createUserVisit(CustomerUser user) {
        ResidentUserVisit visit = new ResidentUserVisit(getVistaApplication(), user.<CustomerUser> duplicate());
        Persistence.ensureRetrieve(user.preferences(), AttachLevel.Attached);
        visit.setPreferences(user.preferences().<CustomerPreferences> detach());
        return visit;
    }

    @Override
    protected VistaApplication getVistaApplication() {
        return VistaApplication.resident;
    }

    @Override
    protected VistaAccessGrantedBehavior getApplicationAccessGrantedBehavior() {
        return VistaAccessGrantedBehavior.ResidentPortal;
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
    public String beginApplicationSession(ResidentUserVisit visit, CustomerUserCredential credentials, Set<Behavior> behaviors, IEntity additionalConditions) {
        // No Leases? Create a proper error messages
        //TODO Find a more obvious if() condition
        if ((visit.getLeaseId() == null) && !behaviors.contains(PortalResidentBehavior.LeaseSelectionRequired)) {
            if (!ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(visit.getCurrentUser()).isEmpty()) {
                // active prospect - need a nice message with target url
                String url = VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true);
                throw new UserRuntimeException(i18n.tr("User Account not activated yet. Please use the following URL to log in to your Application:\n{0}", url));
            } else if (ApplicationMode.isDevelopment()) {
                throw new Error("Lease not found for user " + visit.getCurrentUser().getDebugExceptionInfoString());
            } else {
                log.warn("Invalid log-in attempt {} : no active lease or app found: ", visit.getEmail());
                throw new UserRuntimeException(i18n.tr(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE));
            }
        }

        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), visit.getCurrentUser()));
        Customer customer = Persistence.service().retrieve(criteria);
        if (!customer.registeredInPortal().getValue(Boolean.FALSE)) {
            customer.registeredInPortal().setValue(Boolean.TRUE);
            Persistence.service().persist(customer);
            Persistence.service().commit();
        }
        return super.beginApplicationSession(visit, credentials, behaviors, additionalConditions);
    }

    @Override
    public Set<Behavior> getBehaviors(CustomerUserCredential credentials, ResidentUserVisit visit) {
        Set<Behavior> behaviors = new HashSet<Behavior>();

        CustomerUser user = visit.getCurrentUser();
        Lease selectedLeaseId = visit.getLeaseId();

        List<Lease> leases = ServerSideFactory.create(CustomerFacade.class).getActiveLeasesId(user);
        if ((selectedLeaseId != null) && !leases.contains(selectedLeaseId)) {
            selectedLeaseId = null;
        } else if (leases.size() == 1) {
            // Auto Select first Lease, But do not auto switch to this lease if such condition will occur
            selectedLeaseId = leases.get(0);
        } else {
            Collection<Lease> activeLeases = CollectionUtils.select(leases, new Predicate<Lease>() {
                @Override
                public boolean evaluate(Lease lease) {
                    Persistence.ensureRetrieve(lease, AttachLevel.Attached);
                    return lease.status().getValue().isActive();
                }
            });
            if (activeLeases.size() == 1) {
                selectedLeaseId = activeLeases.iterator().next();
            }
        }

        ResidentPortalContext.setLease(visit, selectedLeaseId);

        if (selectedLeaseId != null) {
            Collection<PortalResidentBehavior> leaseBehaviors = ServerSideFactory.create(CustomerFacade.class).getLeaseBehavior(user, selectedLeaseId);
            if (leaseBehaviors.size() > 0) {
                behaviors.add(getApplicationAccessGrantedBehavior());
                behaviors.addAll(leaseBehaviors);
                if (leases.size() > 1) {
                    behaviors.add(PortalResidentBehavior.HasMultipleLeases);
                }

                ResidentPortalPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainHierarchicalEffectivePolicy(selectedLeaseId,
                        ResidentPortalPolicy.class);
                if (policy.communicationEnabled().getValue(false)) {
                    behaviors.add(PortalResidentBehavior.CommunicationCreateMessages);
                }
                // TODO Make it properly in PaymentFacade
                {
                    EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                    criteria.eq(criteria.proto().lease(), selectedLeaseId);
                    BillingAccount billingAccount = Persistence.service().retrieve(criteria);
                    Collection<PaymentType> allowedPaymentTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(billingAccount,
                            PaymentMethodTarget.TODO, VistaApplication.resident);
                    for (PaymentType paymentType : allowedPaymentTypes) {
                        switch (paymentType) {
                        case CreditCard:
                            behaviors.add(VistaCustomerPaymentTypeBehavior.CreditCardPaymentsAllowed);
                            break;
                        case Echeck:
                            behaviors.add(VistaCustomerPaymentTypeBehavior.EcheckPaymentsAllowed);
                            break;
                        case DirectBanking:
                            behaviors.add(VistaCustomerPaymentTypeBehavior.DirectBankingPaymentsAllowed);
                            break;
                        case Interac:
                            behaviors.add(VistaCustomerPaymentTypeBehavior.InteracPaymentsAllowed);
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
        } else if (leases.size() > 0) {
            behaviors.add(PortalResidentBehavior.LeaseSelectionRequired);
        }
        // check if terms have been signed
        if (ServerSideFactory.create(CustomerFacade.class).hasToAcceptTerms(user)) {
            behaviors.add(VistaBasicBehavior.VistaTermsAcceptanceRequired);
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
        // See if active Lease exists
        if (ServerSideFactory.create(CustomerFacade.class).getActiveLeasesId(user).size() > 0) {
            ServerSideFactory.create(CommunicationFacade.class).sendTenantPasswordRetrievalToken(customer);
        } else if (ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(user).size() > 0) {
            ServerSideFactory.create(CommunicationFacade.class).sendProspectPasswordRetrievalToken(customer);
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
                    i18n.tr("You have been logged out of your account for security reasons.\nTo continue you must sign in with your email and your newly generated password.\nPressing the OK button below will redirect you to the login page"));
        }
    }

}
