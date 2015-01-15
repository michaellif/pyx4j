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

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.N4Policy.EmployeeSelectionMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<N4Batch, N4BatchDTO> implements N4BatchCrudService {

    public N4BatchCrudServiceImpl() {
        super(N4Batch.class, N4BatchDTO.class);
    }

    @Override
    /** Generate a batch per building; return the first batch in the list */
    public void createBatches(AsyncCallback<N4BatchDTO> callback, Vector<Lease> leaseCandidates) {
        Map<Building, N4Batch> n4batches = new HashMap<>();
        Map<PolicyNode, N4Policy> n4policies = new HashMap<>();

        for (Lease leaseId : leaseCandidates) {
            Persistence.ensureRetrieve(leaseId.unit().building(), AttachLevel.Attached);
            Building building = leaseId.unit().building();
            N4Batch bo = n4batches.get(building);
            PolicyNode node = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(leaseId);
            N4Policy n4policy = n4policies.get(node);
            if (bo == null) {
                n4policies.put(node, n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, N4Policy.class));
                n4batches.put(building, bo = createBatch(building, n4policy));
                generateBatchName(bo, building);
                Persistence.service().persist(bo);
            }

            N4BatchItem item = EntityFactory.create(N4BatchItem.class);

            List<N4UnpaidCharge> unpaidCharges = getUnpaidCharges(leaseId, new HashSet<ARCode>(n4policy.relevantARCodes()));
            BigDecimal amountOwed = BigDecimal.ZERO;
            for (N4UnpaidCharge rentOwingForPeriod : unpaidCharges) {
                amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
            }
            item.unpaidCharges().addAll(unpaidCharges);
            item.totalRentOwning().setValue(amountOwed);
            item.lease().set(leaseId);

            bo.items().add(item);
            Persistence.service().persist(item);
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
        callback.onSuccess(DeferredProcessRegistry.fork(new N4GenerationDeferredProcess(batchId), ThreadPoolNames.IMPORTS));
    }

    @Override
    protected void enhanceRetrieved(N4Batch bo, N4BatchDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.building(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.items(), AttachLevel.Attached);
        for (N4BatchItem item : to.items()) {
            Persistence.ensureRetrieve(item.unpaidCharges(), AttachLevel.Attached);
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

    // -------- internals -----------
    private N4Batch createBatch(Building building, N4Policy n4policy) {
        N4Batch batch = EntityFactory.create(N4Batch.class);

        batch.building().set(building);
        batch.created().setValue(SystemDateManager.getDate());
        batch.companyLegalName().setValue(n4policy.companyName().getValue());
        batch.companyAddress().set(n4policy.mailingAddress().duplicate(InternationalAddress.class));

        if (EmployeeSelectionMethod.ByLoggedInUser.equals(n4policy.agentSelectionMethodN4().getValue())) {
            batch.signingAgent().set(CrmAppContext.getCurrentUserEmployee());
        }

        if (n4policy.useAgentContactInfoN4().getValue(false) && !batch.signingAgent().isNull()) {
            // TODO use Employee  contact if so configured in policy; has no fax though...
        } else {
            batch.companyPhoneNumber().setValue(n4policy.phoneNumber().getValue());
            batch.companyFaxNumber().setValue(n4policy.faxNumber().getValue());
            batch.companyEmailAddress().setValue(n4policy.emailAddress().getValue());
        }

        return batch;
    }

    private void generateBatchName(N4Batch batch, Building building) {
        batch.name().setValue(building.propertyCode().getValue() + "_" + batch.created().getStringView().replaceAll(" ", "_"));
    }

    // TODO - copied from SelectN4LeaseCandidateListServiceImpl; move to a facade
    private List<N4UnpaidCharge> getUnpaidCharges(Lease lease, HashSet<ARCode> acceptableArCodes) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

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
}
