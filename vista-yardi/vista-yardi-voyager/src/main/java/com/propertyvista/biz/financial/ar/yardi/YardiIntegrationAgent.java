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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Detail;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.PaymentDetailReversal;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiCredit;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.financial.yardi.YardiService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.services.ARCodeAdapter;

public class YardiIntegrationAgent {

    private final static Logger log = LoggerFactory.getLogger(YardiIntegrationAgent.class);

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
    public static YardiBillingAccount getYardiBillingAccount(RTCustomer customer) {
        EntityQueryCriteria<Lease> leaseCrit = EntityQueryCriteria.create(Lease.class);
        leaseCrit.add(PropertyCriterion.eq(leaseCrit.proto().leaseId(), customer.getCustomerID()));
        Lease lease = Persistence.service().retrieve(leaseCrit);
        if (lease == null) {
            // no lease found - quit
            return null;
        }
        EntityQueryCriteria<YardiBillingAccount> accntCrit = EntityQueryCriteria.create(YardiBillingAccount.class);
        accntCrit.add(PropertyCriterion.eq(accntCrit.proto().lease(), lease));
        YardiBillingAccount account = Persistence.service().retrieve(accntCrit);
        if (account == null) {
            // create new account
            account = EntityFactory.create(YardiBillingAccount.class);
            account.lease().set(lease);
            Persistence.service().persist(account);
        }

        return account;
    }

    public static InvoiceLineItem createCharge(YardiBillingAccount account, ChargeDetail detail) {
        BigDecimal amount = new BigDecimal(detail.getAmount());
        // TODO - This calculation assumes that TransactionDate is set to or shortly after start date of cycle
        LogicalDate transactionDate = new LogicalDate(detail.getTransactionDate().getTime());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(account.lease(), transactionDate);
        if (billingCycle == null) {
            throw new IllegalStateException("failed to create charge for yurid account = " + account.getPrimaryKey() + ": billing cycle was not found");
        }
        InvoiceLineItem item = null;
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            YardiCharge charge = EntityFactory.create(YardiCharge.class);
            charge.chargeCode().setValue(detail.getChargeCode());
            charge.arCode().set(new ARCodeAdapter().findARCode(ActionType.Debit, detail.getChargeCode()));
            charge.transactionId().setValue(detail.getTransactionID());
            charge.amountPaid().setValue(new BigDecimal(detail.getAmountPaid()));
            charge.balanceDue().setValue(new BigDecimal(detail.getBalanceDue()));
            charge.outstandingDebit().setValue(new BigDecimal(detail.getAmount()));
            charge.comment().setValue(detail.getComment());
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
            YardiCredit credit = EntityFactory.create(YardiCredit.class);
            credit.arCode().set(new ARCodeAdapter().findARCode(ActionType.Credit, detail.getChargeCode()));
            credit.postDate().setValue(transactionDate);
            item = credit;
        }
        // we don't have postDate
        item.billingAccount().set(account);
        item.amount().setValue(amount);
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
        case EFT:
            return YardiPaymentType.ACH.name();
        case CreditCard:
            return YardiPaymentType.CreditCard.name();
        case Interac:
            return YardiPaymentType.DebitCard.name();
        }
        return null;
    }

    private static void setPaymentInfo(Detail detail, PaymentRecord pr, Lease lease) {
        Persistence.ensureRetrieve(pr.paymentMethod().customer(), AttachLevel.Attached);

        detail.setPaidBy(pr.paymentMethod().customer().person().getStringView());
        // info below is used to uniquely identify transaction in yardi
        detail.setCustomerID(lease.leaseId().getValue());
        detail.setPropertyPrimaryID(lease.unit().building().propertyCode().getValue());
        detail.setDocumentNumber(pr.yardiDocumentNumber().getValue());
        detail.setTransactionDate(pr.receivedDate().getValue());
        detail.setAmount(pr.amount().getValue().toString());
    }
}
