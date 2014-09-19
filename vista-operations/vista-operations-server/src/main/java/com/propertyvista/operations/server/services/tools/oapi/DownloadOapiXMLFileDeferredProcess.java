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
package com.propertyvista.operations.server.services.tools.oapi;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.financial.AggregatedTransfer;

@SuppressWarnings("serial")
public class DownloadOapiXMLFileDeferredProcess extends AbstractDeferredProcess {
    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    private final EntityQueryCriteria<AggregatedTransfer> criteria;

    public DownloadOapiXMLFileDeferredProcess(EntityQueryCriteria<AggregatedTransfer> criteria) {
        this.criteria = criteria;
    }

    @Override
    public void execute() {
//        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
//        EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter = new EntityReportFormatter<AggregatedTransferFileExportModel>(
//                AggregatedTransferFileExportModel.class);
//        entityFormatter.createHeader(formatter);
//
//        try {
//            Persistence.service().startBackgroundProcessTransaction();
//
//            maximum = Persistence.service().count(criteria);
//
//            ICursorIterator<AggregatedTransfer> transfers = Persistence.service().query(null, criteria, AttachLevel.Attached);
//            try {
//                while (transfers.hasNext()) {
//                    AggregatedTransfer aggregateTransfer = transfers.next();
//
//                    // Aggregated transfer records
//                    final EntityQueryCriteria<PaymentRecord> aggregatedTransferCriteria = EntityQueryCriteria.create(PaymentRecord.class);
//                    aggregatedTransferCriteria.eq(aggregatedTransferCriteria.proto().aggregatedTransfer(), aggregateTransfer);
//                    List<PaymentRecord> payments = Persistence.service().query(aggregatedTransferCriteria);
//
//                    // Returned Records
//                    final EntityQueryCriteria<PaymentRecord> returnedRecordsCriteria = EntityQueryCriteria.create(PaymentRecord.class);
//                    returnedRecordsCriteria.eq(returnedRecordsCriteria.proto().aggregatedTransferReturn(), aggregateTransfer);
//                    payments.addAll(Persistence.service().query(returnedRecordsCriteria));
//
//                    formatAggregatedTransfer(formatter, entityFormatter, aggregateTransfer, payments);
//                    ++progress;
//                    if (transfers.hasNext()) {
//                        formatter.newRow();
//                    }
//                }
//            } finally {
//                transfers.close();
//            }
//
//        } finally {
//            Persistence.service().endTransaction();
//        }

        //Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        String dummyData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><building propertyCode=\"B0\"><buildingType>mixedResidential</buildingType><address><streetNumber>1206</streetNumber><streetName>Emerson Ave</streetName><city>Saskatoon</city><province>Saskatchewan</province><postalCode>S7H 2X1</postalCode><country>Canada</country></address><marketing name=\"1206 Emerson Ave mktRG\"><description>Curabitur sem velit, ullamcorper nec sagittis et, fringilla at risus. Donec eleifend convallis massa, ac commodo odio condimentum eu.</description><blurbs/></marketing><amenities><amenity><name>Storage Space GM</name></amenity><amenity><name>Housekeeping RC</name></amenity></amenities><includedUtilities/><floorplans><floorplan propertyCode=\"B0\" name=\"4-bedroom 2 B\"><marketingName>4-bedroom</marketingName><description>Cras aliquam, quam eget dapibus mollis, nibh risus feugiat odio, sit amet interdum dui nisi nec nisl.</description><floorCount>2</floorCount><bedrooms>4</bedrooms><dens>0</dens><bathrooms>2</bathrooms><halfBath>0</halfBath><rentFrom>1617.00</rentFrom><rentTo>1771.00</rentTo><sqftFrom>2579</sqftFrom><sqftTo>3619</sqftTo><availableFrom>2011-01-13</availableFrom><amenities><amenity name=\"MWAMAL\"/><amenity name=\"QGONYH\"/><amenity name=\"XTXXCI\"/><amenity name=\"HBEPOS\"/></amenities><medias note=\"contentDetached\"/></floorplan><floorplan propertyCode=\"B0\" name=\"2-bedroom 1 B\"><marketingName>2-bedroom</marketingName><description>Aliquam porttitor scelerisque nisi at suscipit. Donec magna arcu, vulputate eu tristique a, varius eget leo.</description><floorCount>1</floorCount><bedrooms>2</bedrooms><dens>0</dens><bathrooms>1</bathrooms><halfBath>0</halfBath><rentFrom>1308.00</rentFrom><rentTo>1449.00</rentTo><sqftFrom>1763</sqftFrom><sqftTo>3739</sqftTo><availableFrom>2010-02-11</availableFrom><amenities><amenity name=\"DSREAV\"/><amenity name=\"MYTPQV\"/><amenity name=\"QOXYPL\"/><amenity name=\"JRRCGQ\"/></amenities><medias note=\"contentDetached\"/></floorplan><floorplan propertyCode=\"B0\" name=\"2-bedroom + den 3 D\"><marketingName>2-bedroom + den</marketingName><description>Suspendisse potenti. Morbi at nunc leo, vel aliquet est. Fusce iaculis turpis at ante aliquet tempor. Aenean metus leo, tincidunt quis condimentum non, porta eget libero.</description><floorCount>2</floorCount><bedrooms>2</bedrooms><dens>1</dens><bathrooms>3</bathrooms><halfBath>0</halfBath><rentFrom>1373.00</rentFrom><rentTo>1509.00</rentTo><sqftFrom>1300</sqftFrom><sqftTo>3632</sqftTo><availableFrom>2010-02-06</availableFrom><amenities><amenity name=\"MJUIIT\"/><amenity name=\"ANYRPV\"/></amenities><medias note=\"contentDetached\"/></floorplan><floorplan propertyCode=\"B0\" name=\"2-bedroom + den 1 B\"><marketingName>2-bedroom + den</marketingName><description>Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque auctor ante at justo viverra vitae volutpat elit pulvinar.</description><floorCount>2</floorCount><bedrooms>2</bedrooms><dens>1</dens><bathrooms>1</bathrooms><halfBath>0</halfBath><rentFrom>1272.00</rentFrom><rentTo>1372.00</rentTo><sqftFrom>1512</sqftFrom><sqftTo>3484</sqftTo><availableFrom>2010-03-21</availableFrom><amenities><amenity name=\"KQBNJN\"/><amenity name=\"SCQVKF\"/><amenity name=\"RJLDVN\"/><amenity name=\"EPPQCG\"/><amenity name=\"NNIBFJ\"/></amenities><medias note=\"contentDetached\"/></floorplan><floorplan propertyCode=\"B0\" name=\"1-bedroom 1 B\"><marketingName>1-bedroom</marketingName><description>Nunc ut ante eros, non fringilla diam. Cras sit amet consequat enim. Pellentesque commodo eros blandit augue laoreet at bibendum mi facilisis.</description><floorCount>2</floorCount><bedrooms>1</bedrooms><dens>0</dens><bathrooms>1</bathrooms><halfBath>0</halfBath><rentFrom>1170.00</rentFrom><rentTo>1223.00</rentTo><sqftFrom>1817</sqftFrom><sqftTo>3549</sqftTo><availableFrom>2011-04-12</availableFrom><amenities><amenity name=\"TTGRKL\"/><amenity name=\"JTJRGJ\"/><amenity name=\"BJEORN\"/></amenities><medias note=\"contentDetached\"/></floorplan></floorplans><units note=\"contentDetached\"/><parkings note=\"contentDetached\"/><contacts><contact name=\"Elinore Tylor\"><email>elinore.tylor@yahoo.com</email><phone>416-508-5487</phone></contact><contact name=\"Jeanie Boera\"><email>jeanie.boera@yahoo.com</email><phone>416-375-6462</phone></contact><contact name=\"Scott Virock\"><email>scott.virock@yahoo.ca</email><phone>416-252-1346</phone></contact><contact name=\"Randy C Eisenmenger\"><email>randy.eisenmenger@me.com</email><phone>416-512-9000</phone></contact></contacts><medias><media><caption>building7</caption><accessUrl>http://vista-site.dev.birchwoodsoftwaregroup.com:8888/vista/site/media/118643/large.png</accessUrl></media><media><caption>building7-1</caption><accessUrl>http://vista-site.dev.birchwoodsoftwaregroup.com:8888/vista/site/media/118644/large.png</accessUrl></media></medias><leases note=\"contentDetached\"/></building>";
        Downloadable d = new Downloadable(dummyData.getBytes(), MimeMap.getContentType(DownloadFormat.XML));
        fileName = "OapiXMLFile.xml";
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

//    private void formatAggregatedTransfer(ReportTableFormatter formatter, EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter,
//            AggregatedTransfer transfer, List<PaymentRecord> payments) {
//
//        formatAggregatedTransferRecord(formatter, entityFormatter, transfer);
//
//        for (PaymentRecord payment : payments) {
//            formatPaymentRecord(formatter, entityFormatter, transfer, payment);
//        }
//
//    }
//
//    private void formatAggregatedTransferRecord(ReportTableFormatter formatter, EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter,
//            AggregatedTransfer transfer) {
//
//        AggregatedTransferFileExportModel model = EntityFactory.create(AggregatedTransferFileExportModel.class);
//
//        // Set AggregatedTransfer common data
//        fillAggregatedTransferValues(transfer, model);
//
//        // Set Cards data
//        if (transfer instanceof CardsAggregatedTransfer) {
//            fillCardsAggregatedTransferValues(transfer, model);
//        }
//
//        // Set Eft data
//        if (transfer instanceof EftAggregatedTransfer) {
//            fillEftAggregatedTransferValues(transfer, model);
//        }
//
//        entityFormatter.reportEntity(formatter, model);
//
//        ((ReportTableXLSXFormatter) formatter).fillRowBackGround(formatter.getRowCount() - 2, 0, ((ReportTableXLSXFormatter) formatter).getColumnsCount(),
//                IndexedColors.GREY_25_PERCENT);
//
//    }
//
//    private void formatPaymentRecord(ReportTableFormatter formatter, EntityReportFormatter<AggregatedTransferFileExportModel> entityFormatter,
//            AggregatedTransfer transfer, PaymentRecord payment) {
//
//        AggregatedTransferFileExportModel model = EntityFactory.create(AggregatedTransferFileExportModel.class);
//
//        fillPaymentValues(payment, model);
//
//        entityFormatter.reportEntity(formatter, model);
//    }
//
//    private void fillAggregatedTransferValues(AggregatedTransfer transfer, AggregatedTransferFileExportModel model) {
//        model.paymentDate().setValue(transfer.paymentDate().getValue());
//        model.status().setValue(transfer.status().getValue());
//        model.merchantAccount().setValue(transfer.merchantAccount().getValue());
//        model.fundsTransferType().setValue(transfer.fundsTransferType().getValue());
//        model.netAmount().setValue(transfer.netAmount().getValue());
//        model.grossPaymentAmount().setValue(transfer.grossPaymentAmount().getValue());
//        model.grossPaymentFee().setValue(transfer.grossPaymentFee().getValue());
//        model.grossPaymentCount().setValue(transfer.grossPaymentCount().getValue());
//    }
//
//    private void fillCardsAggregatedTransferValues(AggregatedTransfer transfer, AggregatedTransferFileExportModel model) {
//        model.visaDeposit().setValue(((CardsAggregatedTransfer) transfer).visaDeposit().getValue());
//        model.visaFee().setValue(((CardsAggregatedTransfer) transfer).visaFee().getValue());
//        model.mastercardDeposit().setValue(((CardsAggregatedTransfer) transfer).mastercardDeposit().getValue());
//        model.mastercardFee().setValue(((CardsAggregatedTransfer) transfer).mastercardFee().getValue());
//    }
//
//    private void fillEftAggregatedTransferValues(AggregatedTransfer transfer, AggregatedTransferFileExportModel model) {
//        model.rejectItemsAmount().setValue(((EftAggregatedTransfer) transfer).rejectItemsAmount().getValue());
//        model.rejectItemsFee().setValue(((EftAggregatedTransfer) transfer).rejectItemsFee().getValue());
//        model.rejectItemsCount().setValue(((EftAggregatedTransfer) transfer).rejectItemsCount().getValue());
//        model.returnItemsAmount().setValue(((EftAggregatedTransfer) transfer).returnItemsAmount().getValue());
//        model.returnItemsFee().setValue(((EftAggregatedTransfer) transfer).returnItemsFee().getValue());
//        model.returnItemsCount().setValue(((EftAggregatedTransfer) transfer).returnItemsCount().getValue());
//        model.previousBalance().setValue(((EftAggregatedTransfer) transfer).previousBalance().getValue());
//        model.merchantBalance().setValue(((EftAggregatedTransfer) transfer).merchantBalance().getValue());
//        model.fundsReleased().setValue(((EftAggregatedTransfer) transfer).fundsReleased().getValue());
//    }
//
//    private void fillPaymentValues(PaymentRecord payment, AggregatedTransferFileExportModel model) {
//        Persistence.service().retrieveMember(payment.billingAccount());
//        Persistence.service().retrieveMember(payment.billingAccount().lease());
//        Persistence.service().retrieveMember(payment.billingAccount().lease().unit().building());
//        Persistence.service().retrieveMember(payment.leaseTermParticipant());
//        model.participantId().setValue(payment.leaseTermParticipant().leaseParticipant().participantId().getValue());
//        model.leaseId().setValue(payment.billingAccount().lease().leaseId().getValue());
//        model.propertyCode().setValue(payment.billingAccount().lease().unit().building().propertyCode().getValue());
//        model.amount().setValue(payment.amount().getValue());
//        model.type().setValue(payment.paymentMethod().type().getValue());
//        model.receivedDate().setValue(payment.receivedDate().getValue());
//        model.paymentStatus().setValue(payment.paymentStatus().getValue());
//        model.paymentId().setValue(payment.id().getValue());
//    }

}
