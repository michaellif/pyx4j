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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.server.common.security.VistaContext;

public class PaymentFacadeImpl implements PaymentFacade {

    private final static Logger log = LoggerFactory.getLogger(PaymentFacadeImpl.class);

    private static final I18n i18n = I18n.get(PaymentFacadeImpl.class);

    @Override
    public boolean isPaymentsAllowed(BillingAccount billingAccountId) {
        return PaymentUtils.isPaymentsAllowed(billingAccountId);
    }

    @Override
    public boolean isElectronicPaymentsSetup(BillingAccount billingAccountId) {
        return PaymentUtils.isElectronicPaymentsSetup(billingAccountId);
    }

    @Override
    public boolean isElectronicPaymentsSetup(Lease leaseId) {
        return PaymentUtils.isElectronicPaymentsSetup(leaseId);
    }

    @Override
    public boolean isElectronicPaymentsSetup(LeaseTerm leaseTermId) {
        return PaymentUtils.isElectronicPaymentsSetup(leaseTermId);
    }

    @Override
    public Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedPaymentTypes(billingAccountId, vistaApplication);
    }

    @Override
    public Collection<CreditCardType> getAllowedCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedCardTypes(billingAccountId, vistaApplication);
    }

    @Override
    public Collection<CreditCardType> getConvenienceFeeApplicableCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return PaymentUtils.getConvenienceFeeApplicableCardTypes(billingAccountId, vistaApplication);
    }

    @Override
    public ConvenienceFeeCalculationResponseTO getConvenienceFee(BillingAccount billingAccountId, CreditCardType cardType, BigDecimal amount) {
        MerchantAccount account = PaymentUtils.retrieveValidMerchantAccount(billingAccountId);
        return ServerSideFactory.create(CreditCardFacade.class).getConvenienceFee(account.merchantTerminalId().getValue(), ReferenceNumberPrefix.RentPayments,
                cardType, amount);
    }

    @Override
    public void validatePaymentMethod(BillingAccount billingAccount, LeasePaymentMethod paymentMethod, VistaApplication vistaApplication) {
        switch (vistaApplication) {
        case resident:
            Validate.isTrue(PaymentType.availableInPortal().contains(paymentMethod.type().getValue()), "Payment type not acceptable");
            break;
        case crm:
            Validate.isTrue(PaymentType.availableInCrm().contains(paymentMethod.type().getValue()), "Payment type not acceptable");
            break;
        default:
            throw new IllegalArgumentException();
        }

        Validate.isTrue(getAllowedPaymentTypes(billingAccount, vistaApplication).contains(paymentMethod.type().getValue()), "Payment type not acceptable");
        if (paymentMethod.type().getValue() == PaymentType.CreditCard) {
            CreditCardType cardType = paymentMethod.details().<CreditCardInfo> cast().cardType().getValue();
            Validate.isTrue(getAllowedCardTypes(billingAccount, vistaApplication).contains(cardType), "Card type " + cardType + " not acceptable");
        }
    }

    @Override
    public void validatePayment(PaymentRecord paymentRecord, VistaApplication vistaApplication) {
        if (paymentRecord.paymentMethod().details().isInstanceOf(CreditCardInfo.class)) {
            CreditCardType ccType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
            if (ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(paymentRecord.billingAccount(), vistaApplication)
                    .contains(ccType)) {
                Validate.notNull(paymentRecord.convenienceFee().getValue(), "Convenience Fee fee not calculated");
                Validate.notNull(paymentRecord.convenienceFeeReferenceNumber().getValue(), "Convenience Fee fee not calculated");
            }
        }
        if (!paymentRecord.paymentMethod().id().isNull()) {
            EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
            criteria.eq(criteria.proto().id(), paymentRecord.paymentMethod().id());
            criteria.eq(criteria.proto().customer()._tenantInLease().$().lease().billingAccount(), paymentRecord.billingAccount());
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            Validate.isTrue(1 == Persistence.service().count(criteria), "Payment record owner is not on lease");
        }
    }

    @Override
    public PaymentRecord persistPayment(PaymentRecord paymentRecord) {
        if (!paymentRecord.id().isNull()) {
            PaymentRecord paymentRecordLock = Persistence.service().retrieve(PaymentRecord.class, paymentRecord.getPrimaryKey(), AttachLevel.Attached, true);
            if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                    paymentRecordLock.paymentStatus().getValue())) {
                throw new IllegalArgumentException("paymentStatus:" + paymentRecordLock.paymentStatus().getValue());
            }
        }

        if (paymentRecord.paymentStatus().isNull()) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        }
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        Building building = Persistence.service().retrieve(criteria);
        PaymentMethodPersister.persistLeasePaymentMethod(building, paymentRecord.paymentMethod());

        boolean isNew = false;
        if (paymentRecord.id().isNull()) {
            isNew = true;
            paymentRecord.createdDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            paymentRecord.createdBy().set(VistaContext.getCurrentUserIfAvalable());
        }

        //  receivedDate should be editable by CRM user for Cash and Check.
        if (EnumSet.of(PaymentType.Cash, PaymentType.Check).contains((paymentRecord.paymentMethod().type().getValue()))) {
            if (paymentRecord.receivedDate().isNull()) {
                paymentRecord.receivedDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            }
        } else {
            paymentRecord.receivedDate().setValue(null);
        }

        Persistence.service().merge(paymentRecord);
        if (paymentRecord.yardiDocumentNumber().isNull()) {
            StringBuilder b = new StringBuilder();
            if (paymentRecord.paymentMethod().type().getValue() == PaymentType.Echeck) {
                switch (VistaDeployment.getCurrentPmc().features().countryOfOperation().getValue()) {
                case Canada:
                    b.append("eCheque (EFT)");
                    break;
                case US:
                    b.append("eCheck (ACH)");
                    break;
                default:
                    b.append("eCheck");
                    break;
                }
            } else if (paymentRecord.paymentMethod().type().getValue() == PaymentType.CreditCard) {
                CreditCardType cardType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
                b.append(cardType.name());
            } else {
                b.append(paymentRecord.paymentMethod().type().getValue().name());
            }
            if (!VistaDeployment.isVistaProduction()) {
                b.append(":t").append(PadTransactionUtils.readTestDBversionIdInOperations());
            }
            b.append(":").append(paymentRecord.getPrimaryKey().toString());
            paymentRecord.yardiDocumentNumber().setValue(b.toString());
            Persistence.service().persist(paymentRecord);
        }

        if (isNew) {
            ServerSideFactory.create(AuditFacade.class).created(paymentRecord);
        }

        return paymentRecord;
    }

    @Override
    public PaymentRecord schedulePayment(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }
        if (paymentRecord.targetDate().isNull()) {
            throw new UserRuntimeException(i18n.tr("Payment target date should be present"));
        }
        Validate.isTrue(paymentRecord.paymentMethod().type().getValue().isSchedulable());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Scheduled);

        MerchantAccount merchantAccount = PaymentUtils.retrieveMerchantAccount(paymentRecord);
        if ((merchantAccount == null) || (!PaymentUtils.isElectronicPaymentsSetup(merchantAccount))) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.PendingAction);
        }
        Persistence.service().merge(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord processPayment(PaymentRecord paymentId, PaymentBatchContext paymentBatchContext) throws PaymentException {
        final PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        //  receivedDate should be editable by CRM user for Cash and Check.
        if (EnumSet.of(PaymentType.Cash, PaymentType.Check).contains((paymentRecord.paymentMethod().type().getValue()))) {
            if (paymentRecord.receivedDate().isNull()) {
                paymentRecord.receivedDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            }
        } else {
            paymentRecord.receivedDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        }

        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.merchantAccount().set(PaymentUtils.retrieveMerchantAccount(paymentRecord));
        if (paymentRecord.merchantAccount().isNull()
                && (PaymentRecord.merchantAccountIsRequedForPayments || PaymentType.electronicPayments().contains(
                        paymentRecord.paymentMethod().type().getValue()))) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount found to process the payment"));
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
            PaymentCreditCard.processPayment(paymentRecord);
            paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            break;
        case Echeck:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            break;
        case DirectBanking:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            break;
        case Interac:
            throw new IllegalArgumentException("Not implemented");
        default:
            throw new IllegalArgumentException("paymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }

        if ((paymentBatchContext == null) && (!paymentRecord.batch().isNull())) {
            // Detach from posting batch
            paymentRecord.batch().set(null);
        }

        Persistence.service().merge(paymentRecord);

        if (paymentRecord.paymentStatus().getValue() != PaymentRecord.PaymentStatus.Rejected) {
            try {
                ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord, paymentBatchContext);

                if (paymentBatchContext == null) {
                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            log.error("Unable to cancel posted Receipt Batch to Yardi; {}", paymentRecord);

                            ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord, "Unable to cancel posted Receipt Batch to Yardi; {}",
                                    paymentRecord);

                            return null;
                        }
                    });
                }

            } catch (ARException e) {
                throw new PaymentException("Failed to post payment to AR while processing payment", e);
            }
        } else {
            paymentRecord.receivedDate().setValue(null);
        }

        Persistence.service().merge(paymentRecord);

        if ((paymentRecord.paymentMethod().type().getValue() == PaymentType.Echeck) && (paymentRecord.preauthorizedPayment().isNull())) {
            ServerSideFactory.create(NotificationFacade.class).oneTimePaymentSubmitted(paymentRecord);
        }

        return paymentRecord;
    }

    @Override
    public PaymentRecord cancel(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!PaymentRecord.PaymentStatus.cancelable().contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be canceled"));
        }

        PaymentStatus incommingStatus = paymentRecord.paymentStatus().getValue();

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Canceled);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);

        if (incommingStatus == PaymentRecord.PaymentStatus.Queued) {
            try {
                ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
            } catch (ARException e) {
                throw new UserRuntimeException(i18n.tr("Payment can't be canceled"), e);
            }
        }
        log.info("Payment {} {} Canceled", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
        ServerSideFactory.create(AuditFacade.class).updated(paymentRecord, "Canceled");
        return paymentRecord;
    }

    @Override
    public PaymentRecord clear(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!PaymentRecord.PaymentStatus.checkClearable().contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be cleared"));
        }
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Echeck:
        case DirectBanking:
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

        ServerSideFactory.create(AuditFacade.class).updated(paymentRecord, "Cleared");
        ServerSideFactory.create(NotificationFacade.class).paymentCleared(paymentRecord);
        return paymentRecord;
    }

    @Override
    public PaymentRecord reject(PaymentRecord paymentId, boolean applyNSF) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!PaymentRecord.PaymentStatus.checkRejectable().contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be rejected"));
        }
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Echeck:
        case DirectBanking:
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

        try {
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
        } catch (ARException e) {
            throw new UserRuntimeException(i18n.tr("Payment can't be rejected"), e);
        }
        ServerSideFactory.create(AuditFacade.class).updated(paymentRecord, "Rejected");
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

    @Override
    public List<PaymentRecord> getLatestPaymentActivity(BillingAccount billingAccount) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        LogicalDate dateFrom = new LogicalDate(cal.getTime());
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.gt(criteria.proto().receivedDate(), dateFrom));
        List<PaymentRecord> result = Persistence.service().query(criteria);
        // sort recent first
        Collections.sort(result, new Comparator<PaymentRecord>() {
            @Override
            public int compare(PaymentRecord o1, PaymentRecord o2) {
                return o2.receivedDate().compareTo(o1.receivedDate());
            }
        });
        return result;
    }

}
