/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceFile;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecord;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportRecordType;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;

class CardsDailyReportAcceptor {

    private final ExecutionMonitor executionMonitor;

    CardsDailyReportAcceptor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public void validateAndPersistFile(DailyReportTO dailyReportFile) {
        {
            EntityQueryCriteria<CardsClearanceFile> criteria = EntityQueryCriteria.create(CardsClearanceFile.class);
            criteria.eq(criteria.proto().fileName(), dailyReportFile.fileName());
            if (Persistence.service().count(criteria) > 0) {
                throw new Error("Duplicate reconciliation file received " + dailyReportFile.fileName().getValue());
            }
        }

        CardsClearanceFile clearanceFile = EntityFactory.create(CardsClearanceFile.class);
        clearanceFile.fileName().setValue(dailyReportFile.fileName().getValue());
        clearanceFile.remoteFileDate().setValue(dailyReportFile.remoteFileDate().getValue());
        Persistence.service().persist(clearanceFile);

        Set<String> vistaAccountsTerminalId = loadVistaTerminalId();
        Set<Pmc> pmcCount = new HashSet<>();

        for (DailyReportRecord toRecord : dailyReportFile.records()) {
            if ((toRecord.transactionType().getValue() == DailyReportRecordType.SALE) || (toRecord.transactionType().getValue() == DailyReportRecordType.PRCO)) {
                CardsClearanceRecord record = EntityFactory.create(CardsClearanceRecord.class);
                record.file().set(clearanceFile);
                record.status().setValue(CardsClearanceRecordProcessingStatus.Received);
                record.merchantID().setValue(toRecord.terminalID().getValue());

                if (!vistaAccountsTerminalId.contains(toRecord.terminalID().getValue())) {

                    EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                    OrCriterion or = criteria.or();
                    or.left().eq(criteria.proto().terminalId(), toRecord.terminalID());
                    or.right().eq(criteria.proto().terminalIdConvFee(), toRecord.terminalID());
                    PmcMerchantAccountIndex macc = Persistence.service().retrieve(criteria);
                    if (macc == null) {
                        throw new Error("Unexpected TerminalId '" + toRecord.terminalID().getValue() + "' in file " + dailyReportFile.fileName().getValue());
                    }
                    record.merchantAccount().set(macc);
                    if (macc.terminalIdConvFee().equals(toRecord.terminalID())) {
                        record.convenienceFeeAccount().setValue(true);
                    } else {
                        record.convenienceFeeAccount().setValue(false);
                    }

                    if (pmcCount.add(macc.pmc())) {
                        executionMonitor.addInfoEvent("Pmc", BigDecimal.ONE, null);
                    }
                    executionMonitor.addProcessedEvent("CardClearance", record.amount().getValue());
                }

                record.clearanceDate().setValue(toRecord.date().getValue());
                record.referenceNumber().setValue(toRecord.referenceNumber().getValue());
                record.amount().setValue(toRecord.amount().getValue());
                Persistence.service().persist(record);
            }
        }
    }

    private Set<String> loadVistaTerminalId() {
        Set<String> vistaAccountsTerminalId = new HashSet<>();
        vistaAccountsTerminalId.add(ServerSideFactory.create(Vista2PmcFacade.class).getVistaMerchantTerminalId());
        vistaAccountsTerminalId.add(ServerSideFactory.create(Vista2PmcFacade.class).getTenantSureMerchantTerminalId());
        return vistaAccountsTerminalId;
    }

}
