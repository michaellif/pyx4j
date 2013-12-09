/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInLeaseParticipantDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInPaymentDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.crm.server.util.BuildingsCriteriaNormalizer;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MoneyInToolServiceImpl implements MoneyInToolService {

    private final BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    public MoneyInToolServiceImpl() {
        buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(EntityFactory.getEntityPrototype(Lease.class).unit().building());
    }

    @Override
    public void findCandidates(AsyncCallback<Vector<MoneyInCandidateDTO>> callback, MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        EntityQueryCriteria<Lease> criteria = makeCriteria(criteriaEntity);
        List<Lease> leases = Persistence.secureQuery(criteria);

        Vector<MoneyInCandidateDTO> candidates = new Vector<MoneyInCandidateDTO>();
        for (Lease lease : leases) {
            MoneyInCandidateDTO candidate = toCandidate(lease);
            if (matches(candidate, criteriaEntity)) {
                candidates.add(candidate);
            }
        }
        callback.onSuccess(candidates);
    }

    @Override
    public void createPaymentBatch(AsyncCallback<VoidSerializable> callback, Vector<MoneyInPaymentDTO> payments) {
        // TODO implement this
        callback.onSuccess(null);
    }

    private EntityQueryCriteria<Lease> makeCriteria(MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.in(criteria.proto().status(), Lease.Status.active());
        buildingCriteriaNormalizer.addBuildingCriterion(criteria, criteriaEntity.portfolios(), criteriaEntity.buildings());
        return criteria;
    }

    private boolean matches(MoneyInCandidateDTO candidate, MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        return candidate.totalOutstanding().getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    private MoneyInCandidateDTO toCandidate(Lease lease) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, lease.unit().getPrimaryKey());
        Persistence.service().retrieve(unit.building());

        MoneyInCandidateDTO candidate = EntityFactory.create(MoneyInCandidateDTO.class);
        candidate.leaseIdStub().set(lease.createIdentityStub());
        candidate.building().setValue(unit.building().propertyCode().getValue());
        candidate.unit().setValue(unit.info().number().getValue());
        candidate.leaseId().setValue(lease.leaseId().getValue());
        candidate.payerCandidates().addAll(fetchPayerCandidates(lease));
        candidate.totalOutstanding().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));

        // TODO prepayments?        
        candidate.prepayments();

        return candidate;
    }

    private Collection<? extends MoneyInLeaseParticipantDTO> fetchPayerCandidates(Lease lease) {
        EntityQueryCriteria<LeaseTermParticipant> criteria = EntityQueryCriteria.create(LeaseTermParticipant.class);
        criteria.eq(criteria.proto().leaseParticipant().lease(), lease);
        criteria.in(criteria.proto().role(), LeaseTermParticipant.Role.Applicant, LeaseTermParticipant.Role.CoApplicant);

        List<LeaseTermParticipant> leaseTermParticipants = Persistence.service().query(criteria);

        List<MoneyInLeaseParticipantDTO> moneyInLeaseParticipants = new ArrayList<MoneyInLeaseParticipantDTO>(leaseTermParticipants.size());
        for (LeaseTermParticipant leaseTermParticipant : leaseTermParticipants) {
            moneyInLeaseParticipants.add(toMoneyInLeaseParticipant(leaseTermParticipant));
        }

        return moneyInLeaseParticipants;
    }

    private MoneyInLeaseParticipantDTO toMoneyInLeaseParticipant(LeaseTermParticipant leaseTermParticipant) {
        MoneyInLeaseParticipantDTO moneyInLeaseParticipant = EntityFactory.create(MoneyInLeaseParticipantDTO.class);
        moneyInLeaseParticipant.tenantIdStub().set(leaseTermParticipant.leaseParticipant().duplicate(Tenant.class).createIdentityStub());
        moneyInLeaseParticipant.name().setValue(leaseTermParticipant.leaseParticipant().customer().person().name().getStringView());
        return moneyInLeaseParticipant;
    }
}
