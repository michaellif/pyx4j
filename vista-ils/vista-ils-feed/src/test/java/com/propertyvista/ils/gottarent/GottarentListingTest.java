/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent;

import javax.xml.bind.JAXBException;

import org.junit.experimental.categories.Category;

import com.gottarent.rs.Listing;
import com.gottarent.rs.ObjectFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.occupancy.ILSGottarentIntegrationAgent;
import com.propertyvista.domain.marketing.MarketingContacts;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.ils.ILSTestBase;
import com.propertyvista.ils.gottarent.mapper.GottarentDataMapper;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class GottarentListingTest extends ILSTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        Building building = getBuilding();

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

    public void testScenario() {
        try {
            // fetch relevant data and prepare gottarent xml
            Listing listing = generateData();

            if (hasData(listing)) {
                // update gottarent server
                GottarentClient.updateGottarent("UserId", listing);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private boolean hasData(Listing listing) {
        return listing != null && listing.getCompany() != null && listing.getCompany().getPortfolio() != null
                && listing.getCompany().getPortfolio().getBuilding() != null && listing.getCompany().getPortfolio().getBuilding().size() > 0;
    }

    private Listing generateData() throws JAXBException {

        ILSReportDTO ilsReport = new ILSGottarentIntegrationAgent().getUnitListing();
        assertTrue("No Units found", ilsReport.totalUnits().getValue() > 0);

        return new GottarentDataMapper(new ObjectFactory()).createListing(ilsReport);
    }

}
