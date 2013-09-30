/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.reports;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.operations.rpc.dto.TenantSureCcTaransactionsReportLineDTO;

public class TransactionsReport implements Report {

    private static final Logger log = LoggerFactory.getLogger(TransactionsReport.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME = "Report";

    @Override
    public void start(ReportTableFormatter formatter) {
        EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO> er = new EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO>(
                TenantSureCcTaransactionsReportLineDTO.class);
        er.createHeader(formatter);
    }

    @Override
    public void processReport(final ExecutionMonitor executionMonitor, final Date date, final ReportTableFormatter formatter) {
        new UnitOfWork().execute(new Executable<VoidSerializable, Error>() {
            @Override
            public VoidSerializable execute() throws Error {
                EntityQueryCriteria<TenantSureTransaction> criteria = EntityQueryCriteria.create(TenantSureTransaction.class);
                criteria.ge(criteria.proto().transactionDate(), DateUtils.dayStart(date));
                criteria.le(criteria.proto().transactionDate(), DateUtils.dayEnd(date));
                criteria.eq(criteria.proto().status(), TenantSureTransaction.TransactionStatus.Cleared);

                ICursorIterator<TenantSureTransaction> transactions = Persistence.service().query(null, criteria, AttachLevel.Attached);

                EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO> reportGenerator = new EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO>(
                        TenantSureCcTaransactionsReportLineDTO.class);
                try {

                    while (transactions.hasNext()) {
                        TenantSureTransaction transaction = transactions.next();

                        TenantSureCcTaransactionsReportLineDTO transactionReportLine = EntityFactory.create(TenantSureCcTaransactionsReportLineDTO.class);
                        transactionReportLine.date().setValue(transaction.transactionDate().getValue());
                        transactionReportLine.tenant().setValue(transaction.insurance().tenant().customer().person().name().getStringView());
                        transactionReportLine.insuranceCertificateNumber().setValue(
                                transaction.insurance().certificate().insuranceCertificateNumber().getValue());
                        if (transaction.paymentMethod().details().isInstanceOf(CreditCardInfo.class)) {
                            CreditCardInfo ccInfo = transaction.paymentMethod().details().duplicate(CreditCardInfo.class);
                            transactionReportLine.creditCardType().setValue(ccInfo.cardType().getValue().toString());
                            transactionReportLine.creditCardNumber().setValue(ccInfo.card().obfuscatedNumber().getValue());
                        } else {
                            log.error("Unknown payment method details {} for TenantSure transaction {}", transaction.paymentMethod().details()
                                    .getInstanceValueClass().getSimpleName(), transaction.getPrimaryKey());
                            executionMonitor.addErredEvent(//@formatter:off
                                    EXECUTION_MONITOR_SECTION_NAME,
                                    SimpleMessageFormat.format("Unknown payment method details {0} for TenantSure transaction {1}", transaction.paymentMethod().details().getInstanceValueClass()
                                            .getSimpleName(), transaction.getPrimaryKey())
                            );//@formatter:on
                            transactionReportLine.creditCardType().setValue("N / A");
                            transactionReportLine.creditCardNumber().setValue("N / A");
                        }
                        transactionReportLine.amount().setValue(transaction.amount().getValue());
                        transactionReportLine.transactionReferenceNumber().setValue(
                                ServerSideFactory.create(CreditCardFacade.class).getTransactionreferenceNumber(ReferenceNumberPrefix.TenantSure,
                                        transaction.id().getStringView()));

                        reportGenerator.reportEntity(formatter, transactionReportLine);
                        executionMonitor.addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
                    }

                } finally {
                    transactions.close();
                }
                return null;
            }

        });

    }

    @Override
    public void complete(ReportFileCreator reportFileCreator, ReportTableFormatter formatter) {
        reportFileCreator.report(formatter.getBinaryData());
    }

}
