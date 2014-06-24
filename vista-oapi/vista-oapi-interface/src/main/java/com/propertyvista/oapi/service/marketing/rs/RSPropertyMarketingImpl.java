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
package com.propertyvista.oapi.service.marketing.rs;

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
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.FloorplanIO;
import com.propertyvista.oapi.service.marketing.PropertyMarketingProcessor;
import com.propertyvista.oapi.service.marketing.PropertyMarketingService;
import com.propertyvista.oapi.service.marketing.model.AppointmentRequest;
import com.propertyvista.oapi.service.marketing.model.FloorplanAvailability;
import com.propertyvista.oapi.service.marketing.model.FloorplanList;
import com.propertyvista.oapi.service.marketing.model.PropertyList;
import com.propertyvista.oapi.service.marketing.model.WSPropertySearchCriteria;

@Path("marketing")
public class RSPropertyMarketingImpl implements PropertyMarketingService {

    @GET
    @Path("getPropertyList")
    @Produces(MediaType.APPLICATION_XML)
    public PropertyList getPropertyList( //
            @QueryParam("city") String city, @QueryParam("province") String province, //
            @QueryParam("minBeds") BedroomChoice minBeds, @QueryParam("maxBeds") BedroomChoice maxBeds, //
            @QueryParam("minBaths") BathroomChoice minBaths, @QueryParam("maxBaths") BathroomChoice maxBaths, //
            @QueryParam("minPrice") Integer minPrice, @QueryParam("maxPrice") Integer maxPrice, //
            @QueryParam("amenities") Set<BuildingAmenity.Type> amenities//
    ) {
        WSPropertySearchCriteria criteria = new WSPropertySearchCriteria(city, province, minBeds, maxBeds, minBaths, maxBaths, minPrice, maxPrice, amenities);
        return getPropertyList(criteria);
    }

    @Override
    public PropertyList getPropertyList(WSPropertySearchCriteria criteria) {
        return new PropertyMarketingProcessor().getPropertyList(criteria);
    }

    @GET
    @Path("getPropertyInfo")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public BuildingIO getPropertyInfo(@QueryParam("prId") String propertyId) {
        return new PropertyMarketingProcessor().getPropertyInfo(propertyId);
    }

    @GET
    @Path("getFloorplanList")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public FloorplanList getFloorplanList(@QueryParam("prId") String propertyId) {
        return new PropertyMarketingProcessor().getFloorplanList(propertyId);
    }

    @GET
    @Path("getFloorplanInfo")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public FloorplanIO getFloorplanInfo(@QueryParam("prId") String propertyId, @QueryParam("fpId") String fpId) {
        return new PropertyMarketingProcessor().getFloorplanInfo(propertyId, fpId);
    }

    @GET
    @Path("getFloorplanAvailability")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public List<FloorplanAvailability> getFloorplanAvailability(@QueryParam("prId") String prId, @QueryParam("fpId") String fpId,
            @QueryParam("moveIn") LogicalDate date) {
        return new PropertyMarketingProcessor().getFloorplanAvailability(prId, fpId, date);
    }

    @POST
    @Path("requestAppointment")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public void requestAppointment(AppointmentRequest request) {
        new PropertyMarketingProcessor().requestAppointment(request);
    }

    @GET
    @Path("getApplyForLeaseUrl")
    @Override
    public String getApplyForLeaseUrl(@QueryParam("prId") String prId, @QueryParam("fpId") String fpId) {
        return new PropertyMarketingProcessor().getApplyForLeaseUrl(prId, fpId);
    }
}
