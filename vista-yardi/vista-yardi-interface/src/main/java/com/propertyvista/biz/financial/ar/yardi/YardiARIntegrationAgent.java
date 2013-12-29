/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.yardi;

import java.math.BigDecimal;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Detail;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.PaymentDetailReversal;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiCredit;
import com.propertyvista.domain.financial.yardi.YardiDebit;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.financial.yardi.YardiService;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.services.ARCodeAdapter;

public class YardiARIntegrationAgent {

    private final static Logger log = LoggerFactory.getLogger(YardiARIntegrationAgent.class);

    public enum YardiReversalType {
        NSF, Chargeback, Adjustment, Other
    }

    public enum YardiPaymentType {
        CreditCard, DebitCard, ACH, Check, Cash, MoneyOrder, Other
    }

    public enum YardiPaymentChannel {
        Online, Phone, Onsite, Mail, Lockbox, BankBillPayment, Other
    }

    public static String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification() != null ? getPropertyId(propertyID.getIdentification()) : null;
    }

    public static String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryID()) : null;
    }

    public static String getUnitId(RTCustomer customer) {
        return customer.getRTUnit() != null ? customer.getRTUnit().getUnitID() : null;
    }

    /*
     * ChargeProcessor utils
     */
    public static BillingAccount getYardiBillingAccount(final Key yardiInterfaceId, RTCustomer customer) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().leaseId(), customer.getCustomerID());
        criteria.eq(criteria.proto().unit().building().integrationSystemId(), yardiInterfaceId);
        Lease lease = Persistence.service().retrieve(criteria);
        if (lease == null) {
            // no lease found - quit
            return null;
        }
        if (lease.billingAccount().isNull()) {
            // create new account
            BillingAccount account = EntityFactory.create(BillingAccount.class);
            lease.billingAccount().set(account);
            Persistence.service().persist(account);
        }

        return lease.billingAccount();
    }

    public static InvoiceLineItem createCharge(BillingAccount account, ChargeDetail detail) {
        BigDecimal amount = new BigDecimal(detail.getAmount());
        BigDecimal amountPaid = new BigDecimal(detail.getAmountPaid());
        BigDecimal balanceDue = new BigDecimal(detail.getBalanceDue());
        // TODO - This calculation assumes that TransactionDate is set to or shortly after start date of cycle
        LogicalDate transactionDate = new LogicalDate(detail.getTransactionDate().getTime());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(account.lease(), transactionDate);
        if (billingCycle == null) {
            throw new IllegalStateException("failed to create charge for Yardi account = " + account.getPrimaryKey() + ": billing cycle was not found");
        }
        YardiCharge item = null;
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            YardiDebit charge = EntityFactory.create(YardiDebit.class);
            charge.arCode().set(new ARCodeAdapter().findARCode(ActionType.Debit, detail.getChargeCode(), detail.getCustomerID()));
            charge.amountPaid().setValue(amountPaid);
            charge.balanceDue().setValue(balanceDue);
            charge.outstandingDebit().setValue(balanceDue);
            charge.taxTotal().setValue(BigDecimal.ZERO);
            charge.dueDate().setValue(ARYardiTransactionManager.instance().getTransactionDueDate(account, transactionDate));
            if (detail.getService() != null) {
                try {
                    charge.service().type().setValue(YardiService.Type.valueOf(detail.getService().getType()));
                } catch (Exception e) {
                    log.info("ERROR - unknown service type: " + e);
                }
            }
            item = charge;
        } else {
            // got "negative charge" - create a credit item
            YardiCredit credit = EntityFactory.create(YardiCredit.class);
            credit.arCode().set(new ARCodeAdapter().findARCode(ActionType.Credit, detail.getChargeCode(), detail.getCustomerID()));
            credit.postDate().setValue(transactionDate);
            credit.outstandingCredit().setValue(balanceDue);
            item = credit;
        }
        item.chargeCode().setValue(detail.getChargeCode());
        item.comment().setValue(detail.getComment());
        item.transactionId().setValue(detail.getTransactionID());
        item.description().setValue(detail.getDescription());

        // calculate original amount; if differs from the balanceDue an additional credit will be created by the caller
        item.amount().setValue(amountPaid.add(balanceDue));
        // we don't have postDate
        item.billingAccount().set(account);
        item.description().setValue(detail.getDescription());
        item.billingCycle().set(billingCycle);

        return item;
    }

    /*
     * PaymentProcessor utils
     */
    public static YardiPayment createPayment(BillingAccount account, Payment payment) {
        YardiPayment yp = EntityFactory.create(YardiPayment.class);

        yp.billingAccount().set(account);
        Detail detail = payment.getDetail();
        yp.amount().setValue(new BigDecimal(detail.getAmount()).negate());
        yp.description().setValue(detail.getDescription());
        yp.postDate().setValue(new LogicalDate(detail.getTransactionDate().getTime()));
        yp.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(ARCode.Type.Payment));
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(account.lease(), yp.postDate().getValue());
        yp.billingCycle().set(billingCycle);

        return yp;
    }

    public static Payment getPaymentReceipt(YardiReceipt yp) {
        Payment payment = new Payment();
        payment.setType(getPaymentType(yp));
        payment.setChannel(YardiPaymentChannel.Online.name());
        payment.setDetail(getPaymentDetail(yp));
        return payment;
    }

    public static Payment getReceiptReversal(YardiReceiptReversal reversal) {
        Payment payment = new Payment();
        payment.setType(YardiPaymentType.Other.name());
        payment.setDetail(getReceiptReversalDetail(reversal));
        return payment;
    }

    /*
     * Internals
     */
    private static Detail getReceiptReversalDetail(YardiReceiptReversal reversal) {
        Detail detail = new Detail();
        PaymentDetailReversal reversalType = new PaymentDetailReversal();
        if (reversal.applyNSF().isBooleanTrue()) {
            reversalType.setType("NSF");
        } else {
            reversalType.setType("Reverse");
        }
        detail.setReversal(reversalType);
        detail.setReversalDescription(reversal.description().getStringView());
        detail.setDescription(reversal.description().getStringView());
        setPaymentInfo(detail, reversal.paymentRecord(), reversal.billingAccount().lease());
        return detail;
    }

    private static Detail getPaymentDetail(YardiReceipt yp) {
        Detail detail = new Detail();
        detail.setDescription(yp.description().getValue());
        setPaymentInfo(detail, yp.paymentRecord(), yp.billingAccount().lease());
        return detail;
    }

    private static String getPaymentType(YardiReceipt yp) {
        switch (yp.paymentRecord().paymentMethod().type().getValue()) {
        case Cash:
            return YardiPaymentType.Cash.name();
        case Check:
            return YardiPaymentType.Check.name();
        case Echeck:
            return YardiPaymentType.Other.name();
        case DirectBanking:
            return YardiPaymentType.Other.name();
        case CreditCard:
            return YardiPaymentType.Other.name();
        case Interac:
            return YardiPaymentType.Other.name();
        }
        return null;
    }

    private static void setPaymentInfo(Detail detail, PaymentRecord pr, Lease lease) {
        Persistence.ensureRetrieve(pr.paymentMethod().customer(), AttachLevel.Attached);

        String paidBy = pr.paymentMethod().customer().person().getStringView();
        if (paidBy.length() == 0) {
            Persistence.ensureRetrieve(pr.leaseTermParticipant(), AttachLevel.Attached);
            paidBy = pr.leaseTermParticipant().leaseParticipant().participantId().getValue();
        }
        // There is Max length in YArdi table trans.SUSERDEFINED2 nvarchar(42)
        if (paidBy.length() > 24) {
            paidBy = paidBy.substring(0, 24);
        }
        detail.setPaidBy(paidBy);

        // info below is used to uniquely identify transaction in yardi
        detail.setCustomerID(lease.leaseId().getValue());
        detail.setPropertyPrimaryID(lease.unit().building().propertyCode().getValue());
        detail.setDocumentNumber(pr.yardiDocumentNumber().getValue());

        detail.setTransactionDate(pr.receivedDate().getValue());
        //  Payment record targetDate should be used as yardi TransactionDate when it is after receivedDate
        if (EnumSet.of(PaymentType.Cash, PaymentType.Check).contains((pr.paymentMethod().type().getValue()))) {
            if (!pr.targetDate().isNull() && pr.targetDate().getValue().after(pr.receivedDate().getValue())) {
                detail.setTransactionDate(pr.targetDate().getValue());
            }
        }

        detail.setAmount(pr.amount().getValue().toString());
    }
}
