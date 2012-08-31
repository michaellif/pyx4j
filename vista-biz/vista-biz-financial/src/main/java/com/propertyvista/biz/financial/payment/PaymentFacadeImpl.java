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

import org.apache.commons.lang.Validate;

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
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.util.DomainUtil;

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
        paymentMethod.isDeleted().setValue(Boolean.FALSE);

        Validate.isTrue(!paymentMethod.customer().isNull(), "Owner (customer) is required for PaymentMethod");

        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo eci = paymentMethod.details().cast();
            if (!eci.accountNo().newNumberValue().isNull()) {
                eci.accountNo().number().setValue(eci.accountNo().newNumberValue().getValue());
            } else {
                Validate.isTrue(!paymentMethod.details().id().isNull(), "Account number is required");
                //TODO move to framework
                // Do merge.                
                EcheckInfo origValue = Persistence.service().retrieve(EcheckInfo.class, eci.getPrimaryKey());
                eci.accountNo().number().setValue(origValue.accountNo().number().getValue());
            }
            eci.accountNo().reference().setValue(DomainUtil.last4Numbers(eci.accountNo().number().getValue()));
            break;
        case CreditCard:
            //Verify CC change
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!paymentMethod.details().id().isNull()) {
                CreditCardInfo ccOrigValue = Persistence.service().retrieve(CreditCardInfo.class, cc.getPrimaryKey());
                if (cc.card().newNumberValue().isNull()) {
                    Validate.isTrue(cc.card().reference().equals(ccOrigValue.card().reference()));
                }
            }
            break;
        case Cash:
            // Assert value type
            Validate.isTrue(paymentMethod.details().isInstanceOf(CashInfo.class));
            break;
        case Check:
            // Assert value type
            Validate.isTrue(paymentMethod.details().isInstanceOf(CheckInfo.class));
            break;
        default:
            throw new Error();
        }

        Persistence.service().merge(paymentMethod);

        switch (paymentMethod.type().getValue()) {
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.card().newNumberValue().isNull()) {
                cc.card().number().setValue(cc.card().newNumberValue().getValue());
                cc.card().reference().setValue(DomainUtil.last4Numbers(cc.card().number().getValue()));
            }
            // Allow to update expiryDate 
            CreditCardProcessor.persistToken(building, cc);
            Persistence.service().merge(paymentMethod);

            break;
        default:
            break;
        }

        return paymentMethod;
    }

    @Override
    public void deletePaymentMethod(PaymentMethod paymentMethod) {
        Persistence.service().retrieve(paymentMethod);
        paymentMethod.isDeleted().setValue(Boolean.TRUE);
        paymentMethod.isOneTimePayment().setValue(Boolean.TRUE);
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
        return methods;
    }

    @Override
    public PaymentRecord persistPayment(PaymentRecord paymentRecord) {
        if (paymentRecord.paymentStatus().isNull()) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        }
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        Building building = Persistence.service().retrieve(criteria);
        persistPaymentMethod(building, paymentRecord.paymentMethod());

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
        Validate.isTrue(PaymentType.schedulable().contains(paymentRecord.paymentMethod().type().getValue()));

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
            throw new IllegalArgumentException("Cash is automactialy cleard");
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
