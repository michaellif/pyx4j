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

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.model.UnitIO;

public class UnitMarshaller implements Marshaller<AptUnit, UnitIO> {

    @Override
    public UnitIO unmarshal(AptUnit unit) {
        UnitIO unitIO = new UnitIO();
        unitIO.floorplanName = unit.floorplan().name().getValue();
        unitIO.number = unit.info().number().getValue();
        unitIO.baths = unit.floorplan().bathrooms().getValue();
        unitIO.beds = unit.floorplan().bedrooms().getValue();
        return unitIO;
    }

    @Override
    public AptUnit marshal(UnitIO unitRS) throws Exception {
        return null;
    }

}
