/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils;

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;

public interface ILSTestMockConfiguration {

    public interface ILSBuildingMockConfiguration {

        public List<BuildingAmenity> getBuildingAmenities();

        public List<ILSFloorPlanMockConfiguration> getFloorPlanConfiguration();

        public short getFloorplansInBuildingNumber();

        public boolean getUseMarketingAddress();
    }

    public interface ILSFloorPlanMockConfiguration {
        public short getBedrooms();

        public short getBathrooms();

        public short getHalfBathrooms();

        public ILSProfileFloorplan.Priority getFloorplanPriority();

        public List<FloorplanAmenity> getFloorPlanAmenities();

        public short getUnitsInFloorplan();

        public List<LogicalDate> getAvailabilityDates();
    }

    public ILSVendor getVendor();

    public short getBuildingsNumber();

    public List<ILSBuildingMockConfiguration> getBuildingConfiguration();

    public short getMaxDailyAdsConfig();
}
