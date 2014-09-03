/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.DayPart;
import com.propertyvista.domain.tenant.lead.Lead.LeaseTerm;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.service.marketing.model.AppointmentRequest;
import com.propertyvista.oapi.v1.service.marketing.model.FloorplanAvailability;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.test.mock.models.BuildingDataModel;

public class RSMarketingServiceTest extends RSOapiTestBase {

    private Building building;

    private Floorplan fp;

    private AptUnit unit;

    @Override
    protected Class<? extends Application> getServiceApplication() {
        return OapiRsApplication.class;
    }

    @Override
    protected void preloadData() {
        super.preloadData();
        building = getBuilding();
        Persistence.ensureRetrieve(building.floorplans(), AttachLevel.Attached);
        Persistence.ensureRetrieve(building.units(), AttachLevel.Attached);
        if (building.floorplans().size() < 1) {
            // set building details
            fp = EntityFactory.create(Floorplan.class);
            fp.building().set(building);
            fp.name().setValue("2bdrm");
            Persistence.service().persist(fp);

            unit = EntityFactory.create(AptUnit.class);
            unit.building().set(building);
            unit.info().number().setValue("1");
            unit.floorplan().set(fp);
            Persistence.service().persist(unit);

            // create rent product
            getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(building, new BigDecimal("1000.00"), "1");

            // set unit availability
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(unit.getPrimaryKey());

            Persistence.service().commit();
        } else {
            throw new RuntimeException("Oops, Should not be here (" + NamespaceManager.getNamespace() + ")");
        }
    }

    @Test
    public void testGetBuildings() {
        BuildingListIO buildings = target("marketing/getBuildings").queryParam("province", building.info().address().province().getValue()).request()
                .get(BuildingListIO.class);
        Assert.assertEquals(1, buildings.buildingList.size());
    }

    @Test
    public void testGetPropertyInfo() {
        // in - String prId; out - BuildingIO
        BuildingIO buildingIO = target("marketing/getPropertyInfo") //
                .queryParam("prId", building.propertyCode().getValue()) //
                .request().get(BuildingIO.class);
        Assert.assertEquals(building.info().address().province().getValue(), buildingIO.info.address.province.getValue());
    }

    @Test
    public void testGetFloorplanList() {
        // in - String propertyId; out - FloorplanList
        FloorplanListIO floorplanList = target("marketing/getFloorplanList") //
                .queryParam("prId", building.propertyCode().getValue()) //
                .request().get(FloorplanListIO.class);
        Assert.assertEquals(1, floorplanList.floorplanList.size());
    }

    @Test
    public void testGetFloorplanInfo() {
        // in - String propertyId, String fpId; out - FloorplanIO
        FloorplanIO floorplanIO = target("marketing/getFloorplanInfo") //
                .queryParam("prId", building.propertyCode().getValue()) //
                .queryParam("fpId", fp.name().getValue()) //
                .request().get(FloorplanIO.class);
        Assert.assertEquals(fp.name().getValue(), floorplanIO.name);
    }

    @Test
    public void testGetFloorplanAvailability() {
        // in - String propertyId, String fpId, LogicalDate date; out - List<FloorplanAvailability>
        GenericType<List<FloorplanAvailability>> availListType = new GenericType<List<FloorplanAvailability>>() {
        };
        List<FloorplanAvailability> fpAvail = target("marketing/getFloorplanAvailability") //
                .queryParam("prId", building.propertyCode().getValue()) //
                .queryParam("fpId", fp.name().getValue()) //
                .queryParam("moveIn", unit.availability().availableForRent().getValue()) //
                .request().get(availListType);
        Assert.assertEquals(1, fpAvail.size());
    }

    @Test
    public void testRequestAppointment() {
        // in - AppointmentRequest request
        AppointmentRequest ar = new AppointmentRequest();
        ar.firstName = "John";
        ar.lastName = "Smith";
        ar.email = ar.firstName + "-" + ar.lastName + "-" + SystemDateManager.getTimeMillis() + "@pyx4j.com";
        ar.leaseTerm = LeaseTerm.months12;
        ar.propertyId = building.propertyCode().getValue();
        ar.floorplanId = fp.name().getValue();
        ar.preferredDate1 = SystemDateManager.getLogicalDate();
        ar.preferredTime1 = DayPart.Afternoon;
        target("marketing/requestAppointment").request().post(Entity.xml(ar));

        // retrieve the lead
        EntityQueryCriteria<Lead> crit = EntityQueryCriteria.create(Lead.class);
        crit.eq(crit.proto().guests().$().person().email(), ar.email);
        Assert.assertEquals(1, Persistence.service().query(crit).size());
    }

    @Test
    public void testGetApplyForLeaseUrl() {
        // in - String propertyId, String fpId; out - String
        String url = target("marketing/getApplyForLeaseUrl") //
                .queryParam("prId", building.propertyCode().getValue()) //
                .queryParam("fpId", fp.name().getValue()) //
                .request().get(String.class);
        StringBuilder params = new StringBuilder() //
                .append(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID).append("=").append(building.propertyCode().getValue()) //
                .append("&") //
                .append(ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID).append("=").append(fp.name().getValue());
        Assert.assertTrue(url.endsWith(params.toString()));
    }

}
