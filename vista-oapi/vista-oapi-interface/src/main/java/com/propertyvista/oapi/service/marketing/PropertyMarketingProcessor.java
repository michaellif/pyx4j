/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.service.marketing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.oapi.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.marshaling.FloorplanMarshaller;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.FloorplanIO;
import com.propertyvista.oapi.service.marketing.model.AppointmentRequest;
import com.propertyvista.oapi.service.marketing.model.FloorplanAvailability;
import com.propertyvista.oapi.service.marketing.model.WSPropertySearchCriteria;
import com.propertyvista.server.common.util.PropertyFinder;

public class PropertyMarketingProcessor {

    public List<BuildingIO> getPropertyList(WSPropertySearchCriteria criteria) {
        List<BuildingIO> result = new ArrayList<>();
        for (Building building : PropertyFinder.getPropertyList(criteria.getDbCriteria())) {
            result.add(BuildingMarshaller.getInstance().marshal(building));
        }
        return result;
    }

    public BuildingIO getPropertyInfo(String propertyId) {
        return BuildingMarshaller.getInstance().marshal(PropertyFinder.getBuildingDetails(propertyId));
    }

    public List<FloorplanIO> getFloorplanList(String propertyId) {
        List<FloorplanIO> result = new ArrayList<>();
        Building building = PropertyFinder.getBuildingDetails(propertyId);
        if (building != null) {
            for (Floorplan fp : PropertyFinder.getBuildingFloorplans(building).keySet()) {
                result.add(FloorplanMarshaller.getInstance().marshal(fp));
            }
        }
        return result;
    }

    public FloorplanIO getFloorplanInfo(String propertyId, String fpId) {
        EntityQueryCriteria<Floorplan> dbCriteria = EntityQueryCriteria.create(Floorplan.class);
        dbCriteria.eq(dbCriteria.proto().name(), fpId);
        dbCriteria.eq(dbCriteria.proto().building().propertyCode(), propertyId);
        Floorplan fp = Persistence.service().retrieve(dbCriteria);
        return fp == null ? null : FloorplanMarshaller.getInstance().marshal(PropertyFinder.getFloorplanDetails(fp.getPrimaryKey().asLong()));
    }

    public List<FloorplanAvailability> getFloorplanAvailability(String fpId, LogicalDate date) {
        List<FloorplanAvailability> availInfo = new ArrayList<>();
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().floorplan().name(), fpId);
        criteria.add(ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), AptUnitOccupancySegment.Status.available, date,
                DateUtils.addDays(date, 30)));
        criteria.sort(new Sort(criteria.proto().availability().availableForRent(), false));
        for (AptUnit unit : Persistence.service().query(criteria)) {
            FloorplanAvailability avail = new FloorplanAvailability();
            avail.floorplanName = fpId;
            avail.marketRent = unit.financial()._marketRent().getValue();
            avail.areaSqFeet = DomainUtil.getAreaInSqFeet(unit.info().area(), unit.info().areaUnits());
            avail.dateAvailable = unit.availability().availableForRent().getValue();
            availInfo.add(avail);
        }
        return availInfo;
    }

    public void requestAppointment(AppointmentRequest request) {
        // TODO Auto-generated method stub

    }

    public String getApplyForLeaseUrl(String propertyId, String fpId) {
        // TODO Auto-generated method stub
        return null;
    }

}
