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
package com.propertyvista.oapi.v1.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;
import com.propertyvista.oapi.v1.service.MarketingService;

@WebService
public interface WSMarketingServiceTestInterface extends MarketingService {

    @Override
    public BuildingListIO getBuildingList(@WebParam(name = "criteria") PropertySearchCriteriaIO criteria);

    @Override
    public BuildingIO getBuilding(@WebParam(name = "propertyId") String propertyId);

    @Override
    public FloorplanListIO getFloorplanList(@WebParam(name = "propertyId") String propertyId);

    @Override
    public FloorplanIO getFloorplan(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId);

    @Override
    public List<FloorplanAvailabilityIO> getFloorplanAvailability(@WebParam(name = "propertyId") String propertyId,
            @WebParam(name = "floorplanId") String fpId, @WebParam(name = "moveinDate") LogicalDate date);

    @Override
    public void requestAppointment(@WebParam(name = "request") AppointmentRequestIO request);

    @Override
    public String getApplyForLeaseUrl(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId);

}
