/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.qa;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

public class PmcDataImagesReportDeferredProcess extends SearchReportDeferredProcess<Pmc> {

    private static final long serialVersionUID = 1L;

    public PmcDataImagesReportDeferredProcess(ReportRequest request) {
        super(request);
    }

    @Override
    public void execute() {
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            super.execute();
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    protected void createHeaderEnds() {
        formatter.header("BuildingId");
        formatter.header("FloorplanId");
        formatter.header("ImageId");
        formatter.header("ImageCaption");
        super.createHeaderEnds();
    }

    @Override
    protected void reportEntity(Pmc entity) {
        try {
            NamespaceManager.setNamespace(entity.namespace().getValue());
            exportPmcBuildings(entity);
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        }
    }

    @Override
    protected void reportEntityEnds(Pmc entity) {
    }

    private void exportPmcBuildings(Pmc pmc) {
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildings = Persistence.service().query(buildingCriteria);
        for (Building building : buildings) {
            if (!PublicVisibilityType.global.equals(building.marketing().visibility().getValue())) {
                continue;
            }

            Persistence.service().retrieve(building.media());
            for (MediaFile media : building.media()) {
                exportPmcMediaFile(pmc, building, null, media);
            }

            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);
            nextFloorplan: for (Floorplan floorplan : floorplans) {
                Persistence.service().retrieve(floorplan.media());
                for (MediaFile media : floorplan.media()) {
                    exportPmcMediaFile(pmc, building, floorplan, media);
                }
                //Get only one Floorplan
                break nextFloorplan;
            }
        }
    }

    private void exportPmcMediaFile(Pmc pmc, Building building, Floorplan floorplan, MediaFile media) {
        if (!PublicVisibilityType.global.equals(media.visibility().getValue())) {
            return;
        }

        super.reportEntity(pmc);
        formatter.cell(building.getPrimaryKey());
        if (floorplan != null) {
            formatter.cell(floorplan.getPrimaryKey());
        } else {
            formatter.cell(null);
        }
        formatter.cell(media.getPrimaryKey());
        formatter.cell(media.caption().getValue());
        formatter.newRow();
    }
}
