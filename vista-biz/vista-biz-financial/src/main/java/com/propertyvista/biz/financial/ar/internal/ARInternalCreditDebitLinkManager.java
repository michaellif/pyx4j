/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceCharge;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.portal.rpc.shared.BillingException;

/**
 * Default DebitCredit link rule:
 * 
 * CREDITS:
 * 
 * - {@link InvoicePayment} covers {@link InvoiceDeposit}(s), {@link InvoiceProductCharge}(s) of type 'features', {@link InvoiceAccountCharge}(s),
 * {@link InvoiceProductCharge}(s) of type 'service'
 * 
 * - {@link InvoiceProductCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceAccountCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceDepositRefund} covers {@link InvoiceProductCharge}(s) of the same origin (BillableItem) as deposit. If it is still not consumed apply
 * according to {@link InvoicePayment} rules.
 * 
 * DEBITS:
 * 
 * - {@link InvoiceInitialDebit} is covered by any available credit
 * 
 * - {@link InvoicePaymentBackOut} is covered by {@link InvoicePayment} that is originated from same {@link PaymentRecord}
 * 
 * - {@link InvoiceDeposit} is covered by any available credit
 * 
 * - {@link InvoiceWithdrawal} is covered by any credit.
 * 
 * - {@link InvoiceCharge} is covered by any available credit
 * 
 * @author michaellif
 * 
 */
class ARInternalCreditDebitLinkManager {

    private static final I18n i18n = I18n.get(ARInternalPaymentManager.class);

    private ARInternalCreditDebitLinkManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalCreditDebitLinkManager INSTANCE = new ARInternalCreditDebitLinkManager();
    }

    static ARInternalCreditDebitLinkManager instance() {
        return SingletonHolder.INSTANCE;
    }

    InvoiceCredit consumeCredit(InvoiceCredit credit) {

        BillingAccount billingAccount = credit.billingAccount();
        List<InvoiceDebit> debits = ARInternalTransactionManager.instance().getNotCoveredDebitInvoiceLineItems(billingAccount);

        for (InvoiceDebit debit : debits) {

            DebitCreditLink link = EntityFactory.create(DebitCreditLink.class);

            if (debit.outstandingDebit().getValue().compareTo(credit.outstandingCredit().getValue().negate()) > 0) {
                link.amount().setValue(credit.outstandingCredit().getValue().negate());

                debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(credit.outstandingCredit().getValue()));

                credit.outstandingCredit().setValue(BigDecimal.ZERO);
            } else {
                link.amount().setValue(debit.outstandingDebit().getValue());

                credit.outstandingCredit().setValue(credit.outstandingCredit().getValue().add(debit.outstandingDebit().getValue()));

                debit.outstandingDebit().setValue(BigDecimal.ZERO);

            }

            link.debitItem().set(debit);
            credit.debitLinks().add(link);
            link.creditItem().set(credit);

            Persistence.service().persist(link);
            Persistence.service().persist(debit);

            if (credit.outstandingCredit().getValue().compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        Persistence.service().persist(credit);

        return credit;
    }

    /**
     * - Exceptions
     * credit amount - credit hard links >= amount && debit - debit hard links >= amount
     */
    DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {

        DebitCreditLink link = null;
        if (debit.amount().getValue().add(debit.taxTotal().getValue()).compareTo(amount) < 0) {
            throw new BillingException(i18n.tr("Provided amount exceeds Debit"));
        }

        if (paymentRecord.amount().getValue().compareTo(amount) < 0) {
            throw new BillingException(i18n.tr("Provided amount exceeds Payment"));
        }

        InvoicePayment payment = ARInternalTransactionManager.instance().getCorrespodingCreditByPayment(paymentRecord.billingAccount(), paymentRecord);

        // check if amount covers all soft links paid by credit
        BigDecimal sum = BigDecimal.ZERO;
        Collection<DebitCreditLink> links = null;

        links = getDebitCreditLinksByItem(payment);
        if (links != null && links.size() > 0) {
            for (DebitCreditLink link2 : links) {
                if (!link2.hardLink().isBooleanTrue()) {
                    sum = sum.add(link2.amount().getValue());
                }
            }
            if (amount.compareTo(sum) > 0) {
                throw new BillingException(i18n.tr("Payment has been already used to set Hard Link"));
            }
        }

        // check if amount covers outstandingDebit + all soft links used for debit. Pick the first Credit.
        InvoiceCredit firstCredit = payment.cast();

        sum = debit.outstandingDebit().getValue();
        links = getDebitCreditLinksByItem(debit);
        if (links != null && links.size() > 0) {
            if (firstCredit.id().toString().compareTo(links.iterator().next().creditItem().id().toString()) > 0) {
                firstCredit = links.iterator().next().creditItem();
            }
            for (DebitCreditLink link2 : links) {
                if (!link2.hardLink().isBooleanTrue()) {
                    sum = sum.add(link2.amount().getValue());
                }
            }
            if (amount.compareTo(sum) > 0) {
                throw new BillingException(i18n.tr("Debit has been already paid with Hard Link"));
            }
        }

        List<InvoiceCredit> credits = restoreBackwardPayments(payment.billingAccount(), firstCredit, false);

        // pointer to updated credit
        InvoiceCredit hardLinkCredit = null;
        for (InvoiceCredit creditFromList : credits) {
            if (creditFromList.equals(payment)) {
                hardLinkCredit = creditFromList;
                break;
            }
        }

        // hard link for debit already exists. Use it.
        if (!hardLinkCredit.debitLinks().isEmpty()) {
            for (DebitCreditLink linkTemp : hardLinkCredit.debitLinks()) {
                if (linkTemp.debitItem().equals(debit)) {
                    link = linkTemp;
                    break;
                }
            }
        }

        if (link == null) {

            link = EntityFactory.create(DebitCreditLink.class);
            link.amount().setValue(amount);
            link.debitItem().set(debit);
            link.creditItem().set(hardLinkCredit);
            link.hardLink().setValue(true);
            hardLinkCredit.debitLinks().add(link);
        } else {
            link.amount().setValue(link.amount().getValue().add(amount));
        }

        Persistence.service().persist(link);

        hardLinkCredit.outstandingCredit().setValue(hardLinkCredit.outstandingCredit().getValue().add(amount));
        Persistence.service().retrieve(debit);
        debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().subtract(amount));

        Persistence.service().persist(debit);
        Persistence.service().persist(hardLinkCredit);

        for (InvoiceCredit creditFromList : credits) {
            if (creditFromList.outstandingCredit().getValue().compareTo(BigDecimal.ZERO) != 0) {
                consumeCredit(creditFromList);
            }
        }

        return link;
    }

    void removeHardLink(DebitCreditLink link) {
        if (link.hardLink().isBooleanTrue()) {
            link.hardLink().setValue(false);
            Persistence.service().persist(link);
        }
    }

    InvoiceDebit coverDebit(InvoiceDebit debit) {
        List<InvoiceCredit> credits = ARInternalTransactionManager.instance().getNotConsumedCreditInvoiceLineItems(debit.billingAccount());
        for (InvoiceCredit credit : credits) {

            DebitCreditLink link = EntityFactory.create(DebitCreditLink.class);

            if (credit.outstandingCredit().getValue().negate().compareTo(debit.outstandingDebit().getValue()) > 0) {
                link.amount().setValue(debit.outstandingDebit().getValue());

                credit.outstandingCredit().setValue(credit.outstandingCredit().getValue().add(debit.outstandingDebit().getValue()));

                debit.outstandingDebit().setValue(BigDecimal.ZERO);
            } else {
                link.amount().setValue(credit.outstandingCredit().getValue().negate());

                debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(credit.outstandingCredit().getValue()));

                credit.outstandingCredit().setValue(BigDecimal.ZERO);

            }

            link.debitItem().set(debit);
            credit.debitLinks().add(link);
            link.creditItem().set(credit);

            Persistence.service().persist(link);
            Persistence.service().persist(credit);

            if (debit.outstandingDebit().getValue().compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        Persistence.service().persist(debit);

        return debit;
    }

    void declinePayment(InvoicePaymentBackOut backOut) {
        InvoicePayment invoicePaymentToReturn = ARInternalTransactionManager.instance().getCorrespodingCreditByPayment(backOut.billingAccount(),
                backOut.paymentRecord());
        if (invoicePaymentToReturn == null) {
            throw new BillingException(i18n.tr("Cannot find Payment Record"));
        }

        List<InvoiceCredit> credits = restoreBackwardPayments(backOut.billingAccount(), invoicePaymentToReturn, true);

        // Pay available debits by Succeeding credits. Skip the first one
        for (InvoiceCredit credit : credits) {
            if (!credits.get(0).equals(credit)) {
                consumeCredit(credit);
            }
        }
    }

    private List<InvoiceCredit> restoreBackwardPayments(BillingAccount billingAccount, InvoiceCredit creditStartPoint, boolean skipFirst) {

        Collection<DebitCreditLink> itemsToRemove = new ArrayList<DebitCreditLink>();
        List<InvoiceCredit> credits = ARInternalTransactionManager.instance().getSuccedingCreditInvoiceLineItems(billingAccount, creditStartPoint);
        if (credits != null && credits.size() > 0) {
            for (InvoiceCredit credit : credits) {
                Persistence.service().retrieve(credit.debitLinks());
                if (!credit.debitLinks().isEmpty()) {
                    for (DebitCreditLink link : credit.debitLinks()) {
                        if (!link.hardLink().isBooleanTrue() || (skipFirst == true && credits.get(0).equals(credit))) {
                            InvoiceDebit debit = link.debitItem().cast();
                            debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(link.amount().getValue()));
                            credit.outstandingCredit().setValue(credit.outstandingCredit().getValue().add(link.amount().getValue().negate()));
                            Persistence.service().persist(debit);
                            itemsToRemove.add(link);
                            Persistence.service().delete(link);
                        }
                    }
                    for (DebitCreditLink link : itemsToRemove) {
                        credit.debitLinks().remove(link);
                    }
                    Persistence.service().persist(credit);
                }
            }
        }
        return credits;
    }

    Collection<DebitCreditLink> getDebitCreditLinksByItem(InvoiceLineItem invoiceLineItem) {
        Collection<DebitCreditLink> links;
        {
            EntityQueryCriteria<DebitCreditLink> criteria = EntityQueryCriteria.create(DebitCreditLink.class);
            if (invoiceLineItem.isInstanceOf(InvoiceCredit.class)) {
                criteria.add(PropertyCriterion.eq(criteria.proto().creditItem(), invoiceLineItem));
            } else if (invoiceLineItem.isInstanceOf(InvoiceDebit.class)) {
                criteria.add(PropertyCriterion.eq(criteria.proto().debitItem(), invoiceLineItem));
            }
            criteria.asc(criteria.proto().creditItem().id());
            links = Persistence.service().query(criteria);
        }

        return links;
    }

}
