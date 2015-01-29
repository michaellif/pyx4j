/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.eviction.EvictionCaseFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4LeaseArrears;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.N4Policy.EmployeeSelectionMethod;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<N4Batch, N4BatchDTO> implements N4BatchCrudService {

    private static final I18n i18n = I18n.get(N4BatchCrudServiceImpl.class);

    public N4BatchCrudServiceImpl() {
        super(N4Batch.class, N4BatchDTO.class);
    }

    @Override
    /**
     * Generate a batch per building; return the first batch in the list.
     * For each affected lease open EvictionCase and add N4 step.
     */
    public void createBatches(AsyncCallback<N4BatchDTO> callback, Vector<Lease> leaseCandidates) {
        Map<Building, N4Batch> n4batches = new HashMap<>();
        Map<Building, N4Policy> n4policies = new HashMap<>();

        for (Lease leaseId : leaseCandidates) {
            Persistence.ensureRetrieve(leaseId.unit().building(), AttachLevel.Attached);
            Building building = leaseId.unit().building();
            N4Batch bo = n4batches.get(building);
            N4Policy n4policy = n4policies.get(building);
            if (bo == null) {
                n4policies.put(building, n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, N4Policy.class));
                n4batches.put(building, bo = createBatch(building, n4policy));
                generateBatchName(bo, building);
                Persistence.service().persist(bo);
            }

            N4BatchItem item = EntityFactory.create(N4BatchItem.class);
            item.leaseArrears().set(getLeaseArrears(leaseId, new HashSet<ARCode>(n4policy.relevantARCodes())));
            item.lease().set(leaseId);

            bo.items().add(item);
            Persistence.service().persist(item.leaseArrears());
            Persistence.service().persist(item);

            // open eviction case for the lease and add the reference to Lease Arrears data
            EvictionCase evictionCase = ServerSideFactory.create(EvictionCaseFacade.class).getCurrentEvictionCase(leaseId);
            if (evictionCase == null) {
                evictionCase = ServerSideFactory.create(EvictionCaseFacade.class).openEvictionCase(leaseId, i18n.tr("Created by N4 Batch process"));
            }
            EvictionStatusN4 n4status = (EvictionStatusN4) ServerSideFactory.create(EvictionCaseFacade.class).addEvictionStatusDetails(evictionCase,
                    EvictionStepType.N4.toString(), i18n.tr("N4 Batch created: {0}", bo.name().getValue()), null);
            n4status.leaseArrears().set(item.leaseArrears());
            n4status.originatingBatch().set(item.batch());
            n4status.cancellationBalance().setValue(n4policy.cancellationThreshold().getValue());
            // set expiry date by N4Policy
            GregorianCalendar expiryDate = new GregorianCalendar();
            expiryDate.setTime(bo.created().getValue());
            expiryDate.add(GregorianCalendar.DAY_OF_YEAR, n4policy.expiryDays().getValue());
            n4status.expiryDate().setValue(new LogicalDate(expiryDate.getTime()));
            Persistence.service().persist(n4status);
        }
        Persistence.service().commit();

        N4Batch bo = n4batches.values().iterator().next();
        N4BatchDTO to = null;
        if (bo != null) {
            to = binder.createTO(bo);
            enhanceRetrieved(bo, to, null);
        }

        callback.onSuccess(to);
    }

    @Override
    public void serviceBatch(AsyncCallback<String> callback, N4Batch batchId) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4BatchGenerationDeferredProcess(batchId), ThreadPoolNames.IMPORTS));
    }

    @Override
    protected void enhanceRetrieved(N4Batch bo, N4BatchDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.building(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.items(), AttachLevel.Attached);
        for (N4BatchItem item : to.items()) {
            Persistence.ensureRetrieve(item.leaseArrears().unpaidCharges(), AttachLevel.Attached);
            Persistence.ensureRetrieve(item.lease().unit().building(), AttachLevel.Attached);
            Persistence.ensureRetrieve(item.lease()._applicant(), AttachLevel.Attached);
        }
    }

    @Override
    protected void enhanceListRetrieved(N4Batch bo, N4BatchDTO to) {
        super.enhanceListRetrieved(bo, to);

        Persistence.ensureRetrieve(to.building(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.items(), AttachLevel.Attached);
    }

    @Override
    protected boolean persist(N4Batch bo, N4BatchDTO to) {
        for (N4BatchItem item : to.items()) {
            // not owned - persist explicitly
            Persistence.service().persist(item.leaseArrears());
        }
        return super.persist(bo, to);
    }

    // -------- internals -----------
    private N4Batch createBatch(Building building, N4Policy n4policy) {
        N4Batch batch = EntityFactory.create(N4Batch.class);

        batch.building().set(building);
        batch.created().setValue(SystemDateManager.getDate());
        batch.companyLegalName().setValue(n4policy.companyName().getValue());
        batch.companyAddress().set(n4policy.mailingAddress().duplicate(InternationalAddress.class));

        // signing Agent
        if (EmployeeSelectionMethod.ByLoggedInUser.equals(n4policy.agentSelectionMethodN4().getValue())) {
            batch.signingAgent().set(CrmAppContext.getCurrentUserEmployee());
        }

        if (n4policy.useAgentContactInfoN4().getValue(false) && !batch.signingAgent().isNull()) {
            batch.phoneNumber().set(batch.signingAgent().workPhone());
        } else {
            batch.phoneNumber().setValue(n4policy.phoneNumber().getValue());
            batch.faxNumber().setValue(n4policy.faxNumber().getValue());
            batch.emailAddress().setValue(n4policy.emailAddress().getValue());
        }

        // servicing Agent
        if (EmployeeSelectionMethod.ByLoggedInUser.equals(n4policy.agentSelectionMethodCS().getValue())) {
            batch.servicingAgent().set(CrmAppContext.getCurrentUserEmployee());
        }

        if (n4policy.useAgentContactInfoCS().getValue(false) && !batch.servicingAgent().isNull()) {
            batch.phoneNumberCS().set(batch.servicingAgent().workPhone());
        } else {
            batch.phoneNumberCS().setValue(n4policy.phoneNumberCS().getValue());
        }

        return batch;
    }

    private void generateBatchName(N4Batch batch, Building building) {
        batch.name().setValue(building.propertyCode().getValue() + "_" + batch.created().getStringView().replaceAll(" ", "_"));
    }

    private N4LeaseArrears getLeaseArrears(Lease lease, HashSet<ARCode> acceptableArCodes) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, today);
        List<N4UnpaidCharge> owings = new ArrayList<>();
        BigDecimal amountOwed = BigDecimal.ZERO;
        for (InvoiceDebit debit : filteredDebits) {
            // TODO - copied from SelectN4LeaseCandidateListServiceImpl; may want to move to a facade
            N4UnpaidCharge owing = EntityFactory.create(N4UnpaidCharge.class);
            owing.fromDate().setValue(debit.billingCycle().billingCycleStartDate().getValue());
            owing.toDate().setValue(debit.billingCycle().billingCycleEndDate().getValue());
            owing.rentCharged().setValue(owing.rentCharged().getValue(BigDecimal.ZERO).add(debit.amount().getValue()));
            owing.rentCharged().setValue(owing.rentCharged().getValue().add(debit.taxTotal().getValue()));
            owing.rentOwing().setValue(owing.rentOwing().getValue(BigDecimal.ZERO).add(debit.outstandingDebit().getValue()));
            owing.rentPaid().setValue(owing.rentCharged().getValue().subtract(owing.rentOwing().getValue()));
            owing.arCode().set(debit.arCode());
            owings.add(owing);
            amountOwed = amountOwed.add(owing.rentOwing().getValue());
        }
        N4LeaseArrears leaseArrears = EntityFactory.create(N4LeaseArrears.class);
        leaseArrears.lease().set(lease);
        leaseArrears.unpaidCharges().addAll(owings);
        leaseArrears.totalRentOwning().setValue(amountOwed);

        return leaseArrears;
    }
}
