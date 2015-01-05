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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.InvoiceDebitAggregator;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.selections.SelectN4LeaseCandidateListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4RentOwingForPeriod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4LeaseCandidateDTO;

public class SelectN4LeaseCandidateListServiceImpl extends AbstractListServiceDtoImpl<Lease, N4LeaseCandidateDTO> implements SelectN4LeaseCandidateListService {

    private EntityListCriteria<N4LeaseCandidateDTO> toCriteria;

    private final N4Policy n4policy;

    public SelectN4LeaseCandidateListServiceImpl() {
        super(new CrudEntityBinder<Lease, N4LeaseCandidateDTO>(Lease.class, N4LeaseCandidateDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.leaseId(), boProto);
                bind(toProto.propertyCode(), boProto.unit().building().propertyCode());
                bind(toProto.unitNo(), boProto.unit().info().number());
                bind(toProto.moveIn(), boProto.actualMoveIn());
                bind(toProto.moveOut(), boProto.expectedMoveOut());
            }
        });
        n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), N4Policy.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> boCriteria, EntityListCriteria<N4LeaseCandidateDTO> toCriteria) {
        super.enhanceListCriteria(boCriteria, toCriteria);

        this.toCriteria = toCriteria;
        // TODO - add property code search criteria to boCriteria
    }

    @Override
    protected EntitySearchResult<Lease> query(EntityListCriteria<Lease> criteria) {
        EntitySearchResult<Lease> result = Persistence.secureQuery(criteria);
        BigDecimal minAmountOwed = BigDecimal.ZERO; // TODO - get from this.toCriteria
        for (Iterator<Lease> it = result.getData().iterator(); it.hasNext();) {
            // filter leases - remove ones with open eviction case or not enough owing
            Lease lease = it.next();
            if (hasOpenCase(lease) || !hasAmountOwed(lease, minAmountOwed)) {
                it.remove();
            }
        }
        return result;
    }

    @Override
    protected void enhanceListRetrieved(Lease bo, N4LeaseCandidateDTO to) {
        super.enhanceListRetrieved(bo, to);
        // TODO - add amountOwed and lastNotice date
        to.amountOwed().setValue(getAmountOwed(bo));
        to.lastNotice().setValue(null);
    }

    // ----- internals ----------
    private boolean hasOpenCase(Lease lease) {
        return false;
    }

    private List<N4RentOwingForPeriod> getUnpaidCharges(Lease lease) {
        HashSet<ARCode> acceptableArCodes = new HashSet<ARCode>(n4policy.relevantARCodes());
        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, today);
        InvoiceDebitAggregator debitCalc = new InvoiceDebitAggregator();
        return debitCalc.debitsForPeriod(debitCalc.aggregate(filteredDebits));
    }

    private BigDecimal getAmountOwed(Lease lease) {
        BigDecimal amountOwed = BigDecimal.ZERO;

        for (N4RentOwingForPeriod rentOwingForPeriod : getUnpaidCharges(lease)) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

    private boolean hasAmountOwed(Lease lease, BigDecimal minAmountOwed) {
        return getAmountOwed(lease).compareTo(minAmountOwed) > 0;
    }

}
