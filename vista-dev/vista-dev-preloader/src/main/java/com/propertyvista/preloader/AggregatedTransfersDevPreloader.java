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

import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentType;

public class AggregatedTransfersDevPreloader extends BaseVistaDevDataPreloader {

    private static final Logger log = LoggerFactory.getLogger(AggregatedTransfersDevPreloader.class);

    @Override
    public String create() {

        // Retrieve payment records
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Received);
        criteria.asc(criteria.proto().paymentMethod().type());
        criteria.asc(criteria.proto().createdDate());

        List<PaymentRecord> eChequePayments = Persistence.service().query(criteria);
        log.info("There are {} payments of type 'eCheque'", eChequePayments.size());

        AggregatedTransfer aggregatedTransfer = null;
        for (PaymentRecord paymentRecord : Persistence.service().query(criteria)) {
            boolean createNew = (aggregatedTransfer == null) || belongsTo(paymentRecord, aggregatedTransfer);
            if (createNew) {
                if (aggregatedTransfer != null) {
                    // Save updated values.
                    Persistence.service().persist(aggregatedTransfer);
                }
                aggregatedTransfer = createAggregatedTransfer(paymentRecord);
                Persistence.service().persist(aggregatedTransfer);
            }
            addPaymentRecord(aggregatedTransfer, paymentRecord);

        }

        return null;
    }

    private boolean belongsTo(PaymentRecord paymentRecord, AggregatedTransfer aggregatedTransfer) {
        if (!aggregatedTransfer.paymentDate().equals(paymentRecord.receivedDate())) {
            return false;
        }
        if ((aggregatedTransfer.fundsTransferType().getValue() == FundsTransferType.Cards)
                && (!paymentRecord.paymentMethod().type().getValue().equals(PaymentType.CreditCard))) {
            return false;
        }
        return true;
    }

    private AggregatedTransfer createAggregatedTransfer(PaymentRecord paymentRecord) {
        AggregatedTransfer at;
        if (paymentRecord.paymentMethod().type().getValue().equals(PaymentType.CreditCard)) {
            at = createCardAggregatedTransfer(paymentRecord);
        } else {
            at = createEftAggregatedTransfer(paymentRecord);
        }
        at.status().setValue(AggregatedTransferStatus.Paid);
        at.paymentDate().setValue(paymentRecord.receivedDate().getValue());
        at.merchantAccount().set(paymentRecord.merchantAccount());

        at.grossPaymentCount().setValue(0);
        at.grossPaymentAmount().setValue(BigDecimal.ZERO);
        at.grossPaymentFee().setValue(BigDecimal.ZERO);
        at.netAmount().setValue(BigDecimal.ZERO);

        Persistence.service().persist(at);
        return at;
    }

    public EftAggregatedTransfer createEftAggregatedTransfer(PaymentRecord paymentRecord) {
        EftAggregatedTransfer at = EntityFactory.create(EftAggregatedTransfer.class);
        at.fundsTransferType().setValue(FundsTransferType.PreAuthorizedDebit);

        at.rejectItemsAmount().setValue(BigDecimal.ZERO);
        at.rejectItemsFee().setValue(BigDecimal.ZERO);
        at.rejectItemsCount().setValue(0);

        at.returnItemsAmount().setValue(BigDecimal.ZERO);
        at.returnItemsFee().setValue(BigDecimal.ZERO);
        at.returnItemsCount().setValue(0);

        //at.previousBalance().setValue(BigDecimal.ZERO);
        //at.merchantBalance().setValue(BigDecimal.ZERO);
        //at.fundsReleased().setValue(BigDecimal.ZERO);

        return at;
    }

    public CardsAggregatedTransfer createCardAggregatedTransfer(PaymentRecord paymentRecord) {
        CardsAggregatedTransfer at = EntityFactory.create(CardsAggregatedTransfer.class);
        at.fundsTransferType().setValue(FundsTransferType.Cards);

        at.visaDeposit().setValue(BigDecimal.ZERO);
        at.visaFee().setValue(BigDecimal.ZERO);
        at.mastercardDeposit().setValue(BigDecimal.ZERO);
        at.mastercardFee().setValue(BigDecimal.ZERO);

        return at;
    }

    private void addPaymentRecord(AggregatedTransfer at, PaymentRecord paymentRecord) {
        at.netAmount().setValue(at.netAmount().getValue().add(paymentRecord.amount().getValue()));
        at.grossPaymentCount().setValue(at.grossPaymentCount().getValue() + 1);
        at.grossPaymentAmount().setValue(at.grossPaymentAmount().getValue().add(paymentRecord.amount().getValue()));
        if (paymentRecord.paymentMethod().type().getValue().equals(PaymentType.CreditCard)) {
            updateCardsTotals((CardsAggregatedTransfer) at, paymentRecord);
        }

        paymentRecord.aggregatedTransfer().set(at);
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        Persistence.service().persist(paymentRecord);
    }

    private void updateCardsTotals(CardsAggregatedTransfer at, PaymentRecord paymentRecord) {
        switch (paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue()) {
        case MasterCard:
            at.mastercardDeposit().setValue(at.mastercardDeposit().getValue().add(paymentRecord.amount().getValue()));
            break;
        case Visa:
        case VisaDebit:
            at.visaDeposit().setValue(at.visaDeposit().getValue().add(paymentRecord.amount().getValue()));
            break;
        }
    }

    @Override
    public String delete() {
        return null;
    }

}
