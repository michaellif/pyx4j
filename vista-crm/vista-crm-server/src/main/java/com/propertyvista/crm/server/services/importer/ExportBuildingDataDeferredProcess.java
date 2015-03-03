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
 */
package com.propertyvista.crm.server.services.importer;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.xml.XMLEntityWriter;
import com.pyx4j.entity.xml.XMLStringWriter;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.ExportBuildingDataRetriever;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;

@SuppressWarnings("serial")
public class ExportBuildingDataDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(ExportBuildingDataDeferredProcess.class);

    private final EntityQueryCriteria<Building> criteria;

    private String fileName;

    ExportBuildingDataDeferredProcess(EntityQueryCriteria<Building> criteria) {
        this.criteria = criteria;
    }

    @Override
    public void execute() {
        try {
            Persistence.service().startBackgroundProcessTransaction();

            fileName = "";
            ImportIO importIO = EntityFactory.create(ImportIO.class);
            ICursorIterator<Building> buildings = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            try {
                while (buildings.hasNext()) {
                    Building building = buildings.next();
                    try {
                        importIO.buildings().add(new ExportBuildingDataRetriever().getModel(building, progress));
                    } catch (Throwable t) {
                        log.error("Error converting building {}", building, t);
                        throw t;
                    }
                    fileName += building.propertyCode().getStringView();
                }
            } finally {
                buildings.close();
            }

            createDownloadable(importIO);

        } finally {
            Persistence.service().endTransaction();
        }
        completed = true;
    }

    private void createDownloadable(ImportIO importIO) {
        fileName += ".xml";

        XMLStringWriter xml = new XMLStringWriter(StandardCharsets.UTF_8);
        XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
        xmlWriter.setEmitId(false);
        xmlWriter.write(importIO);

        Downloadable d = new Downloadable(xml.getBytes(), Downloadable.getContentType(DownloadFormat.XML));
        d.save(fileName);
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            return super.status();
        }
    }
}
