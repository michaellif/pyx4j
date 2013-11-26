/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.mock;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.MarketingContacts;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.test.mock.MockDataModel;

public class ILSProfileBuildingDataModel extends MockDataModel<ILSProfileBuilding> {

    @Override
    protected void generate() {
    }

    public void fillProfiles(Building building) {

        ILSProfileBuilding profileBuilding = EntityFactory.create(ILSProfileBuilding.class);
        profileBuilding.building().set(building);
        profileBuilding.disabled().setValue(Boolean.FALSE);
        profileBuilding.vendor().setValue(ILSVendor.kijiji);
        MarketingContacts mc = EntityFactory.create(MarketingContacts.class);
        mc.email().value().setValue("a@b.com");
        mc.phone().value().setValue("987-654-3210");
        profileBuilding.preferredContacts().set(mc);
        Persistence.service().persist(profileBuilding);

        for (Floorplan f : building.floorplans()) {
            ILSProfileFloorplan floorplan = EntityFactory.create(ILSProfileFloorplan.class);
            floorplan.floorplan().set(f);
            floorplan.vendor().setValue(ILSVendor.kijiji);
            Persistence.service().persist(floorplan);
        }
    }

}
