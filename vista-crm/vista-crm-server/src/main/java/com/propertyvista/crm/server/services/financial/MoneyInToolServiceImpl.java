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
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInLeaseParticipantDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInPaymentDTO;
import com.propertyvista.crm.rpc.dto.tools.TooManyResultsException;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.crm.server.util.BuildingsCriteriaNormalizer;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class MoneyInToolServiceImpl implements MoneyInToolService {

    private static final Logger log = LoggerFactory.getLogger(MoneyInToolServiceImpl.class);

    /** I'm not sure current UI implementation will be able to support more than that without lag or 'unresponsive script' */
    private final static int DEFAULT_MAX_NUM_OF_RESULTS = 600;

    private final BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    private final int maxNumberOfResults;

    public MoneyInToolServiceImpl() {
        buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(EntityFactory.getEntityPrototype(Lease.class).unit().building());
        maxNumberOfResults = DEFAULT_MAX_NUM_OF_RESULTS;
    }

    @Override
    public void findCandidates(AsyncCallback<Vector<MoneyInCandidateDTO>> callback, MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        // part of the filtering is done in the DB and part on server side utilizing 'matches()' function
        EntityQueryCriteria<Lease> criteria = makeCriteria(criteriaEntity);

        Vector<MoneyInCandidateDTO> candidates = new Vector<MoneyInCandidateDTO>();
        ICursorIterator<Lease> leases = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        try {
            while (leases.hasNext()) {
                Lease lease = leases.next();
                MoneyInCandidateDTO candidate = toCandidate(lease);
                if (matches(candidate, criteriaEntity)) {
                    candidates.add(candidate);
                }
            }
        } catch (Throwable e) {
            log.error("got error during creation candidates for Money In", e);
            IOUtils.closeQuietly(leases);
        }

        if (candidates.size() > maxNumberOfResults) {
            log.warn("Got too many results while searching for candidates for Money In Batch creation (# of results = " + candidates.size() + ")");
            throw new TooManyResultsException();
        }
        callback.onSuccess(candidates);
    }

    @Override
    public void createPaymentBatch(AsyncCallback<String> callback, LogicalDate receiptDate, Vector<MoneyInPaymentDTO> payments) {
        callback.onSuccess(DeferredProcessRegistry.fork(new MoneyInBatchCreateDeferredProcess(receiptDate, payments), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void postPaymentBatch(AsyncCallback<String> callback, Key batchId) {
        callback.onSuccess(DeferredProcessRegistry.fork(new MoneyInBatchPostDeferredProcess(batchId), ThreadPoolNames.IMPORTS));
    }

    @Override
    public void cancelPaymentBatchPosting(AsyncCallback<String> callback, Key batchId) {
        callback.onSuccess(DeferredProcessRegistry.fork(new MoneyInBatchCancelPostingDeferredProcess(batchId), ThreadPoolNames.IMPORTS));
    }

    private EntityQueryCriteria<Lease> makeCriteria(MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);

        criteria.in(criteria.proto().status(), Lease.Status.active());

        buildingCriteriaNormalizer.addBuildingCriterion(criteria,//@formatter:off
                criteriaEntity.filterByPortfolio().getValue(false) ? criteriaEntity.selectedPortfolios() : null,
                criteriaEntity.filterByBuildings().getValue(false) ? criteriaEntity.selectedBuildings() : null
        );//@formatter:on

        if (!CommonsStringUtils.isEmpty(criteriaEntity.unit().getValue())) {
            criteria.like(criteria.proto().unit().info().number(), "%" + criteriaEntity.unit().getValue() + "%");
        }
        if (!CommonsStringUtils.isEmpty(criteriaEntity.lease().getValue())) {
            criteria.like(criteria.proto().leaseId(), "%" + criteriaEntity.lease().getValue() + "%");
        }

        return criteria;
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

        candidate.processPayment().setValue(false);
        candidate.payment().leaseIdStub().set(lease.createIdentityStub());
        candidate.payment().payerLeaseTermTenantIdStub().set(candidate.payerCandidates().get(0).leaseTermTenantIdStub());

        return candidate;
    }

    private Collection<? extends MoneyInLeaseParticipantDTO> fetchPayerCandidates(Lease lease) {
        Persistence.ensureRetrieve(lease.currentTerm(), AttachLevel.IdOnly);
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        List<MoneyInLeaseParticipantDTO> moneyInLeaseParticipants = new ArrayList<MoneyInLeaseParticipantDTO>(lease.currentTerm().version().tenants().size());
        for (LeaseTermParticipant leaseTermParticipant : lease.currentTerm().version().tenants()) {
            if (leaseTermParticipant.role().getValue() == LeaseTermParticipant.Role.Applicant
                    || leaseTermParticipant.role().getValue() == LeaseTermParticipant.Role.CoApplicant) {
                moneyInLeaseParticipants.add(toMoneyInLeaseParticipant(leaseTermParticipant));
            }
        }

        return moneyInLeaseParticipants;
    }

    private MoneyInLeaseParticipantDTO toMoneyInLeaseParticipant(LeaseTermParticipant leaseTermParticipant) {
        MoneyInLeaseParticipantDTO moneyInLeaseParticipant = EntityFactory.create(MoneyInLeaseParticipantDTO.class);
        moneyInLeaseParticipant.leaseTermTenantIdStub().set(leaseTermParticipant.duplicate(LeaseTermTenant.class).createIdentityStub());
        moneyInLeaseParticipant.name().setValue(leaseTermParticipant.leaseParticipant().customer().person().name().getStringView());
        return moneyInLeaseParticipant;
    }

    private static boolean matches(MoneyInCandidateDTO candidate, MoneyInCandidateSearchCriteriaDTO criteriaEntity) {
        return candidate.totalOutstanding().getValue().compareTo(BigDecimal.ZERO) > 0 && matchesTenant(candidate, criteriaEntity.tenant().getValue());
    }

    private static boolean matchesTenant(MoneyInCandidateDTO candidate, String tenantNamePart) {
        return CommonsStringUtils.isEmpty(tenantNamePart) || anyTenantNameMatches(candidate.payerCandidates(), tenantNamePart);
    }

    private static boolean anyTenantNameMatches(List<MoneyInLeaseParticipantDTO> participants, String tenantNamePart) {
        for (MoneyInLeaseParticipantDTO p : participants) {
            if (p.name().getValue().toLowerCase().contains(tenantNamePart)) {
                return true;
            }
        }
        return false;
    }

}
