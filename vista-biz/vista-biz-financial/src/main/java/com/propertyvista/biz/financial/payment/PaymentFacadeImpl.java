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
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.AllowedPaymentsSetup;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentPostingBatch;
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
    public AllowedPaymentsSetup getAllowedPaymentsSetup(BillingAccount billingAccountId, PaymentMethodTarget paymentMethodTarget,
            VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedPaymentsSetup(billingAccountId, paymentMethodTarget, vistaApplication);
    }

    @Override
    public AllowedPaymentsSetup getAllowedPaymentsSetup(Building policyNode, PaymentMethodTarget paymentMethodTarget, VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedPaymentsSetup(policyNode, paymentMethodTarget, vistaApplication);
    }

    @Override
    public Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, PaymentMethodTarget paymentMethodTarget,
            VistaApplication vistaApplication) {
        return PaymentUtils.getAllowedPaymentTypes(billingAccountId, paymentMethodTarget, vistaApplication);
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
        MerchantAccount account = PaymentUtils.getMerchantAccount(billingAccountId);
        Validate.isTrue(!account.merchantTerminalIdConvenienceFee().isNull(), "MerchantAccount not setup to process the payment with fee");
        return ServerSideFactory.create(CreditCardFacade.class).getConvenienceFee(account.merchantTerminalIdConvenienceFee().getValue(),
                ReferenceNumberPrefix.RentPayments, cardType, amount);
    }

    @Override
    public void validatePaymentMethod(BillingAccount billingAccount, LeasePaymentMethod paymentMethod, PaymentMethodTarget paymentMethodTarget,
            VistaApplication vistaApplication) {
        switch (vistaApplication) {
        case prospect:
        case resident:
            Validate.isTrue(PaymentType.availableInPortal().contains(paymentMethod.type().getValue()), "Payment type not acceptable");
            break;
        case crm:
            Validate.isTrue(PaymentType.availableInCrm().contains(paymentMethod.type().getValue()), "Payment type not acceptable");
            break;
        default:
            throw new IllegalArgumentException();
        }

        Validate.isTrue(getAllowedPaymentTypes(billingAccount, paymentMethodTarget, vistaApplication).contains(paymentMethod.type().getValue()),
                "Payment type not acceptable");
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
                Validate.isTrue(paymentRecord.convenienceFeeSignedTerm().signature().agree().getValue(), "Convenience Fee fee not signed");
            } else {
                paymentRecord.convenienceFeeSignedTerm().set(null);
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
            paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        }
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().units().$().leases().$().billingAccount(), paymentRecord.billingAccount()));
        Building building = Persistence.service().retrieve(criteria);
        PaymentMethodPersister.persistLeasePaymentMethod(building, paymentRecord.paymentMethod());

        boolean isNew = false;
        if (paymentRecord.id().isNull()) {
            isNew = true;
            paymentRecord.created().setValue(SystemDateManager.getDate());
            paymentRecord.createdBy().set(VistaContext.getCurrentUserIfAvalable());
        }

        //  receivedDate should be editable by CRM user for Cash and Check.
        if (EnumSet.of(PaymentType.Cash, PaymentType.Check).contains((paymentRecord.paymentMethod().type().getValue()))) {
            if (paymentRecord.receivedDate().isNull()) {
                paymentRecord.receivedDate().setValue(SystemDateManager.getLogicalDate());
            }
        } else {
            paymentRecord.receivedDate().setValue(null);
        }

        Persistence.service().merge(paymentRecord);
        if (paymentRecord.yardiDocumentNumber().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignDocumentNumber(paymentRecord);
            StringBuilder b = new StringBuilder();
            b.append(paymentRecord.yardiDocumentNumber().getStringView());
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

        Persistence.ensureRetrieve(paymentRecord.billingAccount().lease(), AttachLevel.Attached);
        assert !Lease.Status.noPayment().contains(paymentRecord.billingAccount().lease().status().getValue()) : "Cannot process payment for inactive lease";

        if (!EnumSet.of(PaymentRecord.PaymentStatus.Submitted, PaymentRecord.PaymentStatus.Scheduled, PaymentRecord.PaymentStatus.PendingAction).contains(
                paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        //  receivedDate should be editable by CRM user for Cash and Check.
        if (EnumSet.of(PaymentType.Cash, PaymentType.Check).contains((paymentRecord.paymentMethod().type().getValue()))) {
            if (paymentRecord.receivedDate().isNull()) {
                paymentRecord.receivedDate().setValue(SystemDateManager.getLogicalDate());
            }
        } else {
            paymentRecord.receivedDate().setValue(SystemDateManager.getLogicalDate());
        }

        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());

        paymentRecord.merchantAccount().set(PaymentUtils.retrieveMerchantAccount(paymentRecord));
        if (paymentRecord.merchantAccount().isNull()
                && (PaymentRecord.merchantAccountIsRequedForPayments || PaymentType.electronicPayments().contains(
                        paymentRecord.paymentMethod().type().getValue()))) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount found to process the payment"));
        }

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Cash:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
            paymentRecord.finalizedDate().setValue(SystemDateManager.getLogicalDate());
            break;
        case Check:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            break;
        case CreditCard:
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
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

        if (PaymentRecord.PaymentStatus.arPostable().contains(paymentRecord.paymentStatus().getValue())) {
            try {
                ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord, paymentBatchContext);

                if (paymentBatchContext == null) {
                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            log.error("Unable to cancel posted Receipt Batch to Yardi; {}", paymentRecord);
                            ServerSideFactory
                                    .create(OperationsAlertFacade.class)
                                    .record(paymentRecord,
                                            "Unable to Cancel in Yardi posted Receipt Batch; Payment Record {0} {1} will appear in Yardi BUT will not be processed in Vista",
                                            paymentRecord.id(), paymentRecord.amount());

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
    public boolean isCompleteTransactionRequired(PaymentRecord paymentRecord) {
        return (paymentRecord.paymentMethod().type().getValue() == PaymentType.CreditCard)
                && (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Queued);
    }

    /**
     * Used For CreditCards status 'Queued', Change to status: 'Received' or 'Rejected' (call AR. Reject)
     */
    @Override
    public PaymentRecord completeRealTimePayment(PaymentRecord paymentId) {
        final PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);

        if (!EnumSet.of(PaymentRecord.PaymentStatus.Queued).contains(paymentRecord.paymentStatus().getValue())) {
            throw new IllegalArgumentException("paymentStatus:" + paymentRecord.paymentStatus().getValue());
        }

        switch (paymentRecord.paymentMethod().type().getValue()) {
        case CreditCard:
            PaymentCreditCard.processPayment(paymentRecord);
            break;
        default:
            throw new IllegalArgumentException("paymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }

        return paymentRecord;

    }

    @Override
    public void processPaymentUnitOfWork(final PaymentRecord paymentId, final boolean cancelOnError) {
        final PaymentRecord paymentRecord = paymentId;
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                try {
                    if (cancelOnError) {
                        UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {
                            @Override
                            public Void execute() {
                                cancel(paymentId);
                                return null;
                            }
                        });
                    }
                    paymentRecord.set(processPayment(paymentId, null));
                } catch (PaymentException e) {
                    throw new UserRuntimeException(i18n.tr("Payment processing has failed!"), e);
                }
                return null;
            }
        });

        if (isCompleteTransactionRequired(paymentRecord)) {
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() throws RuntimeException {
                    paymentRecord.set(completeRealTimePayment(paymentRecord));
                    return null;
                }
            });
        }

        if (paymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.ProcessingReject) {
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() throws RuntimeException {
                    try {
                        processReject(paymentRecord, false);
                    } catch (UserRuntimeException e) {
                        if (cancelOnError) {
                            throw e;
                        }
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public PaymentRecord cancel(PaymentRecord paymentId) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!PaymentRecord.PaymentStatus.cancelable().contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be canceled"));
        }

        PaymentStatus incommingStatus = paymentRecord.paymentStatus().getValue();

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Canceled);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizedDate().setValue(SystemDateManager.getLogicalDate());
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
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizedDate().setValue(SystemDateManager.getLogicalDate());
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
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizedDate().setValue(SystemDateManager.getLogicalDate());
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
    public PaymentRecord processReject(PaymentRecord paymentId, boolean applyNSF) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, paymentId.getPrimaryKey(), AttachLevel.Attached, true);
        if (!EnumSet.of(PaymentRecord.PaymentStatus.ProcessingReject).contains(paymentRecord.paymentStatus().getValue())) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be rejected"));
        }
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Echeck:
        case CreditCard:
            break;
        default:
            throw new UnsupportedOperationException();
        }

        log.debug("Process Reject {} {}", paymentRecord.id().getValue(), paymentRecord.amount().getValue());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizedDate().setValue(SystemDateManager.getLogicalDate());
        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        } catch (ARException e) {
            throw new UserRuntimeException(i18n.tr("Failed to post payment to AR while processing payment"), e);
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

    @Override
    public List<PaymentRecord> getLatestPaymentActivity(BillingAccount billingAccount) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        LogicalDate dateFrom = new LogicalDate(cal.getTime());
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().billingAccount(), billingAccount);
        criteria.gt(criteria.proto().receivedDate(), dateFrom);
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

    @Override
    public PaymentPostingBatch createPostingBatch(Building buildingId, LogicalDate receiptDate) {
        return new MoneyInBatchManager().createPostingBatch(buildingId, receiptDate);
    }

    @Override
    public void cancelPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress) {
        new MoneyInBatchManager().cancelPostingBatch(paymentPostingBatchId, progress);
    }

    @Override
    public void processPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress) {
        new MoneyInBatchManager().processPostingBatch(paymentPostingBatchId, progress);
    }
}
