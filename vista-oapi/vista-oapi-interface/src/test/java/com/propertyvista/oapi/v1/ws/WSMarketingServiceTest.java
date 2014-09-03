/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.ws;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

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
import com.propertyvista.oapi.v1.Version;
import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.test.mock.models.BuildingDataModel;

public class WSMarketingServiceTest extends WSOapiTestBase {

    private WSMarketingServiceTestInterface service;

    private Building building;

    private Floorplan fp;

    private AptUnit unit;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create server. The server will run in a separate thread, so we need to provide namespace and Persistence handling
        final String namespace = NamespaceManager.getNamespace();
        publish(WSMarketingServiceImpl.class, new LogicalHandler<LogicalMessageContext>() {

            @Override
            public boolean handleMessage(LogicalMessageContext context) {
                // handler is called twice - on request and on response; we use the first call
                if (NamespaceManager.getNamespace() == null) {
                    NamespaceManager.setNamespace(namespace);
                    Persistence.service().startBackgroundProcessTransaction();
                }
                return true;
            }

            @Override
            public boolean handleFault(LogicalMessageContext context) {
                return true;
            }

            @Override
            public void close(MessageContext context) {
                if (Persistence.service().getTransactionScopeOption() != null) {
                    Persistence.service().commit();
                    Persistence.service().endTransaction();
                }
            }
        });

        String nsUrl = "http://ws." + Version.VERSION_NAME + ".oapi.propertyvista.com/";
        QName svcName = new QName(nsUrl, WSMarketingServiceImpl.class.getSimpleName() + "Service");
        QName portName = new QName(nsUrl, WSMarketingServiceImpl.class.getSimpleName() + "Port");
        service = Service.create(new URL(getAddress()), svcName).getPort(portName, WSMarketingServiceTestInterface.class);

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
        PropertySearchCriteriaIO crit = new PropertySearchCriteriaIO();
        crit.province = "Ontario";
        Assert.assertEquals(1, service.getBuildingList(crit).buildingList.size());
    }

    @Test
    public void testGetPropertyInfo() {
        BuildingIO buildingIO = service.getBuilding(building.propertyCode().getValue());
        Assert.assertEquals(building.info().address().province().getValue(), buildingIO.address.province.getValue());
    }

    @Test
    public void testGetFloorplanList() {
        FloorplanListIO floorplanList = service.getFloorplanList(building.propertyCode().getValue());
        Assert.assertEquals(1, floorplanList.getList().size());
    }

    @Test
    public void testGetFloorplanInfo() {
        FloorplanIO floorplanIO = service.getFloorplan(building.propertyCode().getValue(), fp.name().getValue());
        Assert.assertEquals(fp.name().getValue(), floorplanIO.name);
    }

    @Test
    public void testGetFloorplanAvailability() {
        List<FloorplanAvailabilityIO> fpAvail = service.getFloorplanAvailability( //
                building.propertyCode().getValue(), //
                fp.name().getValue(), //
                unit.availability().availableForRent().getValue() //
                );
        Assert.assertEquals(1, fpAvail.size());
    }

    @Test
    public void testRequestAppointment() {
        AppointmentRequestIO ar = new AppointmentRequestIO();
        ar.firstName = "John";
        ar.lastName = "Smith";
        ar.email = ar.firstName + "-" + ar.lastName + "-" + SystemDateManager.getTimeMillis() + "@pyx4j.com";
        ar.leaseTerm = LeaseTerm.months12;
        ar.propertyId = building.propertyCode().getValue();
        ar.floorplanId = fp.name().getValue();
        ar.preferredDate1 = SystemDateManager.getLogicalDate();
        ar.preferredTime1 = DayPart.Afternoon;
        service.requestAppointment(ar);

        // retrieve the lead
        EntityQueryCriteria<Lead> crit = EntityQueryCriteria.create(Lead.class);
        crit.eq(crit.proto().guests().$().person().email(), ar.email);
        Assert.assertEquals(1, Persistence.service().query(crit).size());
    }

    @Test
    public void testGetApplyForLeaseUrl() {
        String url = service.getApplyForLeaseUrl(building.propertyCode().getValue(), fp.name().getValue());
        StringBuilder params = new StringBuilder() //
                .append(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID).append("=").append(building.propertyCode().getValue()) //
                .append("&") //
                .append(ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID).append("=").append(fp.name().getValue());
        Assert.assertTrue(url.endsWith(params.toString()));
    }

}
