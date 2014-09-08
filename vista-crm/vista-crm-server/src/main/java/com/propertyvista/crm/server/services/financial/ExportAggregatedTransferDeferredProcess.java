/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.List;

import org.apache.poi.ss.usermodel.IndexedColors;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.server.services.financial.xlmodel.AggregatedTransferFileExportModel;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.CardsAggregatedTransfer;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;

@SuppressWarnings("serial")
public class ExportAggregatedTransferDeferredProcess extends AbstractDeferredProcess {
    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    private final EntityQueryCriteria<AggregatedTransfer> criteria;

    public ExportAggregatedTransferDeferredProcess(EntityQueryCriteria<AggregatedTransfer> criteria) {
        this.criteria = criteria;
    }

    @Override
    public void execute() {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter = new EntityReportFormatter<AggregatedTransferFileExportModel>(
                AggregatedTransferFileExportModel.class);
        entityFormatter.createHeader(formatter);

        try {
            Persistence.service().startBackgroundProcessTransaction();

            maximum = Persistence.service().count(criteria);

            ICursorIterator<AggregatedTransfer> transfers = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (transfers.hasNext()) {
                    AggregatedTransfer aggregateTransfer = transfers.next();
                    final EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
                    criteria.eq(criteria.proto().aggregatedTransfer(), aggregateTransfer);
                    List<PaymentRecord> payments = Persistence.service().query(criteria);
                    formatAggregatedTransfer(formatter, entityFormatter, aggregateTransfer, payments);
                    ++progress;
                    if (transfers.hasNext()) {
                        ((ReportTableXLSXFormatter) formatter).addSeparatorRow(IndexedColors.GREY_25_PERCENT);
                    }
                }
            } finally {
                transfers.close();
            }

        } finally {
            Persistence.service().endTransaction();
        }

        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        fileName = VistaDeployment.getCurrentPmc().name().getValue() + "-AggregatedTransfers.xlsx";
        d.save(fileName);
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(progress);
            r.setProgressMaximum(maximum);
            return r;
        }
    }

    private void formatAggregatedTransfer(ReportTableFormatter formatter, EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter,
            AggregatedTransfer transfer, List<PaymentRecord> payments) {

        if (payments.isEmpty()) {
            formatAggregatedTransferRecord(formatter, entityFormatter, transfer, null);
        } else {
            for (PaymentRecord payment : payments) {
                formatAggregatedTransferRecord(formatter, entityFormatter, transfer, payment);
            }
        }

    }

    private void formatAggregatedTransferRecord(ReportTableFormatter formatter, EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter,
            AggregatedTransfer transfer, PaymentRecord payment) {

        AggregatedTransferFileExportModel model = EntityFactory.create(AggregatedTransferFileExportModel.class);

        // Set AggregatedTransfer common data
        model.paymentDate().setValue(transfer.paymentDate().getValue());
        model.status().setValue(transfer.status().getValue());
        model.merchantAccount().setValue(transfer.merchantAccount().getValue());
        model.fundsTransferType().setValue(transfer.fundsTransferType().getValue());
        model.netAmount().setValue(transfer.netAmount().getValue());
        model.grossPaymentAmount().setValue(transfer.grossPaymentAmount().getValue());
        model.grossPaymentFee().setValue(transfer.grossPaymentFee().getValue());
        model.grossPaymentCount().setValue(transfer.grossPaymentCount().getValue());

        // Set Cards data
        if (transfer instanceof CardsAggregatedTransfer) {
            model.visaDeposit().setValue(((CardsAggregatedTransfer) transfer).visaDeposit().getValue());
            model.visaFee().setValue(((CardsAggregatedTransfer) transfer).visaFee().getValue());
            model.mastercardDeposit().setValue(((CardsAggregatedTransfer) transfer).mastercardDeposit().getValue());
            model.mastercardFee().setValue(((CardsAggregatedTransfer) transfer).mastercardFee().getValue());
        }

        // Set Eft data
        if (transfer instanceof EftAggregatedTransfer) {
            model.rejectItemsAmount().setValue(((EftAggregatedTransfer) transfer).rejectItemsAmount().getValue());
            model.rejectItemsFee().setValue(((EftAggregatedTransfer) transfer).rejectItemsFee().getValue());
            model.rejectItemsCount().setValue(((EftAggregatedTransfer) transfer).rejectItemsCount().getValue());
            model.returnItemsAmount().setValue(((EftAggregatedTransfer) transfer).returnItemsAmount().getValue());
            model.returnItemsFee().setValue(((EftAggregatedTransfer) transfer).returnItemsFee().getValue());
            model.returnItemsCount().setValue(((EftAggregatedTransfer) transfer).returnItemsCount().getValue());
            model.previousBalance().setValue(((EftAggregatedTransfer) transfer).previousBalance().getValue());
            model.merchantBalance().setValue(((EftAggregatedTransfer) transfer).merchantBalance().getValue());
            model.fundsReleased().setValue(((EftAggregatedTransfer) transfer).fundsReleased().getValue());
        }

        // Set Payment data
        if (payment != null) {
            model.paymentMethod().setValue(payment.paymentMethod().getValue());
            model.paymentStatus().setValue(payment.paymentStatus().getValue());
        }

        entityFormatter.reportEntity(formatter, model);
    }
}
