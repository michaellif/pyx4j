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
package com.propertyvista.oapi.service.marketing.rs;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.ws.rs.core.Application;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.rs.RSOapiTestBase;
import com.propertyvista.oapi.service.marketing.model.PropertyList;
import com.propertyvista.test.mock.models.BuildingDataModel;

public class RSPropertyMarketingTest extends RSOapiTestBase {

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
        Persistence.ensureRetrieve(building.floorplans(), AttachLevel.IdOnly);
        Persistence.ensureRetrieve(building.units(), AttachLevel.IdOnly);
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

            getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(building, new BigDecimal("1000.00"), "1");

            Persistence.service().commit();
        } else {
            fp = new ArrayList<Floorplan>(building.floorplans()).get(0);
            unit = new ArrayList<AptUnit>(building.units()).get(0);
        }
    }

    @Test
    public void testGetBuildings() {
        PropertyList propertyList = target("marketing/getPropertyList").queryParam("province", "Ontario").request().get(PropertyList.class);
        Assert.assertEquals(1, propertyList.items.size());
    }
}
