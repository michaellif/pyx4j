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

import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.service.MarketingService;
import com.propertyvista.oapi.v1.service.marketing.model.AppointmentRequest;
import com.propertyvista.oapi.v1.service.marketing.model.FloorplanAvailability;
import com.propertyvista.oapi.v1.service.marketing.model.FloorplanList;
import com.propertyvista.oapi.v1.service.marketing.model.PropertyList;
import com.propertyvista.oapi.v1.service.marketing.model.WSPropertySearchCriteria;

@WebService
public interface WSMarketingServiceTestInterface extends MarketingService {

    @Override
    public PropertyList getPropertyList(@WebParam(name = "criteria") WSPropertySearchCriteria criteria);

    @Override
    public BuildingIO getPropertyInfo(@WebParam(name = "propertyId") String propertyId);

    @Override
    public FloorplanList getFloorplanList(@WebParam(name = "propertyId") String propertyId);

    @Override
    public FloorplanIO getFloorplanInfo(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId);

    @Override
    public List<FloorplanAvailability> getFloorplanAvailability(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId,
            @WebParam(name = "moveinDate") LogicalDate date);

    @Override
    public void requestAppointment(@WebParam(name = "request") AppointmentRequest request);

    @Override
    public String getApplyForLeaseUrl(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId);

}
