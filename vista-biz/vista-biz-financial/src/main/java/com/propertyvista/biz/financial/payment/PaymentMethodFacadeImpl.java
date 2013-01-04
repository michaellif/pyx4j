/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PaymentMethodFacadeImpl implements PaymentMethodFacade {

    @Override
    public LeasePaymentMethod persistLeasePaymentMethod(Building building, LeasePaymentMethod paymentMethod) {
        return PaymentMethodPersister.persistLeasePaymentMethod(building, paymentMethod);
    }

    @Override
    public void deleteLeasePaymentMethod(LeasePaymentMethod paymentMethod) {
        Persistence.service().retrieve(paymentMethod);
        paymentMethod.isDeleted().setValue(Boolean.TRUE);
        paymentMethod.isOneTimePayment().setValue(Boolean.TRUE);
        Persistence.service().merge(paymentMethod);
    }

    @Override
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<?> participant) {
        assert !participant.leaseParticipant().customer().isValueDetached();
        return retrieveLeasePaymentMethods(participant.leaseParticipant().customer());
    }

    @Override
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(Customer customer) {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
        criteria.add(PropertyCriterion.eq(criteria.proto().isOneTimePayment(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        List<LeasePaymentMethod> methods = Persistence.service().query(criteria);
        return methods;
    }

    @Override
    public InsurancePaymentMethod retrieveInsurancePaymentMethod(Tenant tenantId) {
        EntityQueryCriteria<InsurancePaymentMethod> criteria = EntityQueryCriteria.create(InsurancePaymentMethod.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        Validate.isTrue(paymentMethod.tenant().equals(tenantId));
        Validate.isTrue(PaymentType.avalableInInsurance().contains(paymentMethod.type().getValue()));
        return PaymentMethodPersister.persistInsurancePaymentMethod(paymentMethod);
    }

    @Override
    public PmcPaymentMethod persistPmcPaymentMethod(CreditCardInfo creditCardInfo, Pmc pmc) {
        PmcPaymentMethod pmcPaymentMethod = EntityFactory.create(PmcPaymentMethod.class);
        pmcPaymentMethod.pmc().set(pmc);
        pmcPaymentMethod.details().set(creditCardInfo);
        pmcPaymentMethod.type().setValue(PaymentType.CreditCard);
        //TODO get MerchantTerminalId
        return PaymentMethodPersister.persistPaymentMethod(pmcPaymentMethod, null, new MerchantTerminalSourceVista());
    }

    @Override
    public PmcPaymentMethod persistPmcPaymentMethod(PmcPaymentMethod paymentMethod) {
        return PaymentMethodPersister.persistPmcPaymentMethod(paymentMethod);
    }
}
