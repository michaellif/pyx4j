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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.oapi.model.FloorplanIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;
import com.propertyvista.server.common.util.PropertyFinder;

public class FloorplanMarshaller extends AbstractMarshaller<Floorplan, FloorplanIO> {

    private static class SingletonHolder {
        public static final FloorplanMarshaller INSTANCE = new FloorplanMarshaller();
    }

    private FloorplanMarshaller() {
    }

    public static FloorplanMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public FloorplanIO marshal(Floorplan fp) {
        if (fp == null || fp.isNull()) {
            return null;
        }
        FloorplanIO fpIO = new FloorplanIO();
        Persistence.ensureRetrieve(fp.building(), AttachLevel.Attached);
        fpIO.propertyCode = fp.building().propertyCode().getValue();
        fpIO.name = fp.name().getValue();
        fpIO.marketingName = createIo(StringIO.class, fp.marketingName());
        fpIO.description = createIo(StringIO.class, fp.description());
        fpIO.floorCount = createIo(IntegerIO.class, fp.floorCount());
        fpIO.bedrooms = createIo(IntegerIO.class, fp.bedrooms());
        fpIO.dens = createIo(IntegerIO.class, fp.dens());
        fpIO.bathrooms = createIo(IntegerIO.class, fp.bathrooms());
        fpIO.halfBath = createIo(IntegerIO.class, fp.halfBath());

        Persistence.ensureRetrieve(fp.amenities(), AttachLevel.Attached);
        fpIO.amenities = FloorplanAmenityMarshaller.getInstance().marshal(fp.amenities());
        Persistence.ensureRetrieve(fp.media(), AttachLevel.Attached);
        fpIO.medias = MediaMarshaller.getInstance().marshal(fp.media());

        // calculated values
        List<AptUnit> units = PropertyFinder.getFloorplanUnits(fp);
        MinMaxPair<BigDecimal> minMaxRent = PropertyFinder.getMinMaxMarketRent(units);
        MinMaxPair<Integer> minMaxArea = PropertyFinder.getMinMaxAreaInSqFeet(units);
        fpIO.rentFrom = createIo(BigDecimalIO.class, minMaxRent.getMin());
        fpIO.rentTo = createIo(BigDecimalIO.class, minMaxRent.getMax());
        fpIO.sqftFrom = createIo(IntegerIO.class, minMaxArea.getMin());
        fpIO.sqftTo = createIo(IntegerIO.class, minMaxArea.getMax());
        fpIO.availableFrom = createIo(LogicalDateIO.class, getDateAvailable(fp));
        return fpIO;
    }

    @Override
    public Floorplan unmarshal(FloorplanIO fpIO) {
        Floorplan fp = EntityFactory.create(Floorplan.class);
        fp.name().setValue(fpIO.name);

        // sanity check
        if (fpIO.propertyCode == null) {
            throw new Error("PropertyCode cannot be empty");
        }
        if (fpIO.name == null) {
            throw new Error("Floorplan Name cannot be empty.");
        }
        if (fpIO.bathrooms == null || fpIO.bathrooms.getValue() == null) {
            throw new Error("Floorplan bathrooms cannot be empty");
        }
        if (fpIO.bedrooms == null || fpIO.bedrooms.getValue() == null) {
            throw new Error("Floorplan bedrooms cannot be empty");
        }
        // building
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().propertyCode(), fpIO.propertyCode);
        List<Building> buildings = Persistence.service().query(criteria);
        if (buildings.size() > 0) {
            fp.building().set(buildings.get(0));
        } else {
            throw new Error("Building not found for the given property code");
        }

        setValue(fp.marketingName(), fpIO.marketingName);
        setValue(fp.description(), fpIO.description);
        setValue(fp.floorCount(), fpIO.floorCount);
        setValue(fp.bedrooms(), fpIO.bedrooms);
        setValue(fp.dens(), fpIO.dens);
        setValue(fp.bathrooms(), fpIO.bathrooms);
        setValue(fp.halfBath(), fpIO.halfBath);
        fp.media().addAll(MediaMarshaller.getInstance().unmarshal(fpIO.medias));
        fp.amenities().addAll(FloorplanAmenityMarshaller.getInstance().unmarshal(fpIO.amenities));

        return fp;
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