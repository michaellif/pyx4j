/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 14, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.importer;

import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.crm.rpc.dto.ImportBuildingDataParametersDTO;
import com.propertyvista.interfaces.importer.ImportBuildingDataProcessor;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.parser.VistaXMLImportParser;
import com.propertyvista.operations.domain.scheduler.CompletionType;

@SuppressWarnings("serial")
public class ImportBuildingDataDeferredProcess extends AbstractDeferredProcess {

    private final ExecutionMonitor monitor;

    //TODO this should identify what to import and what building
    private final ImportBuildingDataParametersDTO uploadInitiationData;

    private final UploadedData uploadedData;

    public ImportBuildingDataDeferredProcess(ImportBuildingDataParametersDTO uploadInitiationData, UploadedData uploadedData) {
        super();
        this.uploadInitiationData = uploadInitiationData;
        this.uploadedData = uploadedData;
        monitor = new ExecutionMonitor();
    }

    @Override
    public void cancel() {
        monitor.requestTermination();
        super.cancel();
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        if (!r.isCompleted() && !r.isCanceled()) {
            r.setMessage("Errors: " + monitor.getErred());
        } else if (monitor.getErred() > 0) {
            r.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred) + monitor.getTextMessages(CompletionType.failed));
        } else {
            r.setMessage(monitor.getTextMessages(CompletionType.erred) + monitor.getTextMessages(CompletionType.failed));
        }
        return r;
    }

    private static class FlowTerminationRollbackRuntimeException extends RuntimeException {
    };

    @Override
    public void execute() {
        final ImportIO importIO = new VistaXMLImportParser().parse(uploadedData.binaryContent, DownloadFormat.XML);

        for (BuildingIO buildingIO : importIO.buildings()) {
            progress.progressMaximum.addAndGet(buildingIO.units().size());
        }

        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {

                    for (BuildingIO buildingIO : importIO.buildings()) {
                        new ImportBuildingDataProcessor().importModel(buildingIO, progress, monitor);
                        if (status().isCanceled()) {
                            break;
                        }
                        if (monitor.getErred() != 0) {
                            throw new FlowTerminationRollbackRuntimeException();
                        }
                    }
                    return null;
                }
            });
        } catch (FlowTerminationRollbackRuntimeException ok) {
        }

        completed = true;
    }
}
