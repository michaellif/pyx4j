/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.importer.model.PadFileModel;

public class PadEFTPCalulations {

    private static final Logger log = LoggerFactory.getLogger(PadEFTPCalulations.class);

    public static void main(String[] args) {
        //new PadEFTPCalulations("gr041").run();
        new PadEFTPCalulations("gr0527").run();
        //new PadEFTPCalulations("ber0527").run();
    }

    private final String fileNamePrefix;

    private final Map<String, EFTModel> eftsMap = new HashMap<String, EFTModel>();

    static class MismatchCounter {

        public int different;

        public int missingInVista;

        public int missingInYardi;

        @Override
        public String toString() {
            return "Mismatch Counter : different=" + different + ", missingInVista=" + missingInVista + ", missingInYardi=" + missingInYardi;
        }
    }

    PadEFTPCalulations(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    void run() {
        String inputFile = fileNamePrefix + "_lease_charges.csv";
        // Test 
        inputFile = "./" + fileNamePrefix + "_lease_charges.xlsx";

        List<PadFileModel> models;
        if (inputFile.startsWith(".")) {
            models = new TenantPadParser.PadFileCSVReciver("").loadFile(inputFile);
        } else {
            models = new TenantPadParser.PadFileCSVReciver("").loadResourceFile(IOUtils.resourceFileName(inputFile, PadEFTPCalulations.class));
        }

        new TenantPadProcessor().processOfflineTest(models);

        List<EFTModel> efts = EntityCSVReciver.create(EFTModel.class).loadResourceFile(
                IOUtils.resourceFileName(fileNamePrefix + "_eft_dump.csv", PadEFTPCalulations.class));
        createEftAccountMap(efts);

        // Compare the results
        List<PadEFTReportModel> reportModels = new ArrayList<PadEFTReportModel>();
        List<PadEFTReportModel> reportModelDiff = new ArrayList<PadEFTReportModel>();

        MismatchCounter counter = new MismatchCounter();
        String prevLeaseId = null;
        for (PadFileModel data : models) {
            PadEFTReportModel reportModel = data.duplicate(PadEFTReportModel.class);
            if ((prevLeaseId != null) && (!prevLeaseId.equals(reportModel.leaseId().getValue()))) {
                // add empty row
                reportModels.add(EntityFactory.create(PadEFTReportModel.class));
            }

            reportModels.add(reportModel);

            // Copy data for report
            reportModel.invalid().setValue(reportModel._import().invalid().getValue());
            reportModel.message().setValue(reportModel._import().message().getValue());
            reportModel.status().setValue(reportModel._processorInformation().status().getValue());

            StringBuilder amountStored = new StringBuilder();
            for (PadFileModel charge : data._processorInformation().accountCharges()) {
                if (amountStored.length() > 0) {
                    amountStored.append(", ");
                }
                amountStored.append(charge.chargeCode().getValue()).append(": ");
                amountStored.append(charge._processorInformation().chargeEftAmount().getValue().toString());
            }
            reportModel.amountStored().setValue(amountStored.toString());

            if (!reportModel._processorInformation().accountEftAmountTotal().isNull()) {
                reportModel.calulatedEftAmount().setValue(reportModel._processorInformation().accountEftAmountTotal().getValue());
            } else if (!reportModel._processorInformation().calulatedEftTotalAmount().isNull()) {
                reportModel.calulatedEftAmount().setValue(reportModel._processorInformation().calulatedEftTotalAmount().getValue());
            }
            if (!reportModel._processorInformation().percent().isNull()) {
                reportModel.percentStored().setValue(reportModel._processorInformation().percent().getValue().doubleValue() * 100);
            }
            prevLeaseId = reportModel.leaseId().getValue();

            //  Compare the results
            EFTModel eft = getModelByAccount(data);
            if (eft != null) {
                reportModel.eftAmount().setValue(new BigDecimal(eft.amount().getValue()));
            }

            if (reportModel._processorInformation().status().isNull()) {
                if (eft != null) {
                    if (eft.eftCreated().getValue(false)) {
                        if (!reportModel.calulatedEftAmount().isNull()) {
                            reportModel.eftAmountDelta().setValue(reportModel.calulatedEftAmount().getValue());
                            reportModelDiff.add(reportModel);
                            counter.missingInYardi++;
                        }
                    } else {
                        eft.eftCreated().setValue(true);
                        BigDecimal estimatedEft = reportModel.calulatedEftAmount().getValue();
                        if (estimatedEft != null) {
                            reportModel.eftAmountDelta().setValue(estimatedEft.subtract(reportModel.eftAmount().getValue()));

                            if (reportModel.eftAmountDelta().getValue().compareTo(BigDecimal.ZERO) != 0) {
                                reportModelDiff.add(reportModel);
                                counter.different++;
                            }
                        }
                    }
                }
            } else {
                if (!reportModel.calulatedEftAmount().isNull()) {
                    reportModel.eftAmountDelta().setValue(reportModel.calulatedEftAmount().getValue());
                    reportModelDiff.add(reportModel);
                    counter.missingInYardi++;
                }
            }

        }

        // List EFT that do not have our EFT
        for (EFTModel eft : efts) {
            if (!eft.eftCreated().getValue(false)) {
                PadEFTReportModel reportModel = EntityFactory.create(PadEFTReportModel.class);
                reportModel.leaseId().set(eft.leaseId());
                reportModel.eftAmountDelta().setValue(new BigDecimal(eft.amount().getValue()));
                reportModelDiff.add(reportModel);
                reportModels.add(reportModel);
                counter.missingInVista++;
            }
        }

        System.out.println(fileNamePrefix + " " + counter);

        createReport("", reportModels);
        createReport("_diff", reportModelDiff);
    }

    private void createEftAccountMap(List<EFTModel> efts) {
        for (EFTModel eft : efts) {
            String account = eft.leaseId().getStringView() + "_" + getAccount(eft);
            if (eftsMap.containsKey(account)) {
                throw new Error("Duplicate EFT for account# " + account);
            }
            eftsMap.put(account, eft);
        }

    }

    String getAccount(EFTModel model) {
        StringBuilder b = new StringBuilder();
        b.append(model.accountNumber().getValue().trim()).append(':');
        b.append(model.bankId().getValue().trim()).append(':');
        b.append(model.transitNumber().getValue().trim());
        return b.toString();
    }

    private EFTModel getModelByAccount(PadFileModel model) {
        String account = model.leaseId().getStringView() + "_" + TenantPadProcessor.getAccount(model);
        return eftsMap.get(account);
    }

    private void createReport(String namePrefix, List<PadEFTReportModel> reportModels) {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(false);
        EntityReportFormatter<PadEFTReportModel> entityFormatter = new EntityReportFormatter<PadEFTReportModel>(PadEFTReportModel.class);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, reportModels);
        formatter.getBinaryData();

        File file = new File(new File("target"), fileNamePrefix + namePrefix + "_result.xls");
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
