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
 */
package com.propertyvista.eft.caledoncards.reports;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.eft.EftFileUtils;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;
import com.propertyvista.server.sftp.SftpClient;
import com.propertyvista.server.sftp.SftpFile;

public class CardsReconciliationReceiveManager {

    private static final Logger log = LoggerFactory.getLogger(CardsReconciliationReceiveManager.class);

    public static final String remoteDirectory = "cards_out";

    public DailyReportTO receiveCardsDailyReportFile(String cardsReconciliationId) throws SftpTransportConnectionException {
        log.debug("pool DailyReport files for cardsReconciliationId {}", cardsReconciliationId);

        File workdir = getLocalWorkir();

        // files  20140531_003631_PROPERTYVISTA.CSV

        SftpFile sftpFile = SftpClient.receiveFile(configuration(), new DailyReportRetrieveFilter(workdir, cardsReconciliationId), remoteDirectory);
        if (sftpFile == null) {
            return null;
        }

        DailyReportTO to = EntityFactory.create(DailyReportTO.class);

        boolean parsOk = false;
        try {
            to.fileName().setValue(sftpFile.remoteName);
            to.remoteFileDate().setValue(new Date(sftpFile.lastModified));
            to.records().addAll(new DailyReportParser().parsReport(sftpFile.localFile));

            parsOk = true;
        } finally {
            if (!parsOk) {
                EftFileUtils.move(sftpFile.localFile, workdir, "error");
            }
        }

        return to;
    }

    public CardsReconciliationTO receiveCardsReconciliationFiles(String cardsReconciliationId) throws SftpTransportConnectionException {
        log.debug("pool Reconciliation files for cardsReconciliationId {}", cardsReconciliationId);

        File workdir = getLocalWorkir();

        // Merchant File  PROPERTYVISTA_TPA_dailyconsolidatedtotals_20140520.02.csv
        // Cards File PROPERTYVISTA_TPA_dailycardtotals_20140520.02.csv

        SftpFile sftpFileMerchant = SftpClient.receiveFile(configuration(), new CardsReconciliationMerchantTotalRetrieveFilter(workdir, cardsReconciliationId),
                remoteDirectory);
        if (sftpFileMerchant == null) {
            return null;
        }
        SftpFile sftpFileCards = SftpClient.receiveFile(configuration(), new CardsReconciliationCardTotalRetrieveFilter(workdir, sftpFileMerchant.remoteName),
                remoteDirectory);
        if (sftpFileCards == null) {
            return null;
        }

        CardsReconciliationTO to = EntityFactory.create(CardsReconciliationTO.class);

        boolean parsOk = false;
        try {
            to.fileNameMerchantTotal().setValue(sftpFileMerchant.remoteName);
            to.remoteFileDateMerchantTotal().setValue(new Date(sftpFileMerchant.lastModified));
            to.merchantTotals().addAll(new CardsReconciliationParser().parsMerchantTotalReport(sftpFileMerchant.localFile));

            to.fileNameCardTotal().setValue(sftpFileCards.remoteName);
            to.remoteFileDateCardTotal().setValue(new Date(sftpFileCards.lastModified));
            to.cardTotals().addAll(new CardsReconciliationParser().parsCardTotalReport(sftpFileCards.localFile));

            parsOk = true;
        } finally {
            if (!parsOk) {
                EftFileUtils.move(sftpFileMerchant.localFile, workdir, "error");
                EftFileUtils.move(sftpFileCards.localFile, workdir, "error");
            }
        }

        return to;
    }

    public void confirmReceivedCardsReconciliationFiles(Collection<String> fileNames, boolean protocolErrorFlag) {
        File workdir = getLocalWorkir();
        for (String fileName : fileNames) {
            if (protocolErrorFlag) {
                EftFileUtils.move(new File(workdir, fileName), workdir, "error");
            } else {
                EftFileUtils.move(new File(workdir, fileName), workdir, "processed");

                // Do not remove file from Production while running on QA server.
                if (VistaDeployment.isVistaProduction() || VistaSystemsSimulationConfig.getConfiguration().useFundsTransferSimulator().getValue(Boolean.TRUE)) {
                    try {
                        try {
                            SftpClient.removeFile(configuration(), remoteDirectory, fileName);
                        } catch (SftpTransportConnectionException noConnection) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            SftpClient.removeFile(configuration(), remoteDirectory, fileName);
                        }
                    } catch (Throwable e) {
                        log.warn("unable to remove remote file {}", fileName, e);
                        ServerSideFactory.create(OperationsAlertFacade.class).record(null,
                                "Unable to remove remote file Cards {} on caledon SFTP, Remove it manually", fileName);

                    }
                }
            }
        }
    }

    private CaledonFundsTransferConfiguration configuration() {
        return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonFundsTransferConfiguration();
    }

    private File getLocalWorkir() {
        File padWorkdir = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getCaledonInterfaceWorkDirectory();
        if (!padWorkdir.exists()) {
            if (!padWorkdir.mkdirs()) {
                log.error("Unable to create directory {}", padWorkdir.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", padWorkdir.getAbsolutePath()));
            }
        }
        return padWorkdir;
    }

}
