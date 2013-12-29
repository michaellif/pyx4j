/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.Period;
import com.propertyvista.domain.financial.billingext.PaymentRecordExternal;
import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
import com.propertyvista.domain.financial.billingext.dto.PaymentDTO;
import com.propertyvista.domain.financial.billingext.dto.PaymentRecordDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class ExternalBillingFacadeImpl implements ExternalBillingFacade {
    @Override
    public boolean postCharge(ChargeDTO chargeDTO, final String leaseId) {
        BillingAccount billingAccount = getBillingAccount(leaseId);
        if (billingAccount == null) {
            return false;
        }

        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);
        charge.billingAccount().set(billingAccount);
        charge.amount().set(chargeDTO.amount());
        charge.taxTotal().setValue(new BigDecimal("0.00"));
        charge.description().set(chargeDTO.description());
        charge.fromDate().set(chargeDTO.fromDate());
        charge.toDate().set(chargeDTO.toDate());
        charge.period().setValue(Period.next);

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(billingAccount.lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);

        charge.billingCycle().set(nextCycle);
//        charge.dueDate().setValue(ARDateUtils.getBillingCycleDueDate(billingAccount, nextCycle));
        charge.postDate().setValue(now);

        Persistence.service().persist(charge);

        return true;
    }

    @Override
    public boolean postPayment(PaymentDTO paymentDTO, final String leaseId) {
        BillingAccount billingAccount = getBillingAccount(leaseId);
        if (billingAccount == null) {
            return false;
        }

        InvoicePayment payment = EntityFactory.create(InvoicePayment.class);
        payment.billingAccount().set(billingAccount);
        payment.paymentRecord().set(getPaymentRecord(paymentDTO.transactionId().getValue()));
        payment.amount().setValue(paymentDTO.amount().getValue().negate());
        payment.description().set(paymentDTO.description());

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(billingAccount.lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);

        payment.billingCycle().set(nextCycle);
        payment.postDate().setValue(now);

        Persistence.service().persist(payment);

        return true;
    }

    @Override
    public List<PaymentRecordDTO> getNonProcessedPaymentRecords() {
        EntityQueryCriteria<PaymentRecordExternal> criteria = EntityQueryCriteria.create(PaymentRecordExternal.class);
        criteria.add(PropertyCriterion.isNull(criteria.proto().externalTransactionId()));
        List<PaymentRecordExternal> result = Persistence.service().query(criteria);
        if (result == null || result.size() == 0) {
            return null;
        }

        List<PaymentRecordDTO> paymentRecords = new ArrayList<PaymentRecordDTO>();
        for (PaymentRecordExternal epr : result) {
            PaymentRecordDTO prDTO = EntityFactory.create(PaymentRecordDTO.class);
            prDTO.transactionId().setValue(epr.getPrimaryKey().toString());
            prDTO.leaseId().set(epr.paymentRecord().billingAccount().lease().leaseId());
            prDTO.amount().set(epr.paymentRecord().amount());
            prDTO.paymentType().setValue(epr.paymentRecord().paymentMethod().type().toString());
            prDTO.transactionDate().set(epr.paymentRecord().finalizeDate());
            paymentRecords.add(prDTO);
        }

        return paymentRecords;
    }

    @Override
    public void reconcilePaymentRecords(List<PaymentRecordDTO> records) {
        if (records == null || records.size() == 0) {
            return;
        }
        for (PaymentRecordDTO record : records) {
            PaymentRecordExternal epr = Persistence.service().retrieve(PaymentRecordExternal.class, new Key(record.transactionId().getValue()));
            if (epr != null && epr.externalTransactionId().isNull()) {
                epr.externalTransactionId().set(record.externalTransactionId());
            }
        }
    }

    @Override
    public Bill runBilling(Lease lease) {
        return ExternalBillProducer.produceBill(lease);
    }

    @Override
    public void runBilling(String propertyCode) {
        Building bld = getBuilding(propertyCode);
        if (bld == null) {
            return;
        }

        for (Lease lease : getActiveLeases(bld)) {
            Bill bill = runBilling(lease);
        }
    }

    @Override
    public void onPaymentRecordCreated(PaymentRecord paymentRecord) {
        PaymentRecordExternal epr = EntityFactory.create(PaymentRecordExternal.class);
        epr.billingAccount().set(paymentRecord.billingAccount());
        epr.paymentRecord().set(paymentRecord);
        Persistence.service().persist(epr);
    }

    private PaymentRecord getPaymentRecord(String transactionId) {
        EntityQueryCriteria<PaymentRecordExternal> criteria = EntityQueryCriteria.create(PaymentRecordExternal.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().externalTransactionId(), transactionId));
        PaymentRecordExternal epr = Persistence.service().retrieve(criteria);
        return epr == null ? null : epr.paymentRecord();
    }

    private Building getBuilding(String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().propertyCode(), propertyCode));
        criteria.add(PropertyCriterion.eq(criteria.proto().useExternalBilling(), true));
        return Persistence.service().retrieve(criteria);
    }

    private List<Lease> getActiveLeases(Building bld) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit().building(), bld));
        criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.active()));
        return Persistence.service().query(criteria);
    }

    private BillingAccount getBillingAccount(final String leaseId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseId(), leaseId));
        Lease lease = Persistence.service().retrieve(criteria);
        return lease == null ? null : lease.billingAccount();
    }
}
