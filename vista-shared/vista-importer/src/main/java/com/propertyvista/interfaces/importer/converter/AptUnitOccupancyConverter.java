/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 20, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.interfaces.importer.model.AptUnitOccupancyIO;

public class AptUnitOccupancyConverter extends EntityDtoBinder<AptUnitOccupancySegment, AptUnitOccupancyIO> {

    public AptUnitOccupancyConverter() {
        super(AptUnitOccupancySegment.class, AptUnitOccupancyIO.class, false);
    }

    @Override
    protected void bind() {
        bind(dtoProto.dateFrom(), dboProto.dateFrom());
        bind(dtoProto.dateTo(), dboProto.dateTo());
        bind(dtoProto.status(), dboProto.status());
        bind(dtoProto.offMarket(), dboProto.offMarket());
    }
}
