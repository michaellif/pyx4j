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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.pmc.Pmc;
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

        Set<String> vistaAccountsTerminalId = loadVistaTerminalId();

        Set<Pmc> pmcCount = new HashSet<>();
        Map<String, CardsReconciliationRecord> recordsByMid = new HashMap<>();

        for (CardsReconciliationMerchantTotalRecord merchantTotal : reconciliationFile.merchantTotals()) {
            CardsReconciliationRecord reconciliationRecord = recordsByMid.get(key(merchantTotal));
            if (reconciliationRecord == null) {
                reconciliationRecord = EntityFactory.create(CardsReconciliationRecord.class);
                reconciliationRecord.status().setValue(CardsReconciliationRecordProcessingStatus.Received);
                reconciliationRecord.date().setValue(merchantTotal.date().getValue());
                reconciliationRecord.merchantID().setValue(merchantTotal.merchantID().getValue());
                reconciliationRecord.merchantTerminalId().setValue(merchantTotal.terminalID().getValue());
                reconciliationRecord.fileMerchantTotal().set(fileMerchantTotal);
                reconciliationRecord.fileCardTotal().set(fileCardTotal);

                if (!vistaAccountsTerminalId.contains(reconciliationRecord.merchantTerminalId().getValue())) {
                    EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                    OrCriterion or = criteria.or();
                    or.left().eq(criteria.proto().terminalId(), reconciliationRecord.merchantTerminalId());
                    or.right().eq(criteria.proto().terminalIdConvFee(), reconciliationRecord.merchantTerminalId());
                    PmcMerchantAccountIndex macc = Persistence.service().retrieve(criteria);
                    if (macc == null) {
                        throw new Error("Unexpected TerminalId '" + reconciliationRecord.merchantTerminalId().getValue() + "' in file "
                                + reconciliationFile.fileNameMerchantTotal().getValue());
                    }
                    reconciliationRecord.merchantAccount().set(macc);
                    if (macc.terminalIdConvFee().equals(reconciliationRecord.merchantTerminalId())) {
                        reconciliationRecord.convenienceFeeAccount().setValue(true);
                    } else {
                        reconciliationRecord.convenienceFeeAccount().setValue(false);
                    }

                    if (pmcCount.add(macc.pmc())) {
                        executionMonitor.addInfoEvent("Pmc", BigDecimal.ONE, null);
                    }
                }

                {
                    EntityQueryCriteria<CardsReconciliationRecord> criteria = EntityQueryCriteria.create(CardsReconciliationRecord.class);
                    criteria.eq(criteria.proto().date(), reconciliationRecord.date());
                    criteria.eq(criteria.proto().merchantTerminalId(), reconciliationRecord.merchantTerminalId());
                    if (Persistence.service().count(criteria) > 0) {
                        throw new Error("Duplicate reconciliation record received " + reconciliationRecord.getStringView());
                    }
                }

                recordsByMid.put(key(merchantTotal), reconciliationRecord);
            }

            switch (merchantTotal.type().getValue()) {
            case Deposit:
                Validate.isTrue(merchantTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {0}", merchantTotal);
                Validate.isTrue(reconciliationRecord.totalDeposit().isNull(), "Duplicate Deposit {0}", merchantTotal);
                reconciliationRecord.totalDeposit().setValue(merchantTotal.credit().getValue());
                executionMonitor.addProcessedEvent("Merchant Deposit", reconciliationRecord.totalDeposit().getValue());
                break;
            case Fees:
                Validate.isTrue(merchantTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {0}", merchantTotal);
                Validate.isTrue(reconciliationRecord.totalFee().isNull(), "Duplicate Fees {0}", merchantTotal);
                reconciliationRecord.totalFee().setValue(merchantTotal.debit().getValue());
                executionMonitor.addProcessedEvent("Merchant Fee", reconciliationRecord.totalFee().getValue());
                break;
            case Adjustment:
                reconciliationRecord.adjustments().add(asCredit(merchantTotal));
                break;
            case Chargeback:
                reconciliationRecord.chargebacks().add(asCredit(merchantTotal));
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
                Validate.isTrue(cardTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {0}", cardTotal);
                Validate.isTrue(record.visaDeposit().isNull(), "Duplicate VisaDeposit {0}", cardTotal);
                record.visaDeposit().setValue(cardTotal.credit().getValue());
                break;
            case VisaFees:
                Validate.isTrue(cardTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {0}", cardTotal);
                Validate.isTrue(record.visaFee().isNull(), "Duplicate VisaFees {0}", cardTotal);
                record.visaFee().setValue(cardTotal.debit().getValue());
                break;
            case MastercardDeposit:
                Validate.isTrue(cardTotal.debit().getValue().compareTo(BigDecimal.ZERO) == 0, "Debit ZERO expected for {0}", cardTotal);
                Validate.isTrue(record.mastercardDeposit().isNull(), "Duplicate MastercardDeposit {0}", cardTotal);
                record.mastercardDeposit().setValue(cardTotal.credit().getValue());
                break;
            case MastercardFees:
                Validate.isTrue(cardTotal.credit().getValue().compareTo(BigDecimal.ZERO) == 0, "Credit ZERO expected for {0}", cardTotal);
                Validate.isTrue(record.mastercardFee().isNull(), "Duplicate MastercardFees {0}", cardTotal);
                record.mastercardFee().setValue(cardTotal.debit().getValue());
                break;
            case Adjustment:
                Validate.isTrue(record.adjustments().contains(asCredit(cardTotal)), "Adjustment mismatch to total for {0}", cardTotal);
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

    static Set<String> loadVistaTerminalId() {
        Set<String> vistaAccountsTerminalId = new HashSet<>();
        vistaAccountsTerminalId.add(ServerSideFactory.create(Vista2PmcFacade.class).getVistaMerchantTerminalId());
        vistaAccountsTerminalId.add(ServerSideFactory.create(Vista2PmcFacade.class).getTenantSureMerchantTerminalId());
        //TODO figure it out
        vistaAccountsTerminalId.add("PRVHIGH4");
        return vistaAccountsTerminalId;
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
