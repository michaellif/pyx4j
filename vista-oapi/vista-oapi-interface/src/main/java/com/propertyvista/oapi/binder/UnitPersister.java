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

import com.propertyvista.domain.property.asset.unit.AptUnit;

public class UnitPersister extends AbstractPersister<AptUnit, AptUnit> {

    public UnitPersister() {
        super(AptUnit.class, AptUnit.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public AptUnit retrieve(AptUnit dto) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building().propertyCode(), dto.building().propertyCode());
        criteria.eq(criteria.proto().info().number(), dto.info().number());
        AptUnit unit = Persistence.service().retrieve(criteria);
        if (unit != null) {
            Persistence.service().retrieve(unit.floorplan());
            Persistence.service().retrieve(unit.building());
        }
        return unit;
    }

}