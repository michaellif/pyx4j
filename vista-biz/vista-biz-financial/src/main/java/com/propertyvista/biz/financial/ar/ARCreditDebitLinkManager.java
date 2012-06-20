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
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceCharge;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
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
public class ARCreditDebitLinkManager {

    private static final I18n i18n = I18n.get(ARPaymentProcessor.class);

    static InvoiceCredit consumeCredit(InvoiceCredit credit) {
        List<InvoiceDebit> debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(credit.billingAccount());
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
    static void consumeCredit(InvoiceCredit credit, InvoiceDebit debit, BigDecimal amount) {
        // TODO Auto-generated method stub

    }

    static void deleteHardLink(DebitCreditLink link) {
        // TODO Auto-generated method stub

    }

    static InvoiceDebit coverDebit(InvoiceDebit debit) {
        List<InvoiceCredit> credits = ARTransactionManager.getNotConsumedCreditInvoiceLineItems(debit.billingAccount());
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

    static void declinePayment(InvoicePaymentBackOut backOut) {
        InvoicePayment invoicePaymentToReturn = ARTransactionManager.getCorrespodingCreditByPayment(backOut.billingAccount(), backOut.paymentRecord());
        if (invoicePaymentToReturn.isEmpty()) {
            throw new BillingException(i18n.tr("Cannot find Payment Record"));
        } else {
            Persistence.service().retrieve(invoicePaymentToReturn.debitLinks());
            if (!invoicePaymentToReturn.debitLinks().isEmpty()) {
                for (DebitCreditLink link : invoicePaymentToReturn.debitLinks()) {
                    InvoiceDebit debit = link.debitItem().cast();
                    debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(link.amount().getValue()));
                    Persistence.service().persist(debit);
                    Persistence.service().delete(link);
                }
                invoicePaymentToReturn.debitLinks().clear();
                Persistence.service().persist(invoicePaymentToReturn);
            }
        }
        List<InvoiceCredit> credits = ARTransactionManager.getSuccedingCreditInvoiceLineItems(backOut.billingAccount(), invoicePaymentToReturn);
        if (credits != null && credits.size() > 0) {
            for (InvoiceCredit credit : credits) {
                Persistence.service().retrieve(credit.debitLinks());
                boolean hardLink = false;
                if (!credit.debitLinks().isEmpty()) {
                    for (DebitCreditLink link : credit.debitLinks()) {
                        if (link.hardLink().isBooleanTrue()) {
                            hardLink = true;
                            break;
                        }
                        InvoiceDebit debit = link.debitItem().cast();
                        debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(link.amount().getValue()));
                        Persistence.service().persist(debit);
                        Persistence.service().delete(link);
                    }
                    if (!hardLink) {
                        credit.debitLinks().clear();
                        credit.outstandingCredit().setValue(credit.amount().getValue());
                        Persistence.service().persist(credit);
                    }
                }
            }
            // Pay available debits by Succeeding credits
            for (InvoiceCredit credit : credits) {
                consumeCredit(credit);
            }
        }
    }
}
