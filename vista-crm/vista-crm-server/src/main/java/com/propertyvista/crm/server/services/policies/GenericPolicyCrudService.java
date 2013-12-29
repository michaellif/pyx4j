/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.framework.PolicyNode;

public abstract class GenericPolicyCrudService<POLICY extends Policy, POLICY_DTO extends PolicyDTOBase> extends AbstractCrudServiceDtoImpl<POLICY, POLICY_DTO>
        implements AbstractPolicyCrudService<POLICY_DTO> {

    private final static I18n i18n = I18n.get(GenericPolicyCrudService.class);

    public GenericPolicyCrudService(Class<POLICY> dboClass, Class<POLICY_DTO> dtoClass) {
        super(dboClass, dtoClass);
    }

    private void setLowestNodeType(POLICY_DTO dto) {
        LowestApplicableNode lowestNodeType = boClass.getAnnotation(LowestApplicableNode.class);
        if (lowestNodeType != null) {
            dto.lowestNodeType().setValue(lowestNodeType.value().getName());
        }
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected POLICY_DTO init(InitializationData initializationData) {
        POLICY_DTO policyDTO = EntityFactory.create(toClass);

        setLowestNodeType(policyDTO);

        return policyDTO;
    }

    @Override
    protected void enhanceListRetrieved(POLICY in, POLICY_DTO dto) {
        PolicyNode castedNode = ((PolicyDTOBase) dto).node().cast();
        ((PolicyDTOBase) dto).nodeType().setValue(castedNode.getEntityMeta().getCaption());
        ((PolicyDTOBase) dto).nodeRepresentation().setValue(castedNode.getStringView());
    }

    @Override
    protected void persist(POLICY dbo, POLICY_DTO in) {
        PolicyNode node = ((PolicyDTOBase) in).node().cast();

        if (node.getInstanceValueClass().equals(OrganizationPoliciesNode.class)) {
            // since there should be only one organizational policy node, we don't expect from the client to give us a persisted entity with a PK
            // and do it ourselves
            node = Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));
            if (node == null) {
                throw new Error("failed to retrieve instance of " + OrganizationPoliciesNode.class.getSimpleName() + " from the DB");
            }
            dbo.node().set(node);
        }
        if (node.isNull() || node.getPrimaryKey() == null) {
            throw new Error("unable to persist policy, the scope (a node in organizatonal hierarchy) was not set");
        }

        boolean isNewPolicy = in.getPrimaryKey() == null;
        POLICY orig = null;
        if (isNewPolicy) {
            EntityQueryCriteria<POLICY> criteria = new EntityQueryCriteria<POLICY>(boClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().node(), node));
            POLICY oldPolicyAtTheSameNode = Persistence.service().retrieve(criteria);

            if (oldPolicyAtTheSameNode != null) {
                throw new UserRuntimeException(i18n.tr("Overriding existing policy is forbidden!"));
            }
        } else {
            orig = Persistence.service().retrieve(boClass, dbo.getPrimaryKey());
        }

        super.persist(dbo, in);

        ServerSideFactory.create(PolicyFacade.class).resetPolicyCache();

        if (isNewPolicy) {
            ServerSideFactory.create(AuditFacade.class).created(dbo);
        } else {
            ServerSideFactory.create(AuditFacade.class).updated(dbo, EntityDiff.getChanges(orig, dbo));
        }
    }

    @Override
    protected void delete(POLICY actualEntity) {
        super.delete(actualEntity);
        ServerSideFactory.create(AuditFacade.class).delete(actualEntity);
    }
}
