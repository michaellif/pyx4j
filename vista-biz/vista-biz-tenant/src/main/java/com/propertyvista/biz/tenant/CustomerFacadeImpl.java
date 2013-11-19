/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerAcceptedTerms;
import com.propertyvista.domain.tenant.CustomerSelfRegistration;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.operations.domain.legal.VistaTerms.VistaTermsV;
import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CustomerUserCredential;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.config.VistaDemo;

public class CustomerFacadeImpl implements CustomerFacade {

    private static Logger log = LoggerFactory.getLogger(CustomerFacadeImpl.class);

    private static final I18n i18n = I18n.get(CustomerFacadeImpl.class);

    @Override
    public void persistCustomer(Customer customer) {
        if (customer.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(customer);
        }
        if (!customer.user().isNull() && customer.person().email().isNull()) {
            throw new UnRecoverableRuntimeException(i18n.tr("Can''t remove e-mail address for {0} ", customer.person().name().getStringView()));
        }
        if ((!customer.person().email().isNull()) || (customer.user().getPrimaryKey() != null)) {
            customer.person().email().setValue(EmailValidator.normalizeEmailAddress(customer.person().email().getValue()));
            Persistence.service().retrieve(customer.user());
            customer.user().name().setValue(customer.person().name().getStringView());
            customer.user().email().setValue(customer.person().email().getValue());
            if (customer.user().getPrimaryKey() != null) {
                Persistence.service().merge(customer.user());
            } else {
                Persistence.service().persist(customer.user());

                CustomerUserCredential credential = EntityFactory.create(CustomerUserCredential.class);
                credential.setPrimaryKey(customer.user().getPrimaryKey());
                credential.user().set(customer.user());
                if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
                    credential.credential().setValue(
                            ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(customer.user().email().getValue()));
                }
                credential.enabled().setValue(Boolean.TRUE);
                Persistence.service().persist(credential);
            }
        }

        if (customer.portalRegistrationToken().isNull() && !customer.registeredInPortal().getValue(false)) {
            customer.portalRegistrationToken().setValue(AccessKey.createPortalSecureToken());
        }

        Persistence.service().merge(customer);
    }

    @Override
    public List<Lease> getActiveLeases(CustomerUser customerUser) {
        List<Lease> leases = new ArrayList<Lease>();

        Customer customer = findCustomer(customerUser);
        if (customer != null) {
            {
                EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                criteria.in(criteria.proto().status(), Lease.Status.current());
                criteria.in(criteria.proto().unit().building().suspended(), false);
                criteria.eq(criteria.proto().currentTerm().version().tenants().$().leaseParticipant().customer(), customer);
                criteria.in(criteria.proto().currentTerm().version().tenants().$().role(), LeaseTermParticipant.Role.portalAccess());
                leases.addAll(Persistence.service().query(criteria));
            }
            // TODO guarantors portal not supported for now
            if (false) {
                EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                criteria.in(criteria.proto().status(), Lease.Status.current());
                criteria.eq(criteria.proto().currentTerm().version().guarantors().$().leaseParticipant().customer(), customer);
                leases.addAll(Persistence.service().query(criteria));
            }
        }

        return leases;
    }

    @Override
    public boolean hasToAcceptTerms(CustomerUser customerUser) {
        Customer customer = findCustomer(customerUser);
        if (customer == null) {
            return true;
        }

        final CustomerAcceptedTerms acceptedTerms;
        {
            EntityQueryCriteria<CustomerAcceptedTerms> criteria = EntityQueryCriteria.create(CustomerAcceptedTerms.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
            acceptedTerms = Persistence.service().retrieve(criteria);
            if (acceptedTerms == null || acceptedTerms.vistaTerms().isNull()) {
                return true;
            }
        }
        Key versionKey = TaskRunner.runInOperationsNamespace(new Callable<Key>() {
            @Override
            public Key call() {
                VistaTermsV vistaTermsV = Persistence.service().retrieve(VistaTermsV.class, acceptedTerms.vistaTerms().getValue());
                if (vistaTermsV == null) {
                    return null;
                } else {
                    return vistaTermsV.holder().version().getPrimaryKey();
                }
            }
        });
        return !versionKey.equals(acceptedTerms.vistaTerms().getValue());
    }

    @Override
    public void onVistaTermsAccepted(Key customerUserKey, Key vistaTermsKey, boolean accepted) {
        Customer customer = null;
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            CustomerUser user = EntityFactory.create(CustomerUser.class);
            user.setPrimaryKey(customerUserKey);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
            customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                return;
            }
        }

        CustomerAcceptedTerms acceptedTerms = null;
        {
            EntityQueryCriteria<CustomerAcceptedTerms> criteria = EntityQueryCriteria.create(CustomerAcceptedTerms.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
            acceptedTerms = Persistence.service().retrieve(criteria);
            if (acceptedTerms == null) {
                acceptedTerms = EntityFactory.create(CustomerAcceptedTerms.class);
                acceptedTerms.customer().set(customer);
            }
        }

        if (accepted) {
            acceptedTerms.vistaTerms().setValue(vistaTermsKey);
            Persistence.service().persist(acceptedTerms);
            Persistence.service().commit();
        }
    }

    @Override
    public VistaCustomerBehavior getLeaseBehavior(CustomerUser customerUser, Lease lease) {
        // TODO implement TenantSecondary and Guarantor
        return VistaCustomerBehavior.TenantPrimary;
    }

    @Override
    public void selfRegistration(CustomerSelfRegistration selfRegistration) {
        // We need protection from attacks that check names of the user?
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease().unit().building(), selfRegistration.buildingId());
        criteria.eq(criteria.proto().lease().unit().building().suspended(), false);
        criteria.eq(criteria.proto().customer().portalRegistrationToken(), selfRegistration.securityCode().getValue().toUpperCase(Locale.ENGLISH));

        List<Tenant> tenants = Persistence.service().query(criteria);

        if (tenants.size() == 0) {
            throw EntityValidationException.make(SelfRegistrationDTO.class)
                    .addError(selfRegistration.securityCode(), i18n.tr("The Security Code was incorrect")).build();
        }

        Tenant tenant = null;
        for (Tenant t : tenants) {
            if (CustomerRegistrationNameMatching.nameMatch(t.customer().person().name(), selfRegistration)) {
                tenant = t;
                break;
            }
        }
        if (tenant == null) {
            throw EntityValidationException.make(SelfRegistrationDTO.class)//@formatter:off
                    .addError(selfRegistration.firstName(), i18n.tr("The name provided does not match our records"))
                    .addError(selfRegistration.middleName(), i18n.tr("The name provided does not match our records"))
                    .addError(selfRegistration.lastName(), i18n.tr("The name provided does not match our records"))
                    .build();//@formatter:on
        }

        tenant.customer().person().email().setValue(selfRegistration.email().getValue());
        tenant.customer().portalRegistrationToken().setValue(null);
        try {
            persistCustomer(tenant.customer());
        } catch (UniqueConstraintUserRuntimeException e) {
            throw EntityValidationException.make(SelfRegistrationDTO.class)//@formatter:off
                    .addError(selfRegistration.email(), i18n.tr("Your Email already registered, Contact Property Owner"))
                    .build();//@formatter:on            
        }

        CustomerUserCredential credential = Persistence.service().retrieve(CustomerUserCredential.class, tenant.customer().user().getPrimaryKey());
        credential.accessKey().setValue(null);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(selfRegistration.password().getValue()));

        credential.passwordUpdated().setValue(new Date());
        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);

        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credential.user());

        Persistence.service().persist(credential);
        log.info("tenant {} {} registered for tenant portal", selfRegistration.firstName(), selfRegistration.lastName());

    }

    @Override
    public List<OnlineApplication> getActiveOnlineApplications(CustomerUser customerUser) {
        List<OnlineApplication> applications = new ArrayList<OnlineApplication>();

        Customer customer = findCustomer(customerUser);
        if (customer != null) {
            EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
            criteria.in(criteria.proto().status(), Lease.Status.current());
            criteria.in(criteria.proto().customer(), customer);
            applications.addAll(Persistence.service().query(criteria));
        }

        return applications;
    }

    // internals:

    private Customer findCustomer(CustomerUser customerUser) {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.eq(criteria.proto().user(), customerUser);
        return Persistence.service().retrieve(criteria);
    }
}
