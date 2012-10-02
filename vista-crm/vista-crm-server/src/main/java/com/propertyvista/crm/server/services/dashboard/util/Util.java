/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.util;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.crm.server.util.EntityDto2DboCriteriaConverter;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public class Util {

    /**
     * @return building stubs that are in portfolio of the current user
     */
    public static Vector<Building> enforcePortfolio(List<Building> buildingsFilter) {
        Vector<Building> enforcedBuildingsFilter = new Vector<Building>();

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        if (!buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().id(), buildingsFilter));
        }
        enforcedBuildingsFilter.addAll(Persistence.secureQuery(criteria, AttachLevel.IdOnly));
        return enforcedBuildingsFilter;
    }

    public static LogicalDate dayOfCurrentTransaction() {
        return new LogicalDate(Persistence.service().getTransactionSystemTime());
    }

    public static LogicalDate beginningOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate endOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate beginningOfNextMonth(LogicalDate dayInMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginningOfMonth(dayInMonth));
        cal.add(GregorianCalendar.MONTH, 1);
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate addDays(LogicalDate day, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.add(GregorianCalendar.DAY_OF_YEAR, days);
        return new LogicalDate(cal.getTime());
    }

    public static EntityListCriteria<LeaseDTO> toDtoLeaseCriteria(EntityQueryCriteria<Lease> criteria) {
        // WARNING: this binder is used in the REVERSE WAY
        EntityDtoBinder<LeaseDTO, Lease> binder = new EntityDtoBinder<LeaseDTO, Lease>(LeaseDTO.class, Lease.class) {
            @Override
            protected void bind() {
                bind((Class<IEntity>) dtoProto.getInstanceValueClass(), dboProto, dtoProto);
                // TODO investigate wtf is going on here and why 'leaseTo' is not binded by the previous line
                bind(dtoProto.leaseTo(), dboProto.leaseTo());
                // TODO same "wtf" that applies to the previous line
                bind(dtoProto.expectedMoveOut(), dboProto.expectedMoveOut());
                bind(dtoProto.unit().building(), dboProto.unit().building());
            }
        };
        EntityDto2DboCriteriaConverter<LeaseDTO, Lease> converter = new EntityDto2DboCriteriaConverter<LeaseDTO, Lease>(LeaseDTO.class, Lease.class,
                EntityDto2DboCriteriaConverter.makeMapper(binder));

        EntityListCriteria<LeaseDTO> criteriaDto = EntityListCriteria.create(LeaseDTO.class);
        criteriaDto.addAll(converter.convertDTOSearchCriteria(criteria.getFilters()));

        return criteriaDto;
    }

    public static EntityListCriteria<AptUnitDTO> toDtoUnitsCriteria(EntityQueryCriteria<AptUnit> criteria) {
        // WARNING: this binder is used in the REVERSE WAY: it binds DBO to DTO
        EntityDtoBinder<AptUnitDTO, AptUnit> binder = new EntityDtoBinder<AptUnitDTO, AptUnit>(AptUnitDTO.class, AptUnit.class) {
            @Override
            protected void bind() {
                bind(dtoProto._availableForRent(), dboProto._availableForRent());
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

    public static String asMoney(BigDecimal amount) {
        return MessageFormat.format("{0,number,currency}", amount);
    }

}
