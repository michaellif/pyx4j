/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.InputSource;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.essentials.server.upload.UploadServiceImpl;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcImportDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.interfaces.importer.BuildingImporter;
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.server.domain.admin.Pmc;

public class ImportUploadServiceImpl extends UploadServiceImpl<PmcImportDTO> implements ImportUploadService {

    @Override
    public long getMaxSize(HttpServletRequest request) {
        return 5 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public Key onUploadRecived(UploadDeferredProcess process, UploadData data) {
        try {
            PmcImportDTO importDTO = (PmcImportDTO) process.getData();
            if (importDTO.id().isNull()) {
                throw new Error();
            }
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Pmc pmc = PersistenceServicesFactory.getPersistenceService().retrieve(Pmc.class, importDTO.id().getValue());
            if (pmc == null) {
                throw new Error("PMC Not found");
            }
            NamespaceManager.setNamespace(pmc.dnsName().getValue());

            String imagesBaseFolder = "data/export/images/";

            ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new ByteArrayInputStream(data.data)));
            process.status().setProgressMaximum(importIO.buildings().size());

            int count = 0;
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                if (importDTO.updateOnly().isBooleanTrue()) {
                    counters.add(new BuildingUpdater().update(building, imagesBaseFolder));
                } else {
                    counters.add(new BuildingImporter().persist(building, imagesBaseFolder));
                }
                count++;
                process.status().setProgress(count);
            }
            if (importDTO.updateOnly().isBooleanTrue()) {
                process.status().setMessage(SimpleMessageFormat.format("Updated {0} units in {1} building(s)", counters.units, counters.buildings));
            } else {
                process.status().setMessage(
                        SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", count, counters.floorplans, counters.units));
            }
            process.status().setCompleted();
        } finally {
            NamespaceManager.remove();
        }
        return null;
    }

}
