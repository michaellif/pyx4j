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
    public UnitIO marshal(AptUnit unit) {
        if (unit == null || unit.isNull()) {
            return null;
        }
        UnitIO unitIO = new UnitIO();
        unitIO.number = MarshallerUtils.getValue(unit.info().number());

        unitIO.floorplanName = MarshallerUtils.createIo(StringIO.class, unit.floorplan().name());
        unitIO.baths = MarshallerUtils.createIo(IntegerIO.class, unit.floorplan().bathrooms());
        unitIO.beds = MarshallerUtils.createIo(IntegerIO.class, unit.floorplan().bedrooms());
        return unitIO;
    }

    @Override
    public AptUnit unmarshal(UnitIO unitIO) throws Exception {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue(unitIO.number);

        // EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        // floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), unitIO.floorplanName.value));
        // floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().bathrooms(), unitIO.baths.value));
        // floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().bedrooms(), unitIO.beds.value));
        // List<Floorplan> floorplans = Persistence.service().query(floorplanCriteria);

        MarshallerUtils.setValue(unit.floorplan().name(), unitIO.floorplanName);
        MarshallerUtils.setValue(unit.floorplan().bathrooms(), unitIO.baths);
        MarshallerUtils.setValue(unit.floorplan().bedrooms(), unitIO.beds);
        return unit;
    }
}