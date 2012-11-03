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
import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

import com.propertyvista.admin.domain.legal.VistaTerms.VistaTermsV;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerAcceptedTerms;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.CustomerUserCredential;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.config.VistaDemo;

public class CustomerFacadeImpl implements CustomerFacade {

    private static final I18n i18n = I18n.get(CustomerFacadeImpl.class);

    @Override
    public void persistCustomer(Customer customer) {
        if (customer.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(customer);
        }
        if (!customer.user().isNull() && customer.person().email().isNull()) {
            throw new UnRecoverableRuntimeException(i18n.tr("Can't remove e-mail address for {0} ", customer.person().name().getStringView()));
        }
        if (!customer.person().email().isNull()) {
            Persistence.service().retrieve(customer.user());
            customer.user().name().setValue(customer.person().name().getStringView());
            customer.user().email().setValue(PasswordEncryptor.normalizeEmailAddress(customer.person().email().getValue()));
            if (customer.user().getPrimaryKey() != null) {
                Persistence.service().merge(customer.user());
            } else {
                Persistence.service().persist(customer.user());

                CustomerUserCredential credential = EntityFactory.create(CustomerUserCredential.class);
                credential.setPrimaryKey(customer.user().getPrimaryKey());
                credential.user().set(customer.user());
                if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
                    credential.credential().setValue(PasswordEncryptor.encryptPassword(customer.user().email().getValue()));
                }
                credential.enabled().setValue(Boolean.TRUE);
                Persistence.service().persist(credential);
            }
        }

        Persistence.service().merge(customer);
    }

    @Override
    public List<Lease> getActiveLeases(CustomerUser customerUser) {
        Customer customer;
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), customerUser));
            customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                return null;
            }
        }

        List<Lease> leases = new ArrayList<Lease>();
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            criteria.add(PropertyCriterion.eq(criteria.proto().currentTerm().version().tenants().$().leaseParticipant().customer(), customer));
            leases.addAll(Persistence.service().query(criteria));
        }
        // TODO guarantors portal not supported for now
        if (false) {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            criteria.add(PropertyCriterion.eq(criteria.proto().currentTerm().version().guarantors().$().leaseParticipant().customer(), customer));
            leases.addAll(Persistence.service().query(criteria));
        }
        return leases;
    }

    @Override
    public boolean hasToAcceptTerms(CustomerUser customerUser) {
        Customer customer;
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), customerUser));
            customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                return true;
            }
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
        Key versionKey = TaskRunner.runInAdminNamespace(new Callable<Key>() {
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
}
