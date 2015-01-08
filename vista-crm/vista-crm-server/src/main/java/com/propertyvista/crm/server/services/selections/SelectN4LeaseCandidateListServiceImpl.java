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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
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

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.services.selections.SelectN4LeaseCandidateListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4LeaseCandidateDTO;

public class SelectN4LeaseCandidateListServiceImpl extends AbstractListServiceDtoImpl<Lease, N4LeaseCandidateDTO> implements SelectN4LeaseCandidateListService {

    private EntityListCriteria<N4LeaseCandidateDTO> toCriteria;

    private final Map<PolicyNode, N4Policy> policyCache;

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

        policyCache = new HashMap<>();
    }

    @Override
    protected Criterion convertCriterion(EntityListCriteria<Lease> criteria, Criterion cr) {
        if (cr instanceof PropertyCriterion && toProto.amountOwed().getPath().equals(new Path(((PropertyCriterion) cr).getPropertyPath()))) {
            /*
             * TODO - this is a hack; N4LeaseCandidateDTO.amountOwed() criteria is coming from the Lister UI Filters,
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
    }

    @Override
    protected EntitySearchResult<Lease> query(EntityListCriteria<Lease> criteria) {
        // we have to get everything first, then filter per additional criteria, and then extract the requested page
        int pageSize = criteria.getPageSize();
        int pageNumber = criteria.getPageNumber();
        criteria.setPageSize(-1);
        criteria.setPageNumber(0);
        EntitySearchResult<Lease> result = Persistence.secureQuery(criteria);
        BigDecimal minAmountOwed = getMinAmountOwingFromSearchCriteria();
        for (Iterator<Lease> it = result.getData().iterator(); it.hasNext();) {
            // filter leases - remove ones with open eviction case or not enough owing
            Lease lease = it.next();
            if (hasOpenCase(lease) || !hasAmountOwed(lease, minAmountOwed)) {
                it.remove();
            }
        }
        // update properties of the result set
        int pageFrom = pageNumber * pageSize;
        int pageTo = pageFrom + pageSize;
        result.setTotalRows(result.getData().size());
        result.hasMoreData(result.getTotalRows() > pageTo);
        // extract requested page
        pageTo = result.hasMoreData() ? pageTo : result.getTotalRows();
        result.setData(new Vector<Lease>(result.getData().subList(pageFrom, pageTo)));

        return result;
    }

    @Override
    protected void retrievedForList(Lease bo) {
        super.retrievedForList(bo);

        Persistence.ensureRetrieve(bo.unit().building(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(Lease bo, N4LeaseCandidateDTO to) {
        super.enhanceListRetrieved(bo, to);
        to.amountOwed().setValue(getAmountOwed(bo));
        // TODO - find out lastNotice date
        to.lastNotice().setValue(null);
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
        return false;
    }

    private List<N4UnpaidCharge> getUnpaidCharges(Lease lease) {
        HashSet<ARCode> acceptableArCodes = new HashSet<ARCode>(getPolicy(lease).relevantARCodes());
        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, today);
        List<N4UnpaidCharge> owings = new ArrayList<>();
        for (InvoiceDebit debit : filteredDebits) {
            N4UnpaidCharge owing = EntityFactory.create(N4UnpaidCharge.class);
            owing.fromDate().setValue(debit.billingCycle().billingCycleStartDate().getValue());
            owing.toDate().setValue(debit.billingCycle().billingCycleEndDate().getValue());
            owing.rentCharged().setValue(owing.rentCharged().getValue(BigDecimal.ZERO).add(debit.amount().getValue()));
            owing.rentCharged().setValue(owing.rentCharged().getValue().add(debit.taxTotal().getValue()));
            owing.rentOwing().setValue(owing.rentOwing().getValue(BigDecimal.ZERO).add(debit.outstandingDebit().getValue()));
            owing.rentPaid().setValue(owing.rentCharged().getValue().subtract(owing.rentOwing().getValue()));
            owing.arCode().set(debit.arCode());
            owings.add(owing);
        }
        return owings;
    }

    private BigDecimal getAmountOwed(Lease lease) {
        BigDecimal amountOwed = BigDecimal.ZERO;

        for (N4UnpaidCharge rentOwingForPeriod : getUnpaidCharges(lease)) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

    private boolean hasAmountOwed(Lease lease, BigDecimal minAmountOwed) {
        return getAmountOwed(lease).compareTo(minAmountOwed) > 0;
    }

    private N4Policy getPolicy(Lease lease) {
        PolicyNode node = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(lease);
        N4Policy policy = policyCache.get(node);
        if (policy == null) {
            policyCache.put(node, policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, N4Policy.class));
        }
        return policy;
    }
}
