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
package com.propertyvista.oapi.v1.processing;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.v1.marshaling.FloorplanMarshaller;
import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;
import com.propertyvista.oapi.v1.service.MarketingService;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.server.common.util.PropertyFinder;

public class MarketingServiceProcessor extends AbstractProcessor {

    public MarketingServiceProcessor(ServiceType serviceType) {
        super(MarketingService.class, serviceType);
    }

    public BuildingListIO getBuildingList(PropertySearchCriteriaIO criteria) {
        BuildingListIO result = new BuildingListIO();
        for (Building building : PropertyFinder.getPropertyList(criteria.getDbCriteria())) {
            result.buildingList.add(BuildingMarshaller.getInstance().marshal(building));
        }
        return result;
    }

    public BuildingIO getPropertyInfo(String propertyId) {
        return BuildingMarshaller.getInstance().marshal(PropertyFinder.getBuildingDetails(propertyId));
    }

    public FloorplanListIO getFloorplanList(String propertyId) {
        FloorplanListIO result = new FloorplanListIO();
        Building building = PropertyFinder.getBuildingDetails(propertyId);
        if (building != null) {
            for (Floorplan floorplan : PropertyFinder.getBuildingFloorplans(building).keySet()) {
                result.floorplanList.add(FloorplanMarshaller.getInstance().marshal(floorplan));
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

    public List<FloorplanAvailabilityIO> getFloorplanAvailability(String propertyId, String fpId, LogicalDate date) {
        if (date == null) {
            date = SystemDateManager.getLogicalDate();
        }
        List<FloorplanAvailabilityIO> availInfo = new ArrayList<>();
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().building().propertyCode(), propertyId);
        criteria.eq(criteria.proto().floorplan().name(), fpId);
        criteria.add(ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), AptUnitOccupancySegment.Status.available, date,
                null));
        criteria.sort(new Sort(criteria.proto().availability().availableForRent(), false));
        for (AptUnit unit : Persistence.service().query(criteria)) {
            FloorplanAvailabilityIO avail = new FloorplanAvailabilityIO();
            avail.floorplanName = fpId;
            avail.marketRent = unit.financial()._marketRent().getValue();
            avail.areaSqFeet = DomainUtil.getAreaInSqFeet(unit.info().area(), unit.info().areaUnits());
            avail.dateAvailable = unit.availability().availableForRent().getValue();
            availInfo.add(avail);
        }
        return availInfo;
    }

    public void requestAppointment(AppointmentRequestIO request) {
        // TODO - use javax.validation for bean validation purposes, @NotNull, etc ?
        // for (ConstraintViolation<BeanClass> error : Validation.buildDefaultValidatorFactory.getValidator().validate(bean)) {
        //     ... notify about the wrong field, etc
        // }

        Lead lead = EntityFactory.create(Lead.class);
        lead.status().setValue(Lead.Status.active);
        // floorplan
        EntityQueryCriteria<Floorplan> fpCrit = EntityQueryCriteria.create(Floorplan.class);
        fpCrit.eq(fpCrit.proto().building().propertyCode(), request.propertyId);
        fpCrit.eq(fpCrit.proto().name(), request.floorplanId);
        Floorplan fp = Persistence.service().retrieve(fpCrit);
        if (fp == null) {
            throw new Error("Requested floorplan is not found");
        }
        lead.floorplan().set(fp);
        // copy details from request
        lead.moveInDate().setValue(request.moveInDate);
        lead.leaseTerm().setValue(request.leaseTerm);
        lead.comments().setValue(request.comments);
        lead.appointmentDate1().setValue(request.preferredDate1);
        lead.appointmentTime1().setValue(request.preferredTime1);
        lead.appointmentDate2().setValue(request.preferredDate2);
        lead.appointmentTime2().setValue(request.preferredTime2);
        // add guest
        Guest guest = EntityFactory.create(Guest.class);
        guest.person().name().namePrefix().setValue(request.namePrefix);
        guest.person().name().firstName().setValue(request.firstName);
        guest.person().name().lastName().setValue(request.lastName);
        guest.person().homePhone().setValue(request.homePhone);
        guest.person().mobilePhone().setValue(request.mobilePhone);
        guest.person().workPhone().setValue(request.workPhone);
        guest.person().email().setValue(request.email);
        lead.guests().add(guest);
        // save
        lead.createDate().setValue(SystemDateManager.getLogicalDate());
        ServerSideFactory.create(LeadFacade.class).init(lead);
        ServerSideFactory.create(LeadFacade.class).persist(lead);
        Persistence.service().commit();
    }

    public String getApplyForLeaseUrl(String propertyId, String fpId) {
        String applyUrl = AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true), true, null,
                ProspectPortalSiteMap.ARG_ILS_BUILDING_ID, propertyId, ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID, fpId);
        return applyUrl;
    }

}
