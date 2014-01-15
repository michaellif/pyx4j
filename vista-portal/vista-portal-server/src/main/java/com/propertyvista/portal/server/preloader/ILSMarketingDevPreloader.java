/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 25, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import org.apache.commons.lang.math.RandomUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan.Priority;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class ILSMarketingDevPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        int numCreated = 0;

        ILSConfig ilsConfig = EntityFactory.create(ILSConfig.class);
        // add vendors
        for (ILSVendor vendor : new ILSVendor[] { ILSVendor.kijiji, ILSVendor.gottarent }) {
            ilsConfig.vendors().add(createVendorConfig(vendor, 10));

            // configure buildings/floorplans
            int i = 0;
            for (Building building : Persistence.service().query(EntityQueryCriteria.create(Building.class))) {
                Persistence.service().persist(createILSProfile(building, vendor));
                Persistence.service().retrieveMember(building.floorplans(), AttachLevel.IdOnly);
                for (Floorplan floorplan : building.floorplans()) {
                    Persistence.service().persist(createILSProfile(floorplan, vendor));
                }
            }
            numCreated++;
        }
        Persistence.service().persist(ilsConfig);

        // add available units
        for (AptUnit unit : Persistence.service().query(EntityQueryCriteria.create(AptUnit.class))) {
            if (unit.getPrimaryKey().asLong() % 2 == 0) {
                makeAvailable(unit);
            }
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " vendors");
        return b.toString();
    }

    private ILSVendorConfig createVendorConfig(ILSVendor vendor, int maxAds) {
        ILSVendorConfig ilsConfig = EntityFactory.create(ILSVendorConfig.class);
        ilsConfig.vendor().setValue(vendor);
        ilsConfig.maxDailyAds().setValue(maxAds);
        return ilsConfig;
    }

    private ILSProfileBuilding createILSProfile(Building building, ILSVendor vendor) {
        ILSProfileBuilding profile = EntityFactory.create(ILSProfileBuilding.class);
        profile.vendor().setValue(vendor);
        profile.building().set(building);
        if (vendor == ILSVendor.kijiji) {
            profile.maxAds().setValue(RandomUtils.nextInt(5));
        }
        return profile;
    }

    private ILSProfileFloorplan createILSProfile(Floorplan floorplan, ILSVendor vendor) {
        ILSProfileFloorplan profile = EntityFactory.create(ILSProfileFloorplan.class);
        profile.vendor().setValue(vendor);
        profile.floorplan().set(floorplan);
        profile.priority().setValue(Priority.High);
        return profile;
    }

    private AptUnit makeAvailable(final AptUnit unit) {
        if (unit._availableForRent().isNull()) {
            LogicalDate fromDate = getStatusFromDate(unit);
            if (fromDate != null) {
                SystemDateManager.setDate(fromDate);
                ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(unit.getPrimaryKey());
                SystemDateManager.resetDate();
            }
        }
        return Persistence.service().retrieve(AptUnit.class, unit.getPrimaryKey());
    }

    private LogicalDate getStatusFromDate(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.desc(criteria.proto().dateFrom());
        AptUnitOccupancySegment segment = Persistence.service().retrieve(criteria);

        if (segment == null || segment.status().getValue() != AptUnitOccupancySegment.Status.pending) {
            return null;
        } else {
            return new LogicalDate(segment.dateFrom().getValue());
        }
    }

    @Override
    public String delete() {
        // TODO Auto-generated method stub
        return null;
    }

}
