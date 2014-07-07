/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 7, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.Charge;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiPayment;

public class YardiLeaseFinancialProcessor {

    private final static Logger log = LoggerFactory.getLogger(YardiLeaseFinancialProcessor.class);

    private final I18n i18n = I18n.get(YardiLeaseFinancialProcessor.class);

    private final ExecutionMonitor executionMonitor;

    public YardiLeaseFinancialProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        assert (executionMonitor != null);
    }

    /*
     * Updates charges and payments
     */
    public void processLease(RTCustomer rtCustomer, Key yardiInterfaceId) throws YardiServiceException {
        BillingAccount account = new YardiChargeProcessor().getAccount(yardiInterfaceId, rtCustomer);

        new YardiChargeProcessor().removeOldCharges(account);
        new YardiPaymentProcessor().removeOldPayments(account);

        log.info("        Importing Lease Transactions:", rtCustomer.getCustomerID());

        if (rtCustomer.getRTServiceTransactions() != null) {
            if (rtCustomer.getRTServiceTransactions().getTransactions().isEmpty()) {
                log.info("          No Transactions for Lease customerId={} ", rtCustomer.getCustomerID());
            }

            LeaseFinancialStats stats = new LeaseFinancialStats();

            for (final Transactions tr : rtCustomer.getRTServiceTransactions().getTransactions()) {
                if (tr != null) {
                    if (tr.getCharge() != null) {
                        processCharge(account, tr.getCharge(), stats);
                    }
                    if (tr.getPayment() != null) {
                        processPayment(account, tr.getPayment(), stats);
                    }
                }
            }

            executionMonitor.addProcessedEvent("Charges", stats.getCharges());
            executionMonitor.addProcessedEvent("Payments", stats.getPayments());
            executionMonitor.addProcessedEvent("Transactions");

        } else {
            log.info("          No RT Service Transactions Received for Lease customerId={} ", rtCustomer.getCustomerID());
        }
    }

    private void processCharge(BillingAccount account, Charge chargeIn, LeaseFinancialStats stats) {
        ChargeDetail detail = chargeIn.getDetail();

        BigDecimal amountPaid = new BigDecimal(detail.getAmountPaid());
        BigDecimal balanceDue = new BigDecimal(detail.getBalanceDue());
        BigDecimal amount = amountPaid.add(balanceDue);

        createCharge(account, detail, stats);

        // for a partially paid charge add fully consumed credit for the amount paid
        if (amount.compareTo(BigDecimal.ZERO) > 0 && amountPaid.compareTo(BigDecimal.ZERO) > 0) {

            detail.setAmount("-" + detail.getAmountPaid()); // negate amount
            detail.setBalanceDue("0.00"); // translates to fully consumed credit
            detail.setAmountPaid(detail.getAmount()); // ensure balance
            detail.setDescription(i18n.tr("{0} amount paid", detail.getDescription()));

            createCharge(account, detail, stats);
        }
    }

    private void createCharge(BillingAccount account, ChargeDetail detail, LeaseFinancialStats stats) {
        InvoiceLineItem charge = YardiARIntegrationAgent.createCharge(account, detail);
        Persistence.service().persist(charge);

        stats.addCharge(charge.amount().getValue());
        log.info("          Created charge (transactionId={}, chargePk={}, amount={})", detail.getTransactionID(), charge.id().getValue(), charge.amount()
                .getValue());
    }

    private void processPayment(BillingAccount account, Payment paymentIn, LeaseFinancialStats stats) {
        YardiPayment payment = YardiARIntegrationAgent.createPayment(account, paymentIn);
        Persistence.service().persist(payment);

        stats.addPayment(payment.amount().getValue());
        log.info("          Created payment (transactionId={}, amount={}) ", paymentIn.getDetail().getTransactionID(), payment.amount().getValue());
    }

    private class LeaseFinancialStats {
    
        private BigDecimal chargesAmount = BigDecimal.ZERO;
    
        private BigDecimal paymentsAmount = BigDecimal.ZERO;
    
        public void addCharge(BigDecimal payment) {
            this.chargesAmount = chargesAmount.add(payment);
        }
    
        public void addPayment(BigDecimal payment) {
            this.paymentsAmount = paymentsAmount.add(payment);
        }
    
        public BigDecimal getCharges() {
            return chargesAmount;
        }
    
        public BigDecimal getPayments() {
            return paymentsAmount;
        }
    }
}
