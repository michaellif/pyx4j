/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2014
 * @author michaellif
 */
package com.propertyvista.oapi.v1.processing;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.dto.PropertySearchCriteria;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.service.PortationService;
import com.propertyvista.server.common.util.PropertyFinder;

public class PortationServiceProcessor extends AbstractProcessor {

    public PortationServiceProcessor(ServiceType serviceType) {
        super(PortationService.class, serviceType);
    }

    public BuildingListIO exportBuildings() {
        return BuildingMarshaller.getInstance().marshalCollection(BuildingListIO.class,
                PropertyFinder.getPropertyList(EntityFactory.create(PropertySearchCriteria.class)));
    }
}
