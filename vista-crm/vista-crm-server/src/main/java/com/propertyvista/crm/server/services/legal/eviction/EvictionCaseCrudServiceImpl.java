/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseCrudServiceImpl extends AbstractCrudServiceDtoImpl<EvictionCase, EvictionCaseDTO> implements EvictionCaseCrudService {

    private final Map<Building, EvictionFlowPolicy> policyCache;

    public EvictionCaseCrudServiceImpl() {
        super(EvictionCase.class, EvictionCaseDTO.class);
        policyCache = new HashMap<>();
    }

    @Override
    protected EvictionCaseDTO init(InitializationData initializationData) {
        // TODO - use EvictionCaseFacade
        EvictionCase bo = EntityFactory.create(EvictionCase.class);
        EvictionCaseInitData initData = (EvictionCaseInitData) initializationData;
        if (initData.isNull()) {
            throw new Error("Initialization data is empty");
        }
        Lease lease = Persistence.service().retrieve(Lease.class, initData.lease().getPrimaryKey());
        if (lease == null) {
            throw new Error("Initialization failed - lease not found: " + initData.lease().getPrimaryKey());
        }
        bo.lease().set(initData.lease());
        // copy flow steps from the policy
        bo.evictionFlowPolicy().set(getEvictionFlowPolicy(lease));
        EvictionCaseDTO to = binder.createTO(bo);

        return to;
    }

    @Override
    public void hasEvictionFlow(AsyncCallback<Boolean> callback, Key leaseId) {
        callback.onSuccess(!getEvictionFlowPolicy(Persistence.service().retrieve(Lease.class, leaseId)).evictionFlow().isEmpty());
    }

    @Override
    protected boolean persist(EvictionCase bo, EvictionCaseDTO to) {
        // set signed-in employee as the author
        Employee signedIn = CrmAppContext.getCurrentUserEmployee();
        if (bo.createdBy().isNull()) {
            bo.createdBy().set(signedIn);
        }
        for (EvictionStatus status : bo.history()) {
            if (status.getPrimaryKey() == null) {
                status.addedBy().set(signedIn);
            }
            for (EvictionStatusRecord record : status.statusRecords()) {
                if (record.getPrimaryKey() == null) {
                    record.addedBy().set(signedIn);
                }
            }
            // N4
            if (status instanceof EvictionStatusN4) {
                Persistence.service().persist(((EvictionStatusN4) status).n4Data());
            }
        }

        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(EvictionCase bo, EvictionCaseDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        for (EvictionStatus status : to.history()) {
            Persistence.ensureRetrieve(status.addedBy(), AttachLevel.Attached);
            Persistence.ensureRetrieve(status.statusRecords(), AttachLevel.Attached);
            for (EvictionStatusRecord record : status.statusRecords()) {
                Persistence.ensureRetrieve(record.addedBy(), AttachLevel.Attached);
                Persistence.ensureRetrieve(record.attachments(), AttachLevel.Attached);
            }
            // N4
            if (status instanceof EvictionStatusN4) {
                EvictionStatusN4 statusN4 = (EvictionStatusN4) status;
                Persistence.ensureRetrieve(statusN4.leaseArrears().unpaidCharges(), AttachLevel.Attached);
                if (statusN4.n4Data().isNull()) {
                    statusN4.n4Data().set(EntityFactory.create(N4LeaseData.class));
                    if (statusN4.originatingBatch().getPrimaryKey() != null) {
                        N4Batch batch = Persistence.service().retrieve(N4Batch.class, statusN4.originatingBatch().getPrimaryKey());
                        // copy n4 data
                        statusN4.n4Data().created().set(batch.created());
                        statusN4.n4Data().serviceDate().set(batch.serviceDate());
                        statusN4.n4Data().deliveryMethod().set(batch.deliveryMethod());
                        statusN4.n4Data().deliveryDate().set(batch.deliveryDate());
                        statusN4.n4Data().companyLegalName().set(batch.companyLegalName());
                        statusN4.n4Data().companyAddress().set(batch.companyAddress());
                        statusN4.n4Data().phoneNumber().set(batch.phoneNumber());
                        statusN4.n4Data().faxNumber().set(batch.faxNumber());
                        statusN4.n4Data().emailAddress().set(batch.emailAddress());
                        statusN4.n4Data().phoneNumberCS().set(batch.phoneNumberCS());
                        statusN4.n4Data().isLandlord().set(batch.isLandlord());
                        statusN4.n4Data().signatureDate().set(batch.signatureDate());
                        statusN4.n4Data().signingAgent().set(batch.signingAgent());
                        statusN4.n4Data().servicingAgent().set(batch.servicingAgent());
                    }
                } else {
                    Persistence.ensureRetrieve(statusN4.n4Data(), AttachLevel.Attached);
                }
            }
        }
        Persistence.ensureRetrieve(to.lease(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.evictionFlowPolicy(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(EvictionCase bo, EvictionCaseDTO to) {
        // TODO ?
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<EvictionCaseDTO>> callback, EntityListCriteria<EvictionCaseDTO> dtoCriteria) {
        super.list(callback, dtoCriteria);
    }

    private EvictionFlowPolicy getEvictionFlowPolicy(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        Building building = lease.unit().building();
        EvictionFlowPolicy policy = policyCache.get(building);
        if (policy == null) {
            policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, EvictionFlowPolicy.class);
            policyCache.put(building, policy);
        }
        if (policy == null) {
            throw new Error("Cannot find EvictionFlowPolicy for building: " + building.propertyCode().getValue());
        }
        return policy;
    }

    @Override
    public void issueN4(AsyncCallback<String> callback, EvictionCase caseId) {
        callback.onSuccess(DeferredProcessRegistry.fork(new N4LeaseGenerationDeferredProcess(caseId), ThreadPoolNames.IMPORTS));
    }
}
