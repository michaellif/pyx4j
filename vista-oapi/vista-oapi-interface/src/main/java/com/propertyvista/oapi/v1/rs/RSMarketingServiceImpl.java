/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 16, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.PropertySearchCriteria.BathroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.BedroomChoice;
import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.processing.MarketingServiceProcessor;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;
import com.propertyvista.oapi.v1.service.MarketingService;

@Path("marketing")
public class RSMarketingServiceImpl implements MarketingService {

    @GET
    @Path("getBuildings")
    @Produces(MediaType.APPLICATION_XML)
    public BuildingListIO getBuildings( //
            @QueryParam("city") String city, @QueryParam("province") String province, //
            @QueryParam("minBeds") BedroomChoice minBeds, @QueryParam("maxBeds") BedroomChoice maxBeds, //
            @QueryParam("minBaths") BathroomChoice minBaths, @QueryParam("maxBaths") BathroomChoice maxBaths, //
            @QueryParam("minPrice") Integer minPrice, @QueryParam("maxPrice") Integer maxPrice, //
            @QueryParam("amenities") Set<BuildingAmenity.Type> amenities//
    ) {
        PropertySearchCriteriaIO criteria = new PropertySearchCriteriaIO(city, province, minBeds, maxBeds, minBaths, maxBaths, minPrice, maxPrice, amenities);
        return getBuildingList(criteria);
    }

    @Override
    public BuildingListIO getBuildingList(PropertySearchCriteriaIO criteria) {
        return new MarketingServiceProcessor().getBuildingList(criteria);
    }

    @GET
    @Path("getPropertyInfo")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public BuildingIO getBuilding(@QueryParam("prId") String propertyId) {
        return new MarketingServiceProcessor().getPropertyInfo(propertyId);
    }

    @GET
    @Path("getFloorplanList")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public FloorplanListIO getFloorplanList(@QueryParam("prId") String propertyId) {
        return new MarketingServiceProcessor().getFloorplanList(propertyId);
    }

    @GET
    @Path("getFloorplanInfo")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public FloorplanIO getFloorplan(@QueryParam("prId") String propertyId, @QueryParam("fpId") String fpId) {
        return new MarketingServiceProcessor().getFloorplanInfo(propertyId, fpId);
    }

    @GET
    @Path("getFloorplanAvailability")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public List<FloorplanAvailabilityIO> getFloorplanAvailability(@QueryParam("prId") String prId, @QueryParam("fpId") String fpId,
            @QueryParam("moveIn") LogicalDate date) {
        return new MarketingServiceProcessor().getFloorplanAvailability(prId, fpId, date);
    }

    @POST
    @Path("requestAppointment")
    @Consumes(MediaType.APPLICATION_XML)
    @Override
    public void requestAppointment(AppointmentRequestIO request) {
        new MarketingServiceProcessor().requestAppointment(request);
    }

    @GET
    @Path("getApplyForLeaseUrl")
    @Override
    public String getApplyForLeaseUrl(@QueryParam("prId") String prId, @QueryParam("fpId") String fpId) {
        return new MarketingServiceProcessor().getApplyForLeaseUrl(prId, fpId);
    }
}
