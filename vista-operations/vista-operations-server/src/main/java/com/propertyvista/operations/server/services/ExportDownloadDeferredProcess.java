/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.BuildingRetriever;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;
import com.propertyvista.operations.rpc.dto.PmcExportDownloadDTO;

public class ExportDownloadDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExportDownloadDeferredProcess.class);

    private final PmcExportDownloadDTO request;

    private int maximum = 0;

    private int fetchCount = 0;

    private String fileName;

    public ExportDownloadDeferredProcess(PmcExportDownloadDTO pmcExportDownloadDTO) {
        this.request = pmcExportDownloadDTO;
    }

    @Override
    public void execute() {
        try {
            Persistence.service().startBackgroundProcessTransaction();

            Pmc pmc = Persistence.service().retrieve(Pmc.class, request.pmcId().getValue());
            NamespaceManager.setNamespace(pmc.namespace().getValue());
            fileName = pmc.namespace().getValue() + "-export.xml";
            createExportData();
        } finally {
            Persistence.service().endTransaction();
        }
        completed = true;
    }

    private void createExportData() {
        MediaConfig mediaConfig = new MediaConfig();
        if (request.exportImages().getValue(Boolean.FALSE)) {
            mediaConfig.baseFolder = "data/export/images/" + NamespaceManager.getNamespace() + "/";
        } else {
            mediaConfig.baseFolder = null;
        }

        try {
            ImportIO importIO = EntityFactory.create(ImportIO.class);
            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

            if (!request.propertyManager().isNull()) {
                buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyManager().name(), request.propertyManager()));
            }
            buildingCriteria.asc(buildingCriteria.proto().propertyCode());

            maximum = Persistence.service().count(buildingCriteria);

            ICursorIterator<Building> buildings = Persistence.service().query(null, buildingCriteria, AttachLevel.Attached);
            try {
                while (buildings.hasNext()) {
                    Building building = buildings.next();
                    try {
                        importIO.buildings().add(new BuildingRetriever().getModel(building, mediaConfig));
                    } catch (Throwable t) {
                        log.error("Error converting building {}", building, t);
                        throw t;
                    }
                    fetchCount++;
                }
            } finally {
                buildings.close();
            }

            XMLStringWriter xml = new XMLStringWriter(StandardCharsets.UTF_8);
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
            xmlWriter.setEmitId(false);
            xmlWriter.write(importIO);

            Downloadable d = new Downloadable(xml.getBytes(), Downloadable.getContentType(DownloadFormat.XML));
            d.save(fileName);

        } catch (Throwable t) {
            log.error("Error converting data", t);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                throw new Error("Internal error", t);
            } else {
                throw new Error("Internal error");
            }
        }
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
            r.setProgress(fetchCount);
            r.setProgressMaximum(maximum);
            return r;
        }
    }

}
