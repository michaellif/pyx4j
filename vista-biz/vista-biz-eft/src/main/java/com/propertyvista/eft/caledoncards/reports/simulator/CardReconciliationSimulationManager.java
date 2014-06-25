/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards.reports.simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableCSVFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.eft.caledoncards.reports.CardsReconciliationReceiveManager;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction.SimpulationTransactionType;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord.CardTotalRecordType;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord.MerchantTotalRecordType;

public class CardReconciliationSimulationManager {

    private static final Logger log = LoggerFactory.getLogger(CardReconciliationSimulationManager.class);

    public String createReports(LogicalDate from, LogicalDate to) {
        EntityQueryCriteria<CardServiceSimulationTransaction> criteria = EntityQueryCriteria.create(CardServiceSimulationTransaction.class);
        criteria.eq(criteria.proto().transactionType(), SimpulationTransactionType.sale);
        criteria.eq(criteria.proto().responseCode(), "0000");
        criteria.eq(criteria.proto().voided(), Boolean.FALSE);
        criteria.ge(criteria.proto().transactionDate(), from);
        criteria.lt(criteria.proto().transactionDate(), DateUtils.addDays(to, +1));

        final Map<String, CardServiceSimulationReconciliationRecord> records = new HashMap<>();

        for (CardServiceSimulationTransaction transaction : Persistence.service().query(criteria)) {
            CardServiceSimulationReconciliationRecord record = records.get(key(transaction));
            if (record == null) {
                record = createReportRecord(transaction.transactionDate().getValue(), transaction.merchant());
                records.put(key(transaction), record);
            }
            addTransaction(record, transaction);

        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                createReportFiles(records.values());
                Persistence.service().persist(records.values());
                return null;
            }
        });

        return "Created " + records.values().size() + " record(s)";

    }

    private String key(CardServiceSimulationTransaction transaction) {
        // TODO add Client System Id
        return transaction.merchant().terminalID().getValue() + new LogicalDate(transaction.transactionDate().getValue());
    }

    private CardServiceSimulationReconciliationRecord createReportRecord(Date transactionDate, CardServiceSimulationMerchantAccount merchant) {
        CardServiceSimulationReconciliationRecord record = EntityFactory.create(CardServiceSimulationReconciliationRecord.class);
        record.merchant().set(merchant);
        record.date().setValue(new LogicalDate(transactionDate));
        record.totalDeposit().setValue(BigDecimal.ZERO);
        record.totalFee().setValue(BigDecimal.ZERO);
        record.visaTransactions().setValue(0);
        record.visaDeposit().setValue(BigDecimal.ZERO);
        record.visaFee().setValue(BigDecimal.ZERO);
        record.mastercardTransactions().setValue(0);
        record.mastercardDeposit().setValue(BigDecimal.ZERO);
        record.mastercardFee().setValue(BigDecimal.ZERO);
        return record;
    }

    private void addTransaction(CardServiceSimulationReconciliationRecord record, CardServiceSimulationTransaction transaction) {
        switch (transaction.card().cardType().getValue()) {
        case MasterCard:
            add(record.mastercardDeposit(), transaction.amount().getValue());
            inc(record.mastercardTransactions());
            break;
        case Visa:
        case VisaDebit:
            add(record.visaDeposit(), transaction.amount().getValue());
            inc(record.visaTransactions());
            break;
        }
        add(record.totalDeposit(), transaction.amount().getValue());
    }

    private void inc(IPrimitive<Integer> total) {
        total.setValue(total.getValue() + 1);
    }

    private void add(IPrimitive<BigDecimal> total, BigDecimal value) {
        total.setValue(total.getValue().add(value));
    }

    private void createReportFiles(Collection<CardServiceSimulationReconciliationRecord> records) {
        List<CardsReconciliationMerchantTotalRecord> merchantTotals = new ArrayList<>();
        List<CardsReconciliationCardTotalRecord> cardTotals = new ArrayList<>();

        String fileId = new SimpleDateFormat("yyyMMdd").format(new Date());
        int fileNo = 1;
        for (;; fileNo++) {
            EntityQueryCriteria<CardServiceSimulationReconciliationRecord> criteria = EntityQueryCriteria
                    .create(CardServiceSimulationReconciliationRecord.class);
            criteria.eq(criteria.proto().fileId(), fileId + "." + fileNo);
            if (!Persistence.service().exists(criteria)) {
                break;
            }
        }
        fileId += "." + fileNo;

        for (CardServiceSimulationReconciliationRecord record : records) {
            record.fileId().setValue(fileId);

            {
                CardsReconciliationMerchantTotalRecord merchantTotal = createMerchantTotal(record);
                merchantTotal.type().setValue(MerchantTotalRecordType.Deposit);
                merchantTotal.credit().setValue(record.totalDeposit().getValue());
                merchantTotals.add(merchantTotal);
            }

            if (record.visaTransactions().getValue() > 0) {
                CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                cardTotal.type().setValue(CardTotalRecordType.VisaDeposit);
                cardTotal.credit().setValue(record.visaDeposit().getValue());
                cardTotals.add(cardTotal);
            }

            if (record.mastercardTransactions().getValue() > 0) {
                CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                cardTotal.type().setValue(CardTotalRecordType.MastercardDeposit);
                cardTotal.credit().setValue(record.mastercardDeposit().getValue());
                cardTotals.add(cardTotal);
            }

        }

        String cardsReconciliationId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration()
                .getCardsReconciliationId();

        createMerchantTotalsFile(cardsReconciliationId, fileId, merchantTotals);
        createCardTotalsFile(cardsReconciliationId, fileId, cardTotals);
    }

    private CardsReconciliationMerchantTotalRecord createMerchantTotal(CardServiceSimulationReconciliationRecord record) {
        CardsReconciliationMerchantTotalRecord merchantTotal = EntityFactory.create(CardsReconciliationMerchantTotalRecord.class);
        merchantTotal.date().setValue(record.date().getValue());
        merchantTotal.merchantID().setValue(record.merchant().id().getStringView());
        merchantTotal.terminalID().setValue(record.merchant().terminalID().getValue());
        merchantTotal.credit().setValue(BigDecimal.ZERO);
        merchantTotal.debit().setValue(BigDecimal.ZERO);
        return merchantTotal;
    }

    private CardsReconciliationCardTotalRecord createCardTotal(CardServiceSimulationReconciliationRecord record) {
        CardsReconciliationCardTotalRecord cardTotal = EntityFactory.create(CardsReconciliationCardTotalRecord.class);
        cardTotal.date().setValue(record.date().getValue());
        cardTotal.merchantID().setValue(record.merchant().id().getStringView());
        cardTotal.terminalID().setValue(record.merchant().terminalID().getValue());
        cardTotal.credit().setValue(BigDecimal.ZERO);
        cardTotal.debit().setValue(BigDecimal.ZERO);
        return cardTotal;
    }

    private File getSftpDir() {
        File dir = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration()
                .getCaledonSimulatorSftpDirectory();
        return new File(dir, CardsReconciliationReceiveManager.remoteDirectory);
    }

    private void createMerchantTotalsFile(String cardsReconciliationId, String fileId, List<CardsReconciliationMerchantTotalRecord> merchantTotals) {
        ReportTableFormatter formatter = new ReportTableCSVFormatter();
        EntityReportFormatter<CardsReconciliationMerchantTotalRecord> entityFormatter = new EntityReportFormatter<CardsReconciliationMerchantTotalRecord>(
                CardsReconciliationMerchantTotalRecord.class);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, merchantTotals);

        File file = new File(getSftpDir(), cardsReconciliationId + "_TPA_dailyconsolidatedtotals_" + fileId + ".csv");
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
            out.flush();
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void createCardTotalsFile(String cardsReconciliationId, String fileId, List<CardsReconciliationCardTotalRecord> cardTotals) {
        ReportTableFormatter formatter = new ReportTableCSVFormatter();
        EntityReportFormatter<CardsReconciliationCardTotalRecord> entityFormatter = new EntityReportFormatter<CardsReconciliationCardTotalRecord>(
                CardsReconciliationCardTotalRecord.class);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, cardTotals);

        File file = new File(getSftpDir(), cardsReconciliationId + "_TPA_dailycardtotals_" + fileId + ".csv");
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
            out.flush();
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
