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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.domain.admin.Pmc;

public class PmcDataBuildingsReportDeferredProcess extends SearchReportDeferredProcess<Pmc> {

    private static final long serialVersionUID = 1L;

    public PmcDataBuildingsReportDeferredProcess(ReportRequest request) {
        super(request);
    }

    @Override
    public void execute() {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            super.execute();
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    protected void createHeaderEnds() {
        formater.header("Province");
        formater.header("City");
        formater.header("BuildingId");
        formater.header("PropertyCode");
        formater.header("FloorplanId");
        super.createHeaderEnds();
    }

    @Override
    protected void reportEntity(Pmc entity) {
        try {
            NamespaceManager.setNamespace(entity.namespace().getValue());
            exportPmcBuildings(entity);
        } finally {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
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
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().building(), building));
            List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);
            for (Floorplan floorplan : floorplans) {
                exportPmcBuilding(pmc, building, floorplan);
            }
        }
    }

    private void exportPmcBuilding(Pmc pmc, Building building, Floorplan floorplan) {
        super.reportEntity(pmc);
        formater.cell(escapeURI(building.info().address().province().name().getStringView()));
        formater.cell(escapeURI(building.info().address().city().getStringView()));
        formater.cell(building.getPrimaryKey());
        formater.cell(building.propertyCode().getStringView());
        formater.cell(floorplan.getPrimaryKey());
        formater.newRow();
    }

    private String escapeURI(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
