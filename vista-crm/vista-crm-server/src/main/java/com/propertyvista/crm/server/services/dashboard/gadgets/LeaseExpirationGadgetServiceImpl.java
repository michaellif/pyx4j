/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.GregorianCalendar;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeaseExpirationGadgetService;
import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta.LeaseFilter;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseExpirationGadgetServiceImpl implements LeaseExpirationGadgetService {

    @Override
    public void leaseExpriation(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildings) {

        LeaseExpirationGadgetDataDTO gadgetData = EntityFactory.create(LeaseExpirationGadgetDataDTO.class);

        gadgetData.numOfLeasesEndingThisMonth().setValue(Persistence.service().count(leaseFilterCriteria(buildings, LeaseFilter.THIS_MONTH)));
        gadgetData.numOfLeasesEndingNextMonth().setValue(Persistence.service().count(leaseFilterCriteria(buildings, LeaseFilter.NEXT_MONTH)));
        gadgetData.numOfLeasesEndingOver90Days().setValue(Persistence.service().count(leaseFilterCriteria(buildings, LeaseFilter.OVER_90_DAYS)));
        // TODO : set gadgetData.numOfLeasesOnMonthToMonth();

        gadgetData.unitsOccupied().setValue(numOfOccupiedUnits(buildings));

        int numOfUnits = numOfUnits(buildings);
        if (numOfUnits != 0) {
            gadgetData.unitOccupancyPct().setValue(100d * gadgetData.unitsOccupied().getValue() / numOfUnits);
        }

        callback.onSuccess(gadgetData);
    }

    @Override
    public void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter,
            LeaseExpirationGadgetMeta.LeaseFilter leaseFilter) {
        callback.onSuccess(toDtoLeaseCriteria(leaseFilterCriteria(buildingsFilter, leaseFilter)));
    }

    @Override
    public void makeOccupiedUnitsFilterCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, Vector<Building> buildingsFilter) {
        callback.onSuccess(toDtoOccuiedUnitsCriteria(occupiedUnitsCriteria(buildingsFilter)));
    }

    private EntityListCriteria<Lease> leaseFilterCriteria(Vector<Building> buildings, LeaseExpirationGadgetMeta.LeaseFilter leaseFilter) {

        LogicalDate leaseToLowerBound = null;
        LogicalDate leaseToUpperBound = null;
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());

        switch (leaseFilter) {
        case THIS_MONTH:
            leaseToLowerBound = beginningOfMonth(today);
            leaseToUpperBound = endOfMonth(today);
            break;
        case NEXT_MONTH:
            leaseToLowerBound = beginningOfNextMonth(today);
            leaseToUpperBound = endOfMonth(leaseToLowerBound);
            break;
        case OVER_90_DAYS:
            leaseToLowerBound = beginningOfNextMonth(beginningOfNextMonth(beginningOfNextMonth(today)));
            leaseToUpperBound = null;
            break;
        case MONTH_ON_MONTH:
            break;
        }

        EntityListCriteria<Lease> leaseCriteria = EntityListCriteria.create(Lease.class);
        if (buildings != null && !buildings.isEmpty()) {
            leaseCriteria.add(PropertyCriterion.in(leaseCriteria.proto().unit().building(), buildings));
        }
        if (leaseToLowerBound != null) {
            leaseCriteria.add(PropertyCriterion.ge(leaseCriteria.proto().leaseTo(), leaseToLowerBound));
        }
        if (leaseToUpperBound != null) {
            leaseCriteria.add(PropertyCriterion.le(leaseCriteria.proto().leaseTo(), leaseToUpperBound));
        }
        return leaseCriteria;
    }

    private LogicalDate beginningOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate endOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    private LogicalDate beginningOfNextMonth(LogicalDate dayInMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginningOfMonth(dayInMonth));
        cal.add(GregorianCalendar.MONTH, 1);
        return new LogicalDate(cal.getTime());
    }

    private int numOfOccupiedUnits(Vector<Building> buildings) {
        return Persistence.service().count(occupiedUnitsCriteria(buildings));
    }

    private EntityQueryCriteria<AptUnit> occupiedUnitsCriteria(Vector<Building> buildings) {
        LogicalDate when = new LogicalDate(Persistence.service().getTransactionSystemTime());

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);

        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }

        criteria.add(PropertyCriterion.le(criteria.proto().unitOccupancySegments().$().dateFrom(), when));
        criteria.add(PropertyCriterion.ge(criteria.proto().unitOccupancySegments().$().dateTo(), when));

        criteria.add(PropertyCriterion.eq(criteria.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.leased));

        return criteria;
    }

    private int numOfUnits(Vector<Building> buildings) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        if (buildings != null && !buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        return Persistence.service().count(criteria);
    }

    private EntityListCriteria<LeaseDTO> toDtoLeaseCriteria(EntityQueryCriteria<Lease> criteria) {
        // WARNING: this binder is used in the REVERSE WAY
        EntityDtoBinder<LeaseDTO, Lease> binder = new EntityDtoBinder<LeaseDTO, Lease>(LeaseDTO.class, Lease.class) {
            @Override
            protected void bind() {
                bind((Class<IEntity>) dtoProto.getInstanceValueClass(), dboProto, dtoProto);
                // TODO investigate wtf is going on here and why 'leaseTo' is not binded by the previous line
                bind(dtoProto.leaseTo(), dboProto.leaseTo());
            }
        };
        EntityDto2DboCriteriaConverter<LeaseDTO, Lease> converter = new EntityDto2DboCriteriaConverter<LeaseDTO, Lease>(LeaseDTO.class, Lease.class,
                EntityDto2DboCriteriaConverter.makeMapper(binder));

        EntityListCriteria<LeaseDTO> criteriaDto = EntityListCriteria.create(LeaseDTO.class);
        criteriaDto.addAll(converter.convertDTOSearchCriteria(criteria.getFilters()));

        return criteriaDto;
    }

    private EntityListCriteria<AptUnitDTO> toDtoOccuiedUnitsCriteria(EntityQueryCriteria<AptUnit> criteria) {
        // WARNING: this binder is used in the REVERSE WAY: it binds DBO to DTO
        EntityDtoBinder<AptUnitDTO, AptUnit> binder = new EntityDtoBinder<AptUnitDTO, AptUnit>(AptUnitDTO.class, AptUnit.class) {
            @Override
            protected void bind() {
                bind(dtoProto.building().propertyCode(), dboProto.buildingCode());
                bind(dtoProto.unitOccupancySegments().$().dateFrom(), dboProto.unitOccupancySegments().$().dateFrom());
                bind(dtoProto.unitOccupancySegments().$().dateTo(), dboProto.unitOccupancySegments().$().dateTo());
                bind(dtoProto.unitOccupancySegments().$().status(), dboProto.unitOccupancySegments().$().status());
                bind((Class<IEntity>) dtoProto.getValueClass(), dboProto, dtoProto);
            }
        };
        EntityDto2DboCriteriaConverter<AptUnitDTO, AptUnit> converter = new EntityDto2DboCriteriaConverter<AptUnitDTO, AptUnit>(AptUnitDTO.class,
                AptUnit.class, EntityDto2DboCriteriaConverter.makeMapper(binder));

        EntityListCriteria<AptUnitDTO> criteriaDto = EntityListCriteria.create(AptUnitDTO.class);
        criteriaDto.addAll(converter.convertDTOSearchCriteria(criteria.getFilters()));
        return criteriaDto;
    }
}
