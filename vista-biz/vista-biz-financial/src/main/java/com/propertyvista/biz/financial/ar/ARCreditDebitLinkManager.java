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

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceCarryforwardCredit;
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
 * - {@link InvoiceCarryforwardCredit} behaves same as {@link InvoicePayment}
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

    static InvoiceCredit consumeCredit(InvoiceCredit credit) {
        List<InvoiceDebit> debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(credit.billingAccount());
        for (InvoiceDebit debit : debits) {

            DebitCreditLink link = EntityFactory.create(DebitCreditLink.class);

            if (debit.outstandingDebit().getValue().compareTo(credit.outstandingCredit().getValue().negate()) > 0) {
                link.amount().setValue(credit.outstandingCredit().getValue());

                debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(credit.outstandingCredit().getValue()));

                credit.outstandingCredit().setValue(new BigDecimal("0.00"));
            } else {
                link.amount().setValue(debit.outstandingDebit().getValue());

                credit.outstandingCredit().setValue(credit.outstandingCredit().getValue().add(debit.outstandingDebit().getValue()));

                debit.outstandingDebit().setValue(new BigDecimal("0.00"));

            }

            Persistence.service().persist(link);

            debit.creditLinks().add(link);
            credit.debitLinks().add(link);

            Persistence.service().persist(debit);

            if (credit.outstandingCredit().getValue().compareTo(new BigDecimal("0.00")) == 0) {
                break;
            }
        }

        Persistence.service().persist(credit);

        return credit;
    }

    static void consumeCredit(InvoiceCredit credit, List<InvoiceDebit> debits) {
        // TODO Auto-generated method stub

    }

    static InvoiceDebit coverDebit(InvoiceDebit debit) {
        List<InvoiceCredit> credits = ARTransactionManager.getNotConsumedCreditInvoiceLineItems(debit.billingAccount());
        for (InvoiceCredit credit : credits) {

            DebitCreditLink link = EntityFactory.create(DebitCreditLink.class);

            if (credit.outstandingCredit().getValue().negate().compareTo(debit.outstandingDebit().getValue()) > 0) {
                link.amount().setValue(debit.outstandingDebit().getValue());

                credit.outstandingCredit().setValue(credit.outstandingCredit().getValue().add(debit.outstandingDebit().getValue()));

                debit.outstandingDebit().setValue(new BigDecimal("0.00"));
            } else {
                link.amount().setValue(credit.outstandingCredit().getValue());

                debit.outstandingDebit().setValue(debit.outstandingDebit().getValue().add(credit.outstandingCredit().getValue()));

                credit.outstandingCredit().setValue(new BigDecimal("0.00"));

            }

            Persistence.service().persist(link);

            debit.creditLinks().add(link);
            credit.debitLinks().add(link);

            Persistence.service().persist(credit);

            if (debit.outstandingDebit().getValue().compareTo(new BigDecimal("0.00")) == 0) {
                break;
            }
        }

        Persistence.service().persist(debit);

        return debit;
    }

    static void coverDebit(InvoiceDebit debit, List<InvoiceCredit> credits) {
        // TODO Auto-generated method stub

    }

    static void updateCreditDebitLinks(Bill bill) {
        // TODO Auto-generated method stub

    }

}
