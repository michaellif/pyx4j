/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.interfaces.importer.model.AptUnitIO;

public class AptUnitConverter extends EntityDtoBinder<AptUnit, AptUnitIO> {

    public AptUnitConverter() {
        super(AptUnit.class, AptUnitIO.class, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void bind() {
        bind(dtoProto.number(), dboProto.info().number());
        bind(dtoProto.area(), dboProto.info().area());
        bind(dtoProto.areaUnits(), dboProto.info().areaUnits());
        bind(dtoProto.unitRent(), dboProto.financial().unitRent());
        bind(dtoProto.availableForRent(), dboProto.availableForRent());
    }

    @Override
    public void copyDTOtoDBO(AptUnitIO dto, AptUnit dbo) {
        super.copyDTOtoDBO(dto, dbo);
        if (!dbo.info().area().isNull() && dbo.info().areaUnits().isNull()) {
            dbo.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);
        }
    }
}
