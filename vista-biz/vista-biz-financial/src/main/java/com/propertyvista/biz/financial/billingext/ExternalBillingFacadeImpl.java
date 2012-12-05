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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARDateUtils;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.Period;
import com.propertyvista.domain.financial.billingext.ExtendedPaymentRecord;
import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
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
        charge.claimed().setValue(false);
        charge.dueDate().setValue(ARDateUtils.calculateDueDate(billingAccount));
        charge.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        Persistence.service().persist(charge);

        return true;
    }

    @Override
    public List<PaymentRecordDTO> getNonProcessedPaymentRecords() {
        // 1 - find all ExtendedPaymentRecord with no externalTransactionId
        EntityQueryCriteria<ExtendedPaymentRecord> criteria = EntityQueryCriteria.create(ExtendedPaymentRecord.class);
        criteria.add(PropertyCriterion.isNull(criteria.proto().externalTransactionId()));
        List<ExtendedPaymentRecord> result = Persistence.service().query(criteria);
        if (result == null || result.size() == 0) {
            return null;
        }

        // 2 - for each ExtendedPaymentRecord create PaymentRecordDTO where PaymentRecordDTO.transactionId = ExtendedPaymentRecord.id
        List<PaymentRecordDTO> paymentRecords = new ArrayList<PaymentRecordDTO>();
        for (ExtendedPaymentRecord epr : result) {
            PaymentRecordDTO prDTO = EntityFactory.create(PaymentRecordDTO.class);
            prDTO.amount().set(epr.paymentRecord().amount());
            prDTO.transactionDate().set(epr.paymentRecord().finalizeDate());
            prDTO.paymentType().setValue(epr.paymentRecord().paymentMethod().type().toString());
            prDTO.leaseId().set(epr.paymentRecord().billingAccount().lease().leaseId());
            prDTO.transactionId().setValue(epr.getPrimaryKey().toString());
            paymentRecords.add(prDTO);
        }

        return paymentRecords;
    }

    @Override
    public void reconcilePaymentRecords(List<PaymentRecordDTO> records) {
        if (records == null || records.size() == 0) {
            return;
        }
        // for each PaymentRecordDTO find corresponding ExtendedPaymentRecord using PaymentRecordDTO.transactionId
        // make sure ExtendedPaymentRecord.externalTransactionId is not set
        // update ExtendedPaymentRecord.externalTransactionId with PaymentRecordDTO.externalTransactionId
        for (PaymentRecordDTO record : records) {
            ExtendedPaymentRecord epr = Persistence.service().retrieve(ExtendedPaymentRecord.class, new Key(record.transactionId().getValue()));
            if (epr != null) {
                epr.externalTransactionId().set(record.externalTransactionId());
            }
        }
        Persistence.service().commit();
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
        ExtendedPaymentRecord epr = EntityFactory.create(ExtendedPaymentRecord.class);
        epr.billingAccount().set(paymentRecord.billingAccount());
        epr.paymentRecord().set(paymentRecord);
        Persistence.service().persist(epr);
        Persistence.service().commit();
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
