/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2014
 * @author vlads
 */
package com.propertyvista.interfaces.importer;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.BuildingIO;

class ImportProcessorContext {

    ExecutionMonitor monitor;

    BuildingIO buildingIO;

    Building building;

    Building renamedBuilding;

    boolean ignoreEntityId = false;

    public ImportProcessorContext(ExecutionMonitor monitor, BuildingIO buildingIO) {
        super();
        this.monitor = monitor;
        this.buildingIO = buildingIO;
    }

}
