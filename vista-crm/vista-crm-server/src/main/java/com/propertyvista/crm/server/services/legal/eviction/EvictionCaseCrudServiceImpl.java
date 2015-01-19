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
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
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

        if (!to.evictionFlowPolicy().evictionFlow().isEmpty()) {
            to.nextStep().set(to.evictionFlowPolicy().evictionFlow().get(0));
        }

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
        }

        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(EvictionCase bo, EvictionCaseDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        for (EvictionStatus status : bo.history()) {
            Persistence.ensureRetrieve(status.addedBy(), AttachLevel.Attached);
            Persistence.ensureRetrieve(status.statusRecords(), AttachLevel.Attached);
            for (EvictionStatusRecord record : status.statusRecords()) {
                Persistence.ensureRetrieve(record.addedBy(), AttachLevel.Attached);
                Persistence.ensureRetrieve(record.attachments(), AttachLevel.Attached);
            }
        }

        to.nextStep().set(getNextEvictionStep(bo));
        Persistence.ensureRetrieve(to.nextStep(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(EvictionCase bo, EvictionCaseDTO to) {
        // TODO ?
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

    private EvictionFlowStep getNextEvictionStep(EvictionCase bo) {
        EvictionFlowStep nextStep = null;
        if (bo.history().size() < bo.evictionFlowPolicy().evictionFlow().size()) {
            nextStep = bo.evictionFlowPolicy().evictionFlow().get(bo.history().size());
        }
        return nextStep;
    }
}
