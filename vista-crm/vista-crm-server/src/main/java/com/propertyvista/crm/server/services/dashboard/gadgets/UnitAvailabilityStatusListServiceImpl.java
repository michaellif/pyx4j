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
        bind(toProto.propertyCode(), boProto.building().propertyCode());
        bind(toProto.externalId(), boProto.building().externalId());
        bind(toProto.buildingName(), boProto.building().info().name());
        bind(toProto.address(), boProto.building().info().address());
        bind(toProto.propertyManager(), boProto.building().propertyManager().name());
        bind(toProto.complex(), boProto.building().complex().name());
        bind(toProto.unit(), boProto.unit().info().number());
        bind(toProto.floorplanName(), boProto.floorplan().name());
        bind(toProto.floorplanMarketingName(), boProto.floorplan().marketingName());

        // STATUS DATA    
        bind(toProto.statusFrom(), boProto.statusFrom());
        bind(toProto.statusUntil(), boProto.statusUntil());
        bind(toProto.vacancyStatus(), boProto.vacancyStatus());
        bind(toProto.rentedStatus(), boProto.rentedStatus());
        bind(toProto.scoping(), boProto.scoping());
        bind(toProto.rentReadinessStatus(), boProto.rentReadinessStatus());
        bind(toProto.unitRent(), boProto.unitRent());
        bind(toProto.marketRent(), boProto.marketRent());
        bind(toProto.rentDeltaAbsolute(), boProto.rentDeltaAbsolute());
        bind(toProto.rentDeltaRelative(), boProto.rentDeltaRelative());
        bind(toProto.rentEndDay(), boProto.rentEndDay());
        bind(toProto.vacantSince(), boProto.vacantSince());
        bind(toProto.rentedFromDay(), boProto.rentedFromDay());
        bind(toProto.moveInDay(), boProto.moveInDay());

        // BUISNESS DATA
        bind(toProto.unitId(), boProto.unit().id());
        bind(toProto.buildingsFilterAnchor(), boProto.building());
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
