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

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.ref.ProvincePolicyNode;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseCrudServiceImpl extends AbstractCrudServiceDtoImpl<EvictionCase, EvictionCaseDTO> implements EvictionCaseCrudService {

    public EvictionCaseCrudServiceImpl() {
        super(EvictionCase.class, EvictionCaseDTO.class);
    }

    @Override
    protected EvictionCaseDTO init(InitializationData initializationData) {
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
        bo.evictionFlow().addAll(getEvictionFlowSteps(lease));
        return binder.createTO(bo);
    }

    @Override
    protected boolean persist(EvictionCase bo, EvictionCaseDTO to) {
        if (bo.createdBy().isNull()) {
            bo.createdBy().set(CrmAppContext.getCurrentUserEmployee());
        }
        if (bo.history().isEmpty()) {
            // TODO add the first eviction flow step if none is found?
        }

        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(EvictionCase bo, EvictionCaseDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.createdBy(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(EvictionCase bo, EvictionCaseDTO to) {
        Persistence.ensureRetrieve(to.createdBy(), AttachLevel.Attached);
    }

    private List<EvictionFlowStep> getEvictionFlowSteps(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        InternationalAddress addr = lease.unit().building().info().address();
        ISOProvince prov = ISOProvince.forName(addr.province().getValue(), addr.country().getValue());
        EntityQueryCriteria<ProvincePolicyNode> nodeQuery = EntityQueryCriteria.create(ProvincePolicyNode.class);
        nodeQuery.eq(nodeQuery.proto().province(), prov);
        ProvincePolicyNode policyNode = Persistence.service().retrieve(nodeQuery);
        EvictionFlowPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, EvictionFlowPolicy.class);
        if (policy == null) {
            throw new Error("Cannot retrieve EvictionFlowPolicy for building: " + lease.unit().building().propertyCode().getValue());
        }
        return policy.evictionFlow();
    }
}
