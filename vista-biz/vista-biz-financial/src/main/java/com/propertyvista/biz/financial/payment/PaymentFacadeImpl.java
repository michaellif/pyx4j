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

import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

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
    public PaymentMethod persistPaymentMethod(Building building, PaymentMethod paymentMethod) {

        // if it saved - these flags should be lowered: 
        paymentMethod.isOneTimePayment().setValue(Boolean.FALSE);
        paymentMethod.isDeleted().setValue(Boolean.FALSE);

        Persistence.service().merge(paymentMethod);

        // store credit cards
        if (PaymentType.CreditCard == paymentMethod.type().getValue()) {
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.number().isNull()) {
                cc.numberRefference().setValue(last4Numbers(cc.number().getValue()));
                CreditCardProcessor.persistToken(building, cc);
                Persistence.service().merge(paymentMethod);
            }
        }

        return paymentMethod;
    }

    @Override
    public void deletePaymentMethod(PaymentMethod paymentMethod) {
        Persistence.service().retrieve(paymentMethod);
        paymentMethod.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(paymentMethod);
    }

    @Override
    public List<PaymentMethod> retrievePaymentMethods(LeaseParticipant participant) {
        assert !participant.customer().isValueDetached();
        EntityQueryCriteria<PaymentMethod> criteria = new EntityQueryCriteria<PaymentMethod>(PaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), participant.customer()));
        criteria.add(PropertyCriterion.eq(criteria.proto().isOneTimePayment(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        List<PaymentMethod> methods = Persistence.service().query(criteria);
        for (PaymentMethod method : methods) {
            hidePaymentMethodDetails(method);
        }
        return methods;
    }

    @Override
    public PaymentMethod hidePaymentMethodDetails(PaymentMethod method) {
        if (method.isValueDetached()) {
            Persistence.service().retrieve(method);
        }

        switch (method.type().getValue()) {
        case CreditCard:
            CreditCardInfo cci = method.details().cast();
            if (!cci.numberRefference().isNull()) {
                cci.number().setValue(i18n.tr("XXXX XXXX XXXX {0}", cci.numberRefference().getValue()));
                cci.securityCode().setValue("XXX");
            }
            break;

        case Echeck:
            EcheckInfo eci = method.details().cast();
            if (!eci.accountNo().isNull()) {
                eci.accountNo().setValue(i18n.tr("XXXX XXXX {0}", last4Numbers(eci.accountNo().getValue())));
            }
            break;

        default:
            break;
        }

        return method;
    }

    private String last4Numbers(String value) {
        if (value.length() < 4) {
            return null;
        } else {
            return value.substring(value.length() - 4, value.length());
        }
    }

    @Override
    public PaymentRecord persistPayment(PaymentRecord paymentRecord) {
        if (paymentRecord.paymentStatus().isNull()) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        }
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        if (paymentRecord.paymentMethod().isOneTimePayment().isBooleanTrue()) {

            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
            Building building = Persistence.service().retrieve(criteria);

            persistPaymentMethod(building, paymentRecord.paymentMethod());
        }

        if (paymentRecord.id().isNull()) {
            paymentRecord.createdDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
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
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Scheduled);
        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord processPayment(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey());
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        if ((!paymentRecord.targetDate().isNull()) && Persistence.service().getTransactionSystemTime().before(paymentRecord.targetDate().getValue())) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Scheduled);
            Persistence.service().merge(paymentRecord);
            return paymentRecord;
        }

        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.merchantAccount().set(PaymentUtils.retrieveMerchantAccount(paymentRecord));
        if (paymentRecord.merchantAccount().isNull()) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount found to process the payment"));
        }

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Cash:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
            paymentRecord.receivedDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            Persistence.service().merge(paymentRecord);
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            break;
        case Check:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            paymentRecord.receivedDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            Persistence.service().merge(paymentRecord);
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            break;
        case CreditCard:
            // The credit card processing is done in new transaction and committed regardless of results
            Persistence.service().commit();
            CreditCardProcessor.realTimeSale(paymentRecord);
            break;
        case Echeck:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            Persistence.service().merge(paymentRecord);
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            break;
        case EFT:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            paymentRecord.receivedDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            Persistence.service().merge(paymentRecord);
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            break;
        case Interac:
            throw new IllegalArgumentException("Not implemented");
        default:
            throw new IllegalArgumentException("paymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
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
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
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
        case Check:
            break;
        }

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord reject(PaymentRecord paymentId) {
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
        case Check:
            break;
        }

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.service().merge(paymentRecord);

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Check:
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
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
