/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 3, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

public class AggregatedTransfersDevPreloader extends BaseVistaDevDataPreloader {

    private static final Logger log = LoggerFactory.getLogger(AggregatedTransfersDevPreloader.class);

    @Override
    public String create() {

        // TODO group by By Lease, By Type and By Day. Question = how to group by with your framework?
        // Retrieve Echeck payment records
        EntityQueryCriteria<PaymentRecord> eCheckePaymentsCriteria = EntityQueryCriteria.create(PaymentRecord.class);
        eCheckePaymentsCriteria.eq(eCheckePaymentsCriteria.proto().paymentMethod().type(), PaymentType.Echeck);
        eCheckePaymentsCriteria.asc(eCheckePaymentsCriteria.proto().createdDate());
        List<PaymentRecord> eChequePayments = Persistence.service().query(eCheckePaymentsCriteria);
        log.info("There are {} payments of type 'eCheque'", eChequePayments.size());

        // Retrieve Credit Card records
        EntityQueryCriteria<PaymentRecord> cardPaymentsCriteria = EntityQueryCriteria.create(PaymentRecord.class);
        cardPaymentsCriteria.eq(cardPaymentsCriteria.proto().paymentMethod().type(), PaymentType.CreditCard);
        cardPaymentsCriteria.asc(cardPaymentsCriteria.proto().createdDate());
        List<PaymentRecord> creditCardPayments = Persistence.service().query(cardPaymentsCriteria);
        log.info("There are {} payments of type 'CreditCard'", creditCardPayments.size());

        // Based on results above, create a couple of Aggregated Transfers which first 2 payment records
        // (they have same lease, same date and same payment type)
        EftAggregatedTransfer eftAggTransfer = createEftAggregatedTransfer(eChequePayments.subList(0, 2));
        Persistence.service().persist(eftAggTransfer);

        EftAggregatedTransfer cardAggTransfer = createEftAggregatedTransfer(creditCardPayments.subList(0, 2));
        Persistence.service().persist(cardAggTransfer);

        return "Aggregated transfers preloaded";
    }

    public EftAggregatedTransfer createEftAggregatedTransfer(List<PaymentRecord> payments) {

        // TODO FILL WITH REALISTIC DATA

        BigDecimal netAmount = new BigDecimal(0.0);

        // Any values must be aggregated before add to Aggregated Transfer??
        for (PaymentRecord payment : payments) {
            netAmount.add(payment.amount().getValue());
        }

        EftAggregatedTransfer at = EntityFactory.create(EftAggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Paid);
        at.merchantAccount().set(payments.get(0).merchantAccount());
        at.fundsTransferType().setValue(FundsTransferType.DirectBankingPayment);
        at.paymentDate().set(payments.get(0).receivedDate());
        at.grossPaymentAmount().set(payments.get(0).amount());
        at.grossPaymentFee().set(payments.get(0).convenienceFee());

        at.payments().addAll(payments);
        at.netAmount().setValue(netAmount);

        return at;
    }

    public CardsAggregatedTransfer createCardAggregatedTransfer(List<PaymentRecord> payments) {

        // TODO FILL WITH REALISTIC DATA

        BigDecimal netAmount = new BigDecimal(0.0);

        // Any values must be aggregated before add to Aggregated Transfer??
        for (PaymentRecord payment : payments) {
            netAmount.add(payment.amount().getValue());
        }

        CardsAggregatedTransfer at = EntityFactory.create(CardsAggregatedTransfer.class);
        at.fundsTransferType().setValue(FundsTransferType.Cards);
        at.status().setValue(AggregatedTransferStatus.Paid);
        at.paymentDate().setValue(payments.get(0).receivedDate().getValue());
        at.merchantAccount().set(payments.get(0).merchantAccount());
        at.grossPaymentCount().setValue(0);
        at.grossPaymentAmount().setValue(BigDecimal.ZERO);
        at.netAmount().setValue(netAmount);
        at.grossPaymentFee().setValue(payments.get(0).convenienceFee().getValue(BigDecimal.ZERO));

        at.payments().addAll(payments);

        // Card Specific Data
//        at.visaDeposit().setValue(payments.get(0). getValue(BigDecimal.ZERO));
//        at.visaFee().setValue(reconciliationRecord.visaFee().getValue(BigDecimal.ZERO));
//        at.mastercardDeposit().setValue(reconciliationRecord.mastercardDeposit().getValue(BigDecimal.ZERO));
//        at.mastercardFee().setValue(reconciliationRecord.mastercardFee().getValue(BigDecimal.ZERO));

        return at;
    }

    @Override
    public String delete() {
        return null;
    }

}
