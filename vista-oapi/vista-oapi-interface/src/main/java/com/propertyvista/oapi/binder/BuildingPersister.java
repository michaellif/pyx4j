/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.oapi.binder;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.building.Building;

public class BuildingPersister extends AbstractPersister<Building, Building> {

    public BuildingPersister() {
        super(Building.class, Building.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public Building retrieve(Building dto) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().propertyCode(), dto.propertyCode());
        return Persistence.service().retrieve(criteria);
    }
}
