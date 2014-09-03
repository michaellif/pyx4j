/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 12, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.service;

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.v1.model.AppointmentRequestIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.FloorplanAvailabilityIO;
import com.propertyvista.oapi.v1.model.FloorplanIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.searchcriteria.PropertySearchCriteriaIO;

/*
 * TODO
 * PV: exclude building/fp from this listing via ILS marketing vendor?
 */
public interface MarketingService extends OAPIService {

    BuildingListIO getBuildingList(PropertySearchCriteriaIO criteria);

    BuildingIO getBuilding(String propertyId);

    FloorplanListIO getFloorplanList(String propertyId);

    FloorplanIO getFloorplan(String propertyId, String fpId);

    List<FloorplanAvailabilityIO> getFloorplanAvailability(String propertyId, String fpId, LogicalDate date);

    void requestAppointment(AppointmentRequestIO request);

    String getApplyForLeaseUrl(String propertyId, String fpId);
}
