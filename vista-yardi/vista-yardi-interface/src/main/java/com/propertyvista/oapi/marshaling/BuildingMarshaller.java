/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.BuildingRS;

public class BuildingMarshaller implements Marshaller<Building, BuildingRS> {

    @Override
    public BuildingRS unmarshal(Building building) {
        BuildingRS buildingRS = new BuildingRS();
        buildingRS.propertyCode = building.propertyCode().getValue();
        return buildingRS;
    }

    @Override
    public Building marshal(BuildingRS buildingRs) {
        return null;
    }

}
