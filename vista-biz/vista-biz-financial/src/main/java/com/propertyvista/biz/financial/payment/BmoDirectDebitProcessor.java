/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitFile;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecordProcessingStatus;
import com.propertyvista.payment.pad.EFTTransportFacade;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

class BmoDirectDebitProcessor {

    boolean receiveBmoFiles(ExecutionMonitor executionMonitor) {
        final DirectDebitFile directDebitFile;
        try {
            directDebitFile = ServerSideFactory.create(EFTTransportFacade.class).receiveBmoFiles();
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return false;
        }
        if (directDebitFile == null) {
            executionMonitor.addInfoEvent("Pooled, No file found on server", null);
            return false;
        } else {
            executionMonitor.addInfoEvent("received file", directDebitFile.fileName().getValue());
            executionMonitor.addInfoEvent("fileSerialDate", directDebitFile.fileSerialDate().getStringView());
            executionMonitor.addInfoEvent("fileSerialNumber", directDebitFile.fileSerialNumber().getValue());
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                validateAndPersistFile(directDebitFile);
                return null;
            }
        });

        return true;
    }

    private void validateAndPersistFile(DirectDebitFile directDebitFile) {
        Persistence.service().persist(directDebitFile);
        for (DirectDebitRecord record : directDebitFile.records()) {
            record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Received);
            Persistence.service().persist(record);
        }
    }
}
