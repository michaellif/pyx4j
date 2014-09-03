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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.oapi.v1.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.processing.MarketingServiceProcessor;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;
import com.propertyvista.oapi.v1.service.MarketingService;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class WSMarketingServiceImpl implements MarketingService {

    @Override
    public BuildingListIO getBuildingList(@WebParam(name = "criteria") PropertySearchCriteriaIO criteria) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.List);
        try {
            return processor.getBuildingList(criteria);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public BuildingIO getBuilding(@WebParam(name = "propertyId") String propertyId) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.Read);
        try {
            return processor.getPropertyInfo(propertyId);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public FloorplanListIO getFloorplanList(@WebParam(name = "propertyId") String propertyId) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.Read);
        try {
            return processor.getFloorplanList(propertyId);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public FloorplanIO getFloorplan(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.Read);
        try {
            return processor.getFloorplanInfo(propertyId, fpId);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public List<FloorplanAvailabilityIO> getFloorplanAvailability(@WebParam(name = "propertyId") String propertyId,
            @WebParam(name = "floorplanId") String fpId, @WebParam(name = "moveinDate") LogicalDate date) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.List);
        try {
            return processor.getFloorplanAvailability(propertyId, fpId, date);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public void requestAppointment(@WebParam(name = "request") AppointmentRequestIO request) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.Write);
        try {
            processor.requestAppointment(request);
        } finally {
            processor.destroy();
        }
    }

    @Override
    public String getApplyForLeaseUrl(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId) {
        MarketingServiceProcessor processor = new MarketingServiceProcessor(ServiceType.Read);
        try {
            return processor.getApplyForLeaseUrl(propertyId, fpId);
        } finally {
            processor.destroy();
        }
    }

}
