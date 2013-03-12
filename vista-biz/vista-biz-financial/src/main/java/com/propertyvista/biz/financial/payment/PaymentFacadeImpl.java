/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Collection;
import java.util.EnumSet;

import org.apache.commons.lang.Validate;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class PaymentFacadeImpl implements PaymentFacade {

    private static final I18n i18n = I18n.get(PaymentFacadeImpl.class);

    @Override
    public boolean isPaymentsAllowed(BillingAccount billingAccountId) {
        return PaymentUtils.isPaymentsAllowed(billingAccountId);
    }

    @Override
    public boolean isElectronicPaymentsAllowed(BillingAccount billingAccountId) {
        return PaymentUtils.isElectronicPaymentsAllowed(billingAccountId);
    }

    @Override
    public boolean isElectronicPaymentsAllowed(Lease leaseId) {
        return PaymentUtils.isElectronicPaymentsAllowed(leaseId);
    }

    @Override
    public boolean isElectronicPaymentsAllowed(LeaseTerm leaseTermId) {
        return PaymentUtils.isElectronicPaymentsAllowed(leaseTermId);
    }

    @Override
    public Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedPaymentTypes(billingAccountId, vistaApplication);
    }

    @Override
    public PaymentRecord persistPayment(PaymentRecord paymentRecord) {
        if (paymentRecord.paymentStatus().isNull()) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        }
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        Building building = Persistence.service().retrieve(criteria);
        PaymentMethodPersister.persistLeasePaymentMethod(building, paymentRecord.paymentMethod());

        if (paymentRecord.id().isNull()) {
            paymentRecord.createdDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        }

        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord schedulePayment(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!paymentRecord.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Submitted)) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }
        if (paymentRecord.targetDate().isNull()) {
            throw new UserRuntimeException(i18n.tr("Payment target date should be present"));
        }
        Validate.isTrue(PaymentType.schedulable().contains(paymentRecord.paymentMethod().type().getValue()));

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Scheduled);
        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord processPayment(PaymentRecord paymentId) throws PaymentException {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.merchantAccount().set(PaymentUtils.retrieveMerchantAccount(paymentRecord));
        if (paymentRecord.merchantAccount().isNull()
                && (PaymentRecord.merchantAccountIsRequedForPayments || PaymentType.electronicPayments().contains(
                        paymentRecord.paymentMethod().type().getValue()))) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount found to process the payment"));
        }

        try {
            if (!ServerSideFactory.create(ARFacade.class).validatePayment(paymentRecord)) {
                throw new PaymentException("Failed to post payment to AR while processing payment");
            }
        } catch (ARException e) {
            throw new PaymentException("Failed to post payment to AR while processing payment", e);
        }

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Cash:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
            paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            break;
        case Check:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            break;
        case CreditCard:
            // The credit card processing is done in new transaction and committed regardless of results
            CreditCardProcessor.realTimeSale(paymentRecord);
            paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            break;
        case Echeck:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            break;
        case EFT:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            break;
        case Interac:
            throw new IllegalArgumentException("Not implemented");
        default:
            throw new IllegalArgumentException("paymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }

        Persistence.service().merge(paymentRecord);

        if (paymentRecord.paymentStatus().getValue() != PaymentRecord.PaymentStatus.Rejected) {
            paymentRecord.receivedDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            try {
                ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            } catch (ARException e) {
                throw new PaymentException("Failed to post payment to AR while processing payment", e);
            }
            Persistence.service().merge(paymentRecord);
        }

        return paymentRecord;
    }

    @Override
    public PaymentRecord cancel(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.Queued).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be canceled"));
        }

        PaymentStatus incommingStatus = paymentRecord.paymentStatus().getValue();

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Canceled);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        if (incommingStatus == PaymentRecord.PaymentStatus.Queued) {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        }

        return paymentRecord;
    }

    @Override
    public PaymentRecord clear(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be cleared"));
        }
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Echeck:
        case EFT:
        case CreditCard:
        case Interac:
            throw new IllegalArgumentException("Electronic PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        case Cash:
            throw new IllegalArgumentException("Cash is automatically cleared");
        case Check:
            break;
        }

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord reject(PaymentRecord paymentId, boolean applyNSF) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be rejected"));
        }
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Echeck:
        case EFT:
        case CreditCard:
        case Interac:
            throw new IllegalArgumentException("Electronic PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        case Cash:
            Validate.isTrue(!applyNSF, "Can't Apply NSF on Cash");
            break;
        case Check:
            break;
        }

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Check:
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, applyNSF);
            break;
        case Cash:
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
            break;
        default:
            throw new IllegalArgumentException("PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        return paymentRecord;
    }

    @Override
    public void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransferStub) {
        AggregatedTransfer at = Persistence.service().retrieve(AggregatedTransfer.class, aggregatedTransferStub.getPrimaryKey());
        if (!EnumSet.of(AggregatedTransferStatus.Rejected).contains(at.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed transaction can't be canceled"));
        }
        new PadProcessor().cancelAggregatedTransfer(at);
    }

}
