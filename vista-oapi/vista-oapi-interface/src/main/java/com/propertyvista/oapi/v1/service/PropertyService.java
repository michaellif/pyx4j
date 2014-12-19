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
 */
package com.propertyvista.oapi.v1.service;

import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.UnitIO;
import com.propertyvista.oapi.v1.model.UnitListIO;

public interface PropertyService extends OAPIService {

    BuildingListIO getBuildingList(String province);

    BuildingIO getBuilding(String propertyCode);

    UnitListIO getUnitList(String propertyCode, String floorplan);

    UnitIO getUnitByNumber(String propertyCode, String unitNumber);

    void updateBuilding(BuildingIO buildingIO) throws Exception;

    void updateUnit(String propertyCode, UnitIO unitIO) throws Exception;
}
