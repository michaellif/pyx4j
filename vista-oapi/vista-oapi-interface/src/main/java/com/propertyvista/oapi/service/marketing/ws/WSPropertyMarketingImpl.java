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
package com.propertyvista.oapi.service.marketing.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.FloorplanIO;
import com.propertyvista.oapi.service.marketing.PropertyMarketingProcessor;
import com.propertyvista.oapi.service.marketing.PropertyMarketingService;
import com.propertyvista.oapi.service.marketing.model.AppointmentRequest;
import com.propertyvista.oapi.service.marketing.model.FloorplanAvailability;
import com.propertyvista.oapi.service.marketing.model.FloorplanList;
import com.propertyvista.oapi.service.marketing.model.PropertyList;
import com.propertyvista.oapi.service.marketing.model.WSPropertySearchCriteria;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class WSPropertyMarketingImpl implements PropertyMarketingService {

    @Override
    public PropertyList getPropertyList(@WebParam(name = "criteria") WSPropertySearchCriteria criteria) {
        return new PropertyMarketingProcessor().getPropertyList(criteria);
    }

    @Override
    public BuildingIO getPropertyInfo(@WebParam(name = "propertyId") String propertyId) {
        return new PropertyMarketingProcessor().getPropertyInfo(propertyId);
    }

    @Override
    public FloorplanList getFloorplanList(@WebParam(name = "propertyId") String propertyId) {
        return new PropertyMarketingProcessor().getFloorplanList(propertyId);
    }

    @Override
    public FloorplanIO getFloorplanInfo(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId) {
        return new PropertyMarketingProcessor().getFloorplanInfo(propertyId, fpId);
    }

    @Override
    public List<FloorplanAvailability> getFloorplanAvailability(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId,
            @WebParam(name = "moveinDate") LogicalDate date) {
        return new PropertyMarketingProcessor().getFloorplanAvailability(propertyId, fpId, date);
    }

    @Override
    public void requestAppointment(@WebParam(name = "request") AppointmentRequest request) {
        new PropertyMarketingProcessor().requestAppointment(request);
    }

    @Override
    public String getApplyForLeaseUrl(@WebParam(name = "propertyId") String propertyId, @WebParam(name = "floorplanId") String fpId) {
        return new PropertyMarketingProcessor().getApplyForLeaseUrl(propertyId, fpId);
    }

}
