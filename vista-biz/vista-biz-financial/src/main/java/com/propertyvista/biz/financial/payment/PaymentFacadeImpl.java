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
import com.pyx4j.entity.shared.utils.EntityGraph;
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
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
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
    public boolean isElectronicPaymentsAllowed(Lease leaseId) {
        return PaymentUtils.isElectronicPaymentsAllowed(leaseId);
    }

    @Override
    public boolean isElectronicPaymentsAllowed(LeaseTerm leaseTermId) {
        return PaymentUtils.isElectronicPaymentsAllowed(leaseTermId);
    }

    private boolean isAccountNumberChange(PaymentMethod paymentMethod, PaymentMethod origPaymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo eci = paymentMethod.details().cast();
            if (!eci.accountNo().newNumber().isNull()) {
                return true;
            }
            EcheckInfo origeci = origPaymentMethod.details().cast();
            if (!EntityGraph.membersEquals(eci, origeci, eci.bankId(), eci.branchTransitNumber())) {
                return true;
            }
            break;
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.card().newNumber().isNull()) {
                return true;
            }
            CreditCardInfo origcc = origPaymentMethod.details().cast();
            if (!EntityGraph.membersEquals(cc, origcc, cc.cardType())) {
                return true;
            }
            break;
        default:
            break;
        }
        return false;
    }

    @Override
    public LeasePaymentMethod persistPaymentMethod(Building building, LeasePaymentMethod paymentMethod) {
        LeasePaymentMethod origPaymentMethod = null;
        if (!paymentMethod.id().isNull()) {
            // Keep history of payment methods that were used.
            origPaymentMethod = Persistence.service().retrieve(LeasePaymentMethod.class, paymentMethod.getPrimaryKey());
            if (isAccountNumberChange(paymentMethod, origPaymentMethod)) {
                EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
                criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
                criteria.ne(criteria.proto().paymentStatus(), PaymentStatus.Submitted);
                if (Persistence.service().count(criteria) != 0) {
                    origPaymentMethod.isDeleted().setValue(true);
                    Persistence.service().merge(origPaymentMethod);
                    EntityGraph.makeDuplicate(paymentMethod);
                    switch (paymentMethod.type().getValue()) {
                    case CreditCard:
                        CreditCardInfo cc = paymentMethod.details().cast();
                        cc.token().setValue(null);
                        break;
                    case Echeck:
                        EcheckInfo eci = paymentMethod.details().cast();
                        if (eci.accountNo().newNumber().isNull()) {
                            EcheckInfo origeci = origPaymentMethod.details().cast();
                            eci.accountNo().newNumber().setValue(origeci.accountNo().number().getValue());
                        }
                    default:
                        break;
                    }
                    origPaymentMethod = null;
                }
            }
        } else {
            // New Value validation
            switch (paymentMethod.type().getValue()) {
            case CreditCard:
                CreditCardInfo cc = paymentMethod.details().cast();
                Validate.isTrue(cc.token().isNull(), "Can't attach to token");
                break;
            default:
                break;
            }
        }

        paymentMethod.isDeleted().setValue(Boolean.FALSE);

        Validate.isTrue(!paymentMethod.customer().isNull(), "Owner (customer) is required for PaymentMethod");

        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo eci = paymentMethod.details().cast();
            if (!eci.accountNo().newNumber().isNull()) {
                eci.accountNo().number().setValue(eci.accountNo().newNumber().getValue());
                eci.accountNo().obfuscatedNumber().setValue(DomainUtil.obfuscateAccountNumber(eci.accountNo().number().getValue()));
            } else {
                Validate.isTrue(!paymentMethod.details().id().isNull(), "Account number is required when creating Echeck");
                EcheckInfo origeci = origPaymentMethod.details().cast();
                eci.accountNo().number().setValue(origeci.accountNo().number().getValue());
                Validate.isTrue(eci.accountNo().obfuscatedNumber().equals(origeci.accountNo().obfuscatedNumber()), "obfuscatedNumber changed");
            }
            break;
        case CreditCard:
            //Verify CC change
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!paymentMethod.details().id().isNull()) {
                CreditCardInfo origcc = origPaymentMethod.details().cast();
                if (cc.card().newNumber().isNull()) {
                    Validate.isTrue(cc.card().obfuscatedNumber().equals(origcc.card().obfuscatedNumber()), "obfuscatedNumber changed");
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
            if (!cc.card().newNumber().isNull()) {
                cc.card().number().setValue(cc.card().newNumber().getValue());
                cc.card().obfuscatedNumber().setValue(DomainUtil.obfuscateCreditCardNumber(cc.card().newNumber().getValue()));
            }
            // Allow to update expiryDate or create token
            boolean needUpdate = (origPaymentMethod == null);
            needUpdate |= (!cc.card().newNumber().isNull());
            if (origPaymentMethod != null) {
                needUpdate |= (!EntityGraph.membersEquals(cc, origPaymentMethod.details().cast(), cc.expiryDate()));
            }
            if (needUpdate) {
                CreditCardProcessor.persistToken(building, cc);
                Persistence.service().merge(cc);
            }
            break;
        default:
            break;
        }

        return paymentMethod;
    }

    @Override
    public void deletePaymentMethod(LeasePaymentMethod paymentMethod) {
        Persistence.service().retrieve(paymentMethod);
        paymentMethod.isDeleted().setValue(Boolean.TRUE);
        paymentMethod.isOneTimePayment().setValue(Boolean.TRUE);
        Persistence.service().merge(paymentMethod);
    }

    @Override
    public List<LeasePaymentMethod> retrievePaymentMethods(LeaseTermParticipant<?> participant) {
        assert !participant.leaseParticipant().customer().isValueDetached();
        return retrievePaymentMethods(participant.leaseParticipant().customer());
    }

    @Override
    public List<LeasePaymentMethod> retrievePaymentMethods(Customer customer) {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
        criteria.add(PropertyCriterion.eq(criteria.proto().isOneTimePayment(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        List<LeasePaymentMethod> methods = Persistence.service().query(criteria);
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
        if (paymentRecord.merchantAccount().isNull()
                && (PaymentRecord.merchantAccountIsRequedForPayments || PaymentType.electronicPayments().contains(
                        paymentRecord.paymentMethod().type().getValue()))) {
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
