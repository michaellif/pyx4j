/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.UnitAvailabilityStatusDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilityStatusListService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.common.AsOfDateCriterion;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitAvailabilityStatusListServiceImpl extends AbstractListServiceDtoImpl<UnitAvailabilityStatus, UnitAvailabilityStatusDTO> implements
        UnitAvailabilityStatusListService {

    public interface LeasedStatusProvider {

        boolean isLeasedOn(LogicalDate asOfDate, AptUnit unitStub);

    }

    private final LeasedStatusProvider leasedStatusProvider;

    // TODO not a good thing but i'm afraid there was no other choice
    private LogicalDate asOfDate;

    public UnitAvailabilityStatusListServiceImpl(LeasedStatusProvider leasedStatusProvider) {
        super(UnitAvailabilityStatus.class, UnitAvailabilityStatusDTO.class);
        this.leasedStatusProvider = leasedStatusProvider;
    }

    public UnitAvailabilityStatusListServiceImpl() {
        this(new LeasedStatusProvider() {
            @Override
            public boolean isLeasedOn(LogicalDate asOfDate, AptUnit unitStub) {
                EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitStub));
                criteria.add(PropertyCriterion.ge(criteria.proto().leaseFrom(), asOfDate));
                criteria.add(new OrCriterion(PropertyCriterion.le(criteria.proto().leaseTo(), asOfDate), PropertyCriterion.isNull(criteria.proto().leaseTo())));
                criteria.add(new OrCriterion(PropertyCriterion.eq(criteria.proto().completion(), Lease.CompletionType.Notice), PropertyCriterion
                        .isNull(criteria.proto().completion())));
                return Persistence.service().count(criteria) != 0;
            }
        });
    }

    @Override
    protected void bind() {
        // REFERENCES        
        bind(dtoProto.propertyCode(), dboProto.building().propertyCode());
        bind(dtoProto.externalId(), dboProto.building().externalId());
        bind(dtoProto.buildingName(), dboProto.building().info().name());
        bind(dtoProto.address(), dboProto.building().info().address());
        bind(dtoProto.propertyManager(), dboProto.building().propertyManager().name());
        bind(dtoProto.complex(), dboProto.building().complex().name());
        bind(dtoProto.unit(), dboProto.unit().info().number());
        bind(dtoProto.floorplanName(), dboProto.floorplan().name());
        bind(dtoProto.floorplanMarketingName(), dboProto.floorplan().marketingName());

        // STATUS DATA    
        bind(dtoProto.statusFrom(), dboProto.statusFrom());
        bind(dtoProto.statusUntil(), dboProto.statusUntil());
        bind(dtoProto.vacancyStatus(), dboProto.vacancyStatus());
        bind(dtoProto.rentedStatus(), dboProto.rentedStatus());
        bind(dtoProto.scoping(), dboProto.scoping());
        bind(dtoProto.rentReadinessStatus(), dboProto.rentReadinessStatus());
        bind(dtoProto.unitRent(), dboProto.unitRent());
        bind(dtoProto.marketRent(), dboProto.marketRent());
        bind(dtoProto.rentDeltaAbsolute(), dboProto.rentDeltaAbsolute());
        bind(dtoProto.rentDeltaRelative(), dboProto.rentDeltaRelative());
        bind(dtoProto.rentEndDay(), dboProto.rentEndDay());
        bind(dtoProto.vacantSince(), dboProto.vacantSince());
        bind(dtoProto.rentedFromDay(), dboProto.rentedFromDay());
        bind(dtoProto.moveInDay(), dboProto.moveInDay());

        // BUISNESS DATA
        bind(dtoProto.unitId(), dboProto.unit().id());
        bind(dtoProto.buildingsFilterAnchor(), dboProto.building());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>> callback, EntityListCriteria<UnitAvailabilityStatusDTO> dtoCriteria) {
        // TODO this is not good way (horrible) for transferring to enhancelistRetrieved
        asOfDate = null;
        Iterator<Criterion> criteriaIterator = dtoCriteria.getFilters().iterator();
        while (criteriaIterator.hasNext()) {
            Criterion criterion = criteriaIterator.next();
            if (criterion instanceof AsOfDateCriterion) {
                criteriaIterator.remove();
                asOfDate = ((AsOfDateCriterion) criterion).getAsOfDate();
                break;
            }
        }
        if (asOfDate == null) {
            throw new IllegalArgumentException("Criteria must contain " + AsOfDateCriterion.class.getName());
        }

        dtoCriteria.add(PropertyCriterion.le(dtoCriteria.proto().statusFrom(), asOfDate));
        dtoCriteria.add(PropertyCriterion.ge(dtoCriteria.proto().statusUntil(), asOfDate));

        dtoCriteria.add(PropertyCriterion.isNotNull(dtoCriteria.proto().vacancyStatus()));

        super.list(callback, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(UnitAvailabilityStatus entity, UnitAvailabilityStatusDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        // calculate 'days vacant' and 'revenue lost'
        if (dto.vacancyStatus().getValue() == Vacancy.Vacant & !dto.vacantSince().isNull()) {

            LogicalDate vacantSince = dto.vacantSince().getValue();
            dto.daysVacant().setValue(1 + (int) Math.ceil(((asOfDate.getTime() - vacantSince.getTime())) / (1000.0 * 60.0 * 60.0 * 24.0)));

            // if that unit is still leased, i.e. then it's not losing money            

            if (!dto.marketRent().isNull()) {
                if (!leasedStatusProvider.isLeasedOn(asOfDate, entity.unit().<AptUnit> createIdentityStub())) {
                    BigDecimal revenueLost = new BigDecimal(dto.daysVacant().getValue()).multiply(dto.marketRent().getValue(), MathContext.DECIMAL128).divide(
                            new BigDecimal(30), MathContext.DECIMAL128);
                    dto.revenueLost().setValue(revenueLost);
                } else {
                    dto.revenueLost().setValue(BigDecimal.ZERO);
                }
            }
        }
    }

}
