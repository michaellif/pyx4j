/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;

public interface PropertyService extends OAPIService {

    BuildingsIO getBuildings(String province);

    BuildingIO getBuildingByPropertyCode(String propertyCode);

    List<UnitIO> getAllUnitsByPropertyCode(String propertyCode, String floorplan);

    UnitIO getUnitByNumber(String propertyCode, String unitNumber);

    Response updateBuilding(BuildingIO buildingIO) throws Exception;

    Response updateUnit(String propertyCode, UnitIO unitIO) throws Exception;
}
