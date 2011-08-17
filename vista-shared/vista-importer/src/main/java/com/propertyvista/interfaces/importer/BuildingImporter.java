/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.entity.server.PersistenceServicesFactory;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.interfaces.importer.converter.BuildingConverter;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class BuildingImporter {

    public void persist(BuildingIO buildingIO) {
        // Set defaults
        if (buildingIO.type().isNull()) {
            buildingIO.type().setValue(BuildingInfo.Type.residential);
        }

        // Save building
        Building building = new BuildingConverter().createDBO(buildingIO);
        PersistenceServicesFactory.getPersistenceService().persist(building);
    }
}
