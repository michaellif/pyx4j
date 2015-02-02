/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2015
 * @author stanp
 */
package com.propertyvista.crm.server.services.selections;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.legal.eviction.EvictionCaseFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.selections.SelectN4LeaseCandidateListService;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4LeaseCandidateDTO;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;

public class SelectN4LeaseCandidateListServiceImpl extends AbstractListServiceDtoImpl<Lease, N4LeaseCandidateDTO> implements SelectN4LeaseCandidateListService {

    public static final I18n i18n = I18n.get(SelectN4LeaseCandidateListServiceImpl.class);

    private EntityListCriteria<N4LeaseCandidateDTO> toCriteria;

    private final Map<Key, String> applicableBuildings;

    public SelectN4LeaseCandidateListServiceImpl() {
        super(new CrudEntityBinder<Lease, N4LeaseCandidateDTO>(Lease.class, N4LeaseCandidateDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.leaseId(), boProto);
                bind(toProto.propertyCode(), boProto.unit().building().propertyCode());
                bind(toProto.unitNo(), boProto.unit().info().number());
                bind(toProto.moveIn(), boProto.expectedMoveIn());
                bind(toProto.moveOut(), boProto.expectedMoveOut());
            }
        });
        applicableBuildings = getApplicableBuildings();
    }

    @Override
    protected Criterion convertCriterion(EntityListCriteria<Lease> criteria, Criterion cr) {
        if (cr instanceof PropertyCriterion && toProto.amountOwed().getPath().equals(new Path(((PropertyCriterion) cr).getPropertyPath()))) {
            /*
             * TODO - need a better way; N4LeaseCandidateDTO.amountOwed() criteria is coming from the Lister UI Filters,
             * but this TO-property can not be bound to any of the BO (Lease) properties. So, to avoid failure we must
             * return any relevant criteria that is always TRUE.
             */
            return PropertyCriterion.isNotNull(boProto.id());
        } else {
            return super.convertCriterion(criteria, cr);
        }
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> boCriteria, EntityListCriteria<N4LeaseCandidateDTO> toCriteria) {
        super.enhanceListCriteria(boCriteria, toCriteria);

        // save criteria to access later - see getMinAmountOwingFromSearchCriteria()
        this.toCriteria = toCriteria;

        boCriteria.eq(boCriteria.proto().status(), Lease.Status.Active);
        boCriteria.in(boCriteria.proto().unit().building().id(), applicableBuildings.keySet());
    }

    @Override
    protected EntitySearchResult<Lease> query(EntityListCriteria<Lease> criteria) {
        // we have to get everything first, then filter per additional criteria, and then extract the requested page
        int pageSize = criteria.getPageSize();
        int pageNumber = criteria.getPageNumber();
        criteria.setPageSize(-1);
        criteria.setPageNumber(0);
        BigDecimal minAmountOwed = getMinAmountOwingFromSearchCriteria();
        EntitySearchResult<Lease> result = new EntitySearchResult<>();
        for (Lease lease : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            // filter leases - remove ones with open eviction case or not enough owing
            if (!hasOpenCase(lease) && hasAmountOwed(lease, minAmountOwed)) {
                result.add(lease);
            }
        }
        // update properties of the result set
        result.setTotalRows(result.getData().size());
        int maxPageNo = result.getTotalRows() / pageSize;
        if (pageNumber > maxPageNo) {
            pageNumber = maxPageNo;
        }
        int pageFrom = pageNumber * pageSize;
        int pageTo = pageFrom + pageSize;
        result.hasMoreData(result.getTotalRows() > pageTo);
        // extract requested page
        pageTo = result.hasMoreData() ? pageTo : result.getTotalRows();
        result.setData(new Vector<Lease>(result.getData().subList(pageFrom, pageTo)));

        return result;
    }

    @Override
    protected void enhanceListRetrieved(Lease bo, N4LeaseCandidateDTO to) {
        super.enhanceListRetrieved(bo, to);
        to.amountOwed().setValue(getAmountOwed(bo));
        to.lastNotice().setValue(getLastEvictionCaseCloseDate(bo));
        to.propertyCode().setValue(applicableBuildings.get(bo.unit().building().getPrimaryKey()));
    }

    // ----- internals ----------
    private BigDecimal getMinAmountOwingFromSearchCriteria() {
        BigDecimal minOwing = BigDecimal.ZERO;
        // see if we can find a corresponding GE/GT filter, e.g. PropertyCriterion.ge(toProto.amountOwing(), value)
        List<Criterion> filters = new ArrayList<>();
        if (toCriteria.getFilters() != null) {
            filters.addAll(toCriteria.getFilters());
        }
        for (int i = 0; i < filters.size(); i++) {
            Criterion cr = filters.get(i);
            if (cr instanceof PropertyCriterion) {
                PropertyCriterion crProp = (PropertyCriterion) cr;
                if (toProto.amountOwed().getPath().equals(new Path(crProp.getPropertyPath())) && //
                        EnumSet.of(Restriction.GREATER_THAN_OR_EQUAL, Restriction.GREATER_THAN).contains(crProp.getRestriction())) {
                    return new BigDecimal(crProp.getValue().toString());
                }
            } else if (cr instanceof AndCriterion) {
                AndCriterion crAnd = (AndCriterion) cr;
                if (crAnd.getFilters() != null) {
                    filters.addAll(crAnd.getFilters());
                }
                continue;
            } else if (cr instanceof RangeCriterion) {
                RangeCriterion crRng = (RangeCriterion) cr;
                if (crRng.getFilters() != null) {
                    filters.addAll(crRng.getFilters());
                }
                continue;
            }
        }
        return minOwing;
    }

    private boolean hasOpenCase(Lease lease) {
        return ServerSideFactory.create(EvictionCaseFacade.class).getCurrentEvictionCase(lease) != null;
    }

    private LogicalDate getLastEvictionCaseCloseDate(Lease lease) {
        EvictionCase evictionCase = ServerSideFactory.create(EvictionCaseFacade.class).getLastEvictionCase(lease);
        if (evictionCase != null && !evictionCase.closedOn().isNull()) {
            return new LogicalDate(evictionCase.closedOn().getValue());
        } else {
            return null;
        }

    }

    private BigDecimal getAmountOwed(Lease lease) {
        BigDecimal amountOwed = BigDecimal.ZERO;

        for (N4UnpaidCharge rentOwingForPeriod : N4GenerationUtils.getUnpaidCharges(lease, getPolicy(lease).relevantARCodes())) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

    private boolean hasAmountOwed(Lease lease, BigDecimal minAmountOwed) {
        return getAmountOwed(lease).compareTo(minAmountOwed) > 0;
    }

    private N4Policy getPolicy(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.IdOnly);
        return ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), N4Policy.class);
    }

    private Map<Key, String> getApplicableBuildings() {
        Map<Key, String> result = new HashMap<>();
        for (Building building : Persistence.service().query(EntityListCriteria.create(Building.class), AttachLevel.IdOnly)) {
            try {
                EvictionFlowPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, EvictionFlowPolicy.class);
                for (EvictionFlowStep step : policy.evictionFlow()) {
                    if (EvictionStepType.N4.equals(step.stepType().getValue())) {
                        Persistence.ensureRetrieve(building, AttachLevel.Attached);
                        result.put(building.getPrimaryKey(), building.propertyCode().getValue());
                    }
                }
            } catch (PolicyNotFoundException ignore) {
                // empty result is handled below
            }
        }
        if (result.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("N4 not configured in Eviction Flow Policy"));
        }
        return result;
    }
}
