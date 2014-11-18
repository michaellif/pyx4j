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
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.eft.caledoncards.reports.CardsReconciliationReceiveManager;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationCompany;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationMerchantAccount;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction.SimulationTransactionType;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord.CardTotalRecordType;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord.MerchantTotalRecordType;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportCardType;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportRecordType;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;

public class CardReconciliationSimulationManager {

    private static final Logger log = LoggerFactory.getLogger(CardReconciliationSimulationManager.class);

    public String createReports(CardServiceSimulationCompany companyId, LogicalDate from, LogicalDate to) {
        log.debug("create CardReconciliation Company:{} from:{} to:{}", companyId, from, to);
        EntityQueryCriteria<CardServiceSimulationTransaction> criteria = EntityQueryCriteria.create(CardServiceSimulationTransaction.class);
        criteria.in(criteria.proto().transactionType(), SimulationTransactionType.Sale, SimulationTransactionType.Completion, SimulationTransactionType.Return);
        criteria.eq(criteria.proto().merchant().company(), companyId);
        criteria.eq(criteria.proto().voided(), Boolean.FALSE);
        criteria.ge(criteria.proto().transactionDate(), from);
        criteria.lt(criteria.proto().transactionDate(), DateUtils.addDays(to, +1));

        final CardServiceSimulationCompany company = Persistence.service().retrieve(CardServiceSimulationCompany.class, companyId.getPrimaryKey());

        final List<CardServiceSimulationTransaction> transactions = Persistence.service().query(criteria);
        final Collection<CardServiceSimulationReconciliationRecord> records = createReconciliationRecord(transactions);

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                createReportFiles(company.companyId().getStringView(), transactions, records);
                Persistence.service().persist(records);
                return null;
            }
        });

        return "Created " + records.size() + " record(s)";

    }

    private Collection<CardServiceSimulationReconciliationRecord> createReconciliationRecord(List<CardServiceSimulationTransaction> transactions) {
        final Map<String, CardServiceSimulationReconciliationRecord> records = new HashMap<>();
        for (CardServiceSimulationTransaction transaction : transactions) {
            CardServiceSimulationReconciliationRecord record = records.get(key(transaction));
            if (record == null) {
                record = createReportRecord(transaction.transactionDate().getValue(), transaction.merchant());
                records.put(key(transaction), record);
            }
            addTransaction(record, transaction);
        }
        return records.values();
    }

    public DailyReportTO createDailyReport(List<CardServiceSimulationTransaction> transactions) {
        DailyReportTO to = EntityFactory.create(DailyReportTO.class);
        for (CardServiceSimulationTransaction transaction : transactions) {
            DailyReportRecord record = EntityFactory.create(DailyReportRecord.class);

            if (transaction.transactionType().getValue() == SimulationTransactionType.Return) {
                record.transactionType().setValue(DailyReportRecordType.RETU);
            } else {
                if (transaction.convenienceFee().isNull()) {
                    record.transactionType().setValue(DailyReportRecordType.SALE);
                } else {
                    record.transactionType().setValue(DailyReportRecordType.PRCO);
                }
            }

            record.terminalID().setValue(transaction.merchant().terminalID().getValue());
            record.amount().setValue(transaction.amount().getValue());
            record.date().setValue(transaction.transactionDate().getValue());

            record.referenceNumber().setValue(transaction.reference().getValue());
            switch (transaction.card().cardType().getValue()) {
            case MasterCard:
                record.cardType().setValue(DailyReportCardType.MCRD);
                break;
            case Visa:
            case VisaDebit:
                record.cardType().setValue(DailyReportCardType.VISA);
                break;
            }
            record.cardNumber().setValue("***" + last4(transaction.card().cardNumber().getValue()));
            record.expiry().setValue(new SimpleDateFormat("yyMM").format(transaction.card().expiryDate().getValue()));

            record.authNumber().setValue(transaction.authorizationNumber().getValue());
            record.response().setValue(transaction.authorizationNumber().getValue() + "   $" + record.amount().getValue());
            record.approved().setValue(true);
            record.voided().setValue(false);
            record.oper().setValue("003");

            to.records().add(record);
        }
        return to;
    }

    private String last4(String value) {
        if (value != null && value.length() > 4) {
            return value.substring(value.length() - 4);
        } else {
            return "####";
        }
    }

    public CardsReconciliationTO createReport(List<CardServiceSimulationTransaction> transactions) {
        Collection<CardServiceSimulationReconciliationRecord> records = createReconciliationRecord(transactions);
        return createReconciliationTO(records);
    }

    private String key(CardServiceSimulationTransaction transaction) {
        // TODO add Client System Id
        return transaction.merchant().terminalID().getValue() + new LogicalDate(transaction.transactionDate().getValue());
    }

    private CardServiceSimulationReconciliationRecord createReportRecord(Date transactionDate, CardServiceSimulationMerchantAccount merchant) {
        CardServiceSimulationReconciliationRecord record = EntityFactory.create(CardServiceSimulationReconciliationRecord.class);
        record.merchant().set(merchant);
        record.depositDate().setValue(new LogicalDate(new LogicalDate(DateUtils.addDays(transactionDate, +1))));
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
        BigDecimal amount = transaction.amount().getValue();
        if (transaction.transactionType().getValue() == SimulationTransactionType.Return) {
            amount = amount.negate();
        } else if (!"0000".equals(transaction.responseCode().getValue()) || transaction.voided().getValue(false)) {
            // Ignore non processed transactions
            return;
        }

        BigDecimal fee = BigDecimal.ZERO;

        if (transaction.convenienceFee().isNull()) {
            BigDecimal feePercent;
            switch (transaction.card().cardType().getValue()) {
            case MasterCard:
                feePercent = transaction.merchant().masterCardFee().getValue();
                break;
            case Visa:
                feePercent = transaction.merchant().visaCreditFee().getValue();
                break;
            case VisaDebit:
                feePercent = transaction.merchant().visaDebitFee().getValue();
                break;
            default:
                throw new Error();
            }
            fee = amount.multiply(feePercent).setScale(2, RoundingMode.HALF_UP);
        }

        switch (transaction.card().cardType().getValue()) {
        case MasterCard:
            add(record.mastercardDeposit(), amount);
            add(record.mastercardFee(), fee);
            inc(record.mastercardTransactions());
            break;
        case Visa:
            add(record.visaDeposit(), amount);
            add(record.visaFee(), fee);
            inc(record.visaTransactions());
            break;
        case VisaDebit:
            add(record.visaDeposit(), amount);
            add(record.visaFee(), fee);
            inc(record.visaTransactions());
            break;
        }
        add(record.totalDeposit(), amount);
        add(record.totalFee(), fee);
    }

    private void inc(IPrimitive<Integer> total) {
        total.setValue(total.getValue() + 1);
    }

    private void add(IPrimitive<BigDecimal> total, BigDecimal value) {
        if (value != null) {
            total.setValue(total.getValue().add(value));
        }
    }

    private CardsReconciliationTO createReconciliationTO(Collection<CardServiceSimulationReconciliationRecord> records) {
        CardsReconciliationTO to = EntityFactory.create(CardsReconciliationTO.class);
        for (CardServiceSimulationReconciliationRecord record : records) {

            {
                CardsReconciliationMerchantTotalRecord merchantTotal = createMerchantTotal(record);
                merchantTotal.type().setValue(MerchantTotalRecordType.Deposit);
                if (record.totalDeposit().getValue().compareTo(BigDecimal.ZERO) > 0) {
                    merchantTotal.credit().setValue(record.totalDeposit().getValue());
                } else {
                    merchantTotal.debit().setValue(record.totalDeposit().getValue().negate());
                }
                to.merchantTotals().add(merchantTotal);
            }

            if (record.totalFee().getValue().compareTo(BigDecimal.ZERO) != 0) {
                CardsReconciliationMerchantTotalRecord merchantTotal = createMerchantTotal(record);
                merchantTotal.type().setValue(MerchantTotalRecordType.Fees);
                if (record.totalFee().getValue().compareTo(BigDecimal.ZERO) > 0) {
                    merchantTotal.debit().setValue(record.totalFee().getValue());
                } else {
                    merchantTotal.credit().setValue(record.totalFee().getValue().negate());
                }
                to.merchantTotals().add(merchantTotal);
            }

            if (record.visaTransactions().getValue() > 0) {
                {
                    CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                    cardTotal.type().setValue(CardTotalRecordType.VisaDeposit);
                    setCredit(cardTotal, record.visaDeposit().getValue());
                    to.cardTotals().add(cardTotal);
                }
                if (record.visaFee().getValue().compareTo(BigDecimal.ZERO) != 0) {
                    CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                    cardTotal.type().setValue(CardTotalRecordType.VisaFees);
                    setDebit(cardTotal, record.visaFee().getValue());
                    to.cardTotals().add(cardTotal);
                }
            }

            if (record.mastercardTransactions().getValue() > 0) {
                {
                    CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                    cardTotal.type().setValue(CardTotalRecordType.MastercardDeposit);
                    setCredit(cardTotal, record.mastercardDeposit().getValue());
                    to.cardTotals().add(cardTotal);
                }
                if (record.mastercardFee().getValue().compareTo(BigDecimal.ZERO) != 0) {
                    CardsReconciliationCardTotalRecord cardTotal = createCardTotal(record);
                    cardTotal.type().setValue(CardTotalRecordType.MastercardFees);
                    setDebit(cardTotal, record.mastercardFee().getValue());
                    to.cardTotals().add(cardTotal);
                }
            }

        }
        return to;
    }

    private void setCredit(CardsReconciliationCardTotalRecord cardTotal, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            cardTotal.credit().setValue(value);
        } else {
            cardTotal.debit().setValue(value.negate());
        }
    }

    private void setDebit(CardsReconciliationCardTotalRecord cardTotal, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            cardTotal.debit().setValue(value);
        } else {
            cardTotal.credit().setValue(value.negate());
        }
    }

    private void createReportFiles(String cardsReconciliationId, List<CardServiceSimulationTransaction> transactions,
            Collection<CardServiceSimulationReconciliationRecord> records) {

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
        String dailyfileId = fileId + "_" + new DecimalFormat("000000").format(fileNo);

        fileId += "." + fileNo;

        for (CardServiceSimulationReconciliationRecord record : records) {
            record.fileId().setValue(fileId);
        }

        CardsReconciliationTO to = createReconciliationTO(records);

        createMerchantTotalsFile(cardsReconciliationId, fileId, to.merchantTotals());
        createCardTotalsFile(cardsReconciliationId, fileId, to.cardTotals());

        DailyReportTO drTo = createDailyReport(transactions);

        createDailyReportFile(cardsReconciliationId, dailyfileId, drTo.records());
    }

    private CardsReconciliationMerchantTotalRecord createMerchantTotal(CardServiceSimulationReconciliationRecord record) {
        CardsReconciliationMerchantTotalRecord merchantTotal = EntityFactory.create(CardsReconciliationMerchantTotalRecord.class);
        merchantTotal.date().setValue(record.depositDate().getValue());
        merchantTotal.merchantID().setValue(record.merchant().id().getStringView());
        merchantTotal.terminalID().setValue(record.merchant().terminalID().getValue());
        merchantTotal.credit().setValue(BigDecimal.ZERO);
        merchantTotal.debit().setValue(BigDecimal.ZERO);
        return merchantTotal;
    }

    private CardsReconciliationCardTotalRecord createCardTotal(CardServiceSimulationReconciliationRecord record) {
        CardsReconciliationCardTotalRecord cardTotal = EntityFactory.create(CardsReconciliationCardTotalRecord.class);
        cardTotal.date().setValue(record.depositDate().getValue());
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

    private void createDailyReportFile(String cardsReconciliationId, String fileId, List<DailyReportRecord> records) {
        ReportTableCSVFormatter formatter = new ReportTableCSVFormatter(StandardCharsets.US_ASCII);
        formatter.setForceQuote(false);
        EntityReportFormatter<DailyReportRecord> entityFormatter = new EntityReportFormatter<DailyReportRecord>(DailyReportRecord.class);
        entityFormatter.setMemberValueUseStringView(true);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, records);

        File file = new File(getSftpDir(), fileId + "_" + cardsReconciliationId + ".CSV");
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
            out.flush();
            log.debug("DailyReport file created {}", file);
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void createMerchantTotalsFile(String cardsReconciliationId, String fileId, List<CardsReconciliationMerchantTotalRecord> merchantTotals) {
        ReportTableCSVFormatter formatter = new ReportTableCSVFormatter(StandardCharsets.US_ASCII);
        formatter.setForceQuote(true);
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
            log.debug("MerchantTotals file created {}", file);
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void createCardTotalsFile(String cardsReconciliationId, String fileId, List<CardsReconciliationCardTotalRecord> cardTotals) {
        ReportTableCSVFormatter formatter = new ReportTableCSVFormatter(StandardCharsets.US_ASCII);
        formatter.setForceQuote(true);
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
            log.debug("CardTotals file created {}", file);
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
