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
package com.propertyvista.admin.server.qa;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
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
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            super.execute();
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    protected void createHeaderEnds() {
        formater.header("BuildingId");
        formater.header("FloorplanId");
        formater.header("ImageId");
        formater.header("ImageCaption");
        super.createHeaderEnds();
    }

    @Override
    protected void reportEntity(Pmc entity) {
        try {
            NamespaceManager.setNamespace(entity.namespace().getValue());
            exportPmcBuildings(entity);
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
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
            for (Media media : building.media()) {
                exportPmcBuildingMedia(pmc, building, null, media);
            }

            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);
            nextFloorplan: for (Floorplan floorplan : floorplans) {
                Persistence.service().retrieve(floorplan.media());
                for (Media media : floorplan.media()) {
                    exportPmcBuildingMedia(pmc, building, floorplan, media);
                }
                //Get only one Floorplan
                break nextFloorplan;
            }
        }
    }

    private void exportPmcBuildingMedia(Pmc pmc, Building building, Floorplan floorplan, Media media) {
        if (!PublicVisibilityType.global.equals(media.visibility().getValue())) {
            return;
        }
        if (media.type().getValue() != Media.Type.file) {
            return;
        }

        super.reportEntity(pmc);
        formater.cell(building.getPrimaryKey());
        if (floorplan != null) {
            formater.cell(floorplan.getPrimaryKey());
        } else {
            formater.cell(null);
        }
        formater.cell(media.getPrimaryKey());
        formater.cell(media.caption().getValue());
        formater.newRow();
    }
}
