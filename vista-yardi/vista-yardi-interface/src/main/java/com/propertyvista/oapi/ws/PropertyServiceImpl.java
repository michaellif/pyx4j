/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.propertyvista.oapi.model.BuildingRS;

@WebService(endpointInterface = "com.propertyvista.oapi.ws.PropertyService")
public class PropertyServiceImpl implements PropertyService {

    static private Map<String, BuildingRS> buildings = new HashMap<String, BuildingRS>();

    @Override
    public void createBuilding(BuildingRS building) {
        buildings.put(building.propertyCode, building);
    }

    @Override
    public BuildingRS getBuildingByPropertyCode(String propertyCode) {
        return buildings.get(propertyCode);
    }

    @Override
    public List<BuildingRS> getAllBuildings() {
        return new ArrayList<BuildingRS>(buildings.values());
    }

}
