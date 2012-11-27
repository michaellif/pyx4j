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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class UnitMarshaller implements Marshaller<AptUnit, UnitIO> {

    private static class SingletonHolder {
        public static final UnitMarshaller INSTANCE = new UnitMarshaller();
    }

    private UnitMarshaller() {
    }

    public static UnitMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public UnitIO unmarshal(AptUnit unit) {
        UnitIO unitIO = new UnitIO();
        unitIO.floorplanName = new StringIO(unit.floorplan().name().getValue());
        unitIO.number = unit.info().number().getValue();
        unitIO.baths = new IntegerIO(unit.floorplan().bathrooms().getValue());
        unitIO.beds = new IntegerIO(unit.floorplan().bedrooms().getValue());
        return unitIO;
    }

    @Override
    public AptUnit marshal(UnitIO unitIO) throws Exception {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.floorplan().name().setValue(unitIO.floorplanName.value);
        unit.info().number().setValue(unitIO.number);
        unit.floorplan().bathrooms().setValue(unitIO.baths.value);
        unit.floorplan().bedrooms().setValue(unitIO.beds.value);
        return unit;
    }
}
