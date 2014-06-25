/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Validate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationFile;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;

class CardsReconciliationAcceptor {

    private final ExecutionMonitor executionMonitor;

    CardsReconciliationAcceptor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public void validateAndPersistFile(CardsReconciliationTO reconciliationFile) {
        {
            EntityQueryCriteria<CardsReconciliationFile> criteria = EntityQueryCriteria.create(CardsReconciliationFile.class);
            criteria.eq(criteria.proto().fileName(), reconciliationFile.fileNameMerchantTotal());
            if (Persistence.service().count(criteria) > 0) {
                throw new Error("Duplicate reconciliation file received " + reconciliationFile.fileNameMerchantTotal().getValue());
            }
        }
        {
            EntityQueryCriteria<CardsReconciliationFile> criteria = EntityQueryCriteria.create(CardsReconciliationFile.class);
            criteria.eq(criteria.proto().fileName(), reconciliationFile.fileNameCardTotal());
            if (Persistence.service().count(criteria) > 0) {
                throw new Error("Duplicate reconciliation file received " + reconciliationFile.fileNameCardTotal().getValue());
            }
        }

        CardsReconciliationFile fileMerchantTotal = EntityFactory.create(CardsReconciliationFile.class);
        fileMerchantTotal.fileName().setValue(reconciliationFile.fileNameMerchantTotal().getValue());
        fileMerchantTotal.remoteFileDate().setValue(reconciliationFile.remoteFileDateMerchantTotal().getValue());
        Persistence.service().persist(fileMerchantTotal);

        CardsReconciliationFile fileCardTotal = EntityFactory.create(CardsReconciliationFile.class);
        fileCardTotal.fileName().setValue(reconciliationFile.fileNameCardTotal().getValue());
        fileCardTotal.remoteFileDate().setValue(reconciliationFile.remoteFileDateCardTotal().getValue());
        Persistence.service().persist(fileCardTotal);

        Map<String, CardsReconciliationRecord> recordsByMid = new HashMap<>();

        for (CardsReconciliationMerchantTotalRecord merchantTotal : reconciliationFile.merchantTotals()) {
            CardsReconciliationRecord record = recordsByMid.get(key(merchantTotal));
            if (record == null) {
                record = EntityFactory.create(CardsReconciliationRecord.class);
                record.status().setValue(CardsReconciliationRecordProcessingStatus.Received);
                record.date().setValue(merchantTotal.date().getValue());
                record.merchantID().setValue(merchantTotal.merchantID().getValue());
                record.merchantTerminalId().setValue(merchantTotal.terminalID().getValue());
                record.fileMerchantTotal().set(fileMerchantTotal);
                record.fileCardTotal().set(fileCardTotal);

                {
                    EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                    criteria.eq(criteria.proto().merchantTerminalId(), record.merchantTerminalId());
                    PmcMerchantAccountIndex macc = Persistence.service().retrieve(criteria);
                    if (macc == null) {
                        throw new Error("Unexpected TerminalId '" + record.merchantTerminalId().getValue() + "' in file "
                                + reconciliationFile.fileNameMerchantTotal().getValue());
                    }
                    record.merchantAccount().set(macc);
                }

                {
                    EntityQueryCriteria<CardsReconciliationRecord> criteria = EntityQueryCriteria.create(CardsReconciliationRecord.class);
                    criteria.eq(criteria.proto().date(), record.date());
                    criteria.eq(criteria.proto().merchantAccount(), record.merchantAccount());
                    if (Persistence.service().count(criteria) > 0) {
                        throw new Error("Duplicate reconciliation record received " + record.getStringView());
                    }
                }

                recordsByMid.put(key(merchantTotal), record);
            }

            switch (merchantTotal.type().getValue()) {
            case Deposit:
                Validate.isTrue(merchantTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {}", merchantTotal);
                Validate.isTrue(record.totalDeposit().isNull(), "Duplicate Deposit {}", merchantTotal);
                record.totalDeposit().setValue(merchantTotal.credit().getValue());
                executionMonitor.addProcessedEvent("Merchant Deposit", record.totalDeposit().getValue());
                break;
            case Fees:
                Validate.isTrue(merchantTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {}", merchantTotal);
                Validate.isTrue(record.totalFee().isNull(), "Duplicate Fees {}", merchantTotal);
                record.totalFee().setValue(merchantTotal.debit().getValue());
                executionMonitor.addProcessedEvent("Merchant Fee", record.totalFee().getValue());
                break;
            case Adjustment:
                record.adjustments().add(asCredit(merchantTotal));
                break;
            case Chargeback:
                record.chargebacks().add(asCredit(merchantTotal));
                break;
            default:
                throw new IllegalArgumentException();
            }
        }

        for (CardsReconciliationCardTotalRecord cardTotal : reconciliationFile.cardTotals()) {
            CardsReconciliationRecord record = recordsByMid.get(key(cardTotal));
            if (record == null) {
                throw new Error("Details exists but no totals for TerminalId '" + cardTotal.terminalID().getValue() + "' and date in file "
                        + reconciliationFile.fileNameCardTotal().getValue());
            }

            switch (cardTotal.type().getValue()) {
            case VisaDeposit:
                Validate.isTrue(cardTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {}", cardTotal);
                Validate.isTrue(record.totalDeposit().isNull(), "Duplicate VisaDeposit {}", cardTotal);
                record.visaDeposit().setValue(cardTotal.credit().getValue());
                break;
            case VisaFees:
                Validate.isTrue(cardTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {}", cardTotal);
                Validate.isTrue(record.visaFee().isNull(), "Duplicate VisaFees {}", cardTotal);
                record.visaFee().setValue(cardTotal.debit().getValue());
                break;
            case MastercardDeposit:
                Validate.isTrue(cardTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {}", cardTotal);
                Validate.isTrue(record.mastercardDeposit().isNull(), "Duplicate MastercardDeposit {}", cardTotal);
                record.visaDeposit().setValue(cardTotal.credit().getValue());
                break;
            case MastercardFees:
                Validate.isTrue(cardTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {}", cardTotal);
                Validate.isTrue(record.mastercardFee().isNull(), "Duplicate MastercardFees {}", cardTotal);
                record.mastercardFee().setValue(cardTotal.debit().getValue());
                break;
            case Adjustment:
                Validate.isTrue(record.adjustments().contains(asCredit(cardTotal)), "Adjustment mismatch to total", cardTotal);
                break;
            case Chargeback:
                Validate.isTrue(record.chargebacks().contains(asCredit(cardTotal)), "Chargeback mismatch to total", cardTotal);
                break;
            default:
                throw new IllegalArgumentException();
            }
        }

        Persistence.service().persist(recordsByMid.values());
    }

    private BigDecimal asCredit(CardsReconciliationMerchantTotalRecord record) {
        return record.credit().getValue().subtract(record.debit().getValue());
    }

    private BigDecimal asCredit(CardsReconciliationCardTotalRecord record) {
        return record.credit().getValue().subtract(record.debit().getValue());
    }

    private String key(CardsReconciliationMerchantTotalRecord record) {
        return record.terminalID().getValue() + record.date().getStringView();
    }

    private String key(CardsReconciliationCardTotalRecord record) {
        return record.terminalID().getValue() + record.date().getStringView();
    }
}
