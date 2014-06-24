/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.oapi.model.FloorplanInfoIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;
import com.propertyvista.server.common.util.PropertyFinder;

public class FloorplanInfoMarshaller implements Marshaller<Floorplan, FloorplanInfoIO> {

    private static class SingletonHolder {
        public static final FloorplanInfoMarshaller INSTANCE = new FloorplanInfoMarshaller();
    }

    private FloorplanInfoMarshaller() {
    }

    public static FloorplanInfoMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public FloorplanInfoIO marshal(Floorplan fp) {
        if (fp == null || fp.isNull()) {
            return null;
        }
        FloorplanInfoIO fpIO = new FloorplanInfoIO();
        fpIO.marketingName = MarshallerUtils.createIo(StringIO.class, fp.marketingName());
        fpIO.description = MarshallerUtils.createIo(StringIO.class, fp.description());
        fpIO.bedrooms = MarshallerUtils.createIo(IntegerIO.class, fp.bedrooms());
        fpIO.dens = MarshallerUtils.createIo(IntegerIO.class, fp.dens());
        fpIO.bathrooms = MarshallerUtils.createIo(IntegerIO.class, fp.bathrooms());
        fpIO.halfBath = MarshallerUtils.createIo(IntegerIO.class, fp.halfBath());

        // calculated values
        List<AptUnit> units = PropertyFinder.getFloorplanUnits(fp);
        MinMaxPair<BigDecimal> minMaxRent = PropertyFinder.getMinMaxMarketRent(units);
        MinMaxPair<Integer> minMaxArea = PropertyFinder.getMinMaxAreaInSqFeet(units);
        fpIO.rentFrom = new BigDecimalIO(minMaxRent.getMin());
        fpIO.sqftFrom = new IntegerIO(minMaxArea.getMin());
        fpIO.availableFrom = new LogicalDateIO(getDateAvailable(fp));
        return fpIO;
    }

    @Override
    public Floorplan unmarshal(FloorplanInfoIO fpIO) {
        throw new Error("Unsupported operation");
    }

    private LogicalDate getDateAvailable(Floorplan fp) {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.eq(criteria.proto().floorplan(), fp);
        criteria.add(ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), AptUnitOccupancySegment.Status.available,
                SystemDateManager.getDate(), null));
        criteria.sort(new Sort(criteria.proto().availability().availableForRent(), false));
        AptUnit unit = Persistence.service().retrieve(criteria);
        return unit == null ? null : unit.availability().availableForRent().getValue();
    }
}