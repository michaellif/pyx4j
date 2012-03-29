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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

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
        LowestApplicableNode lowestNodeType = dboClass.getAnnotation(LowestApplicableNode.class);
        if (lowestNodeType != null) {
            dto.lowestNodeType().setValue(lowestNodeType.value().getName());
        }
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
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
        if (node.isNull() || node.id().isNull()) {
            throw new Error("unable to persist policy, the scope (a node in organizatonal hierarchy) was not set");
        }

        boolean isNewPolicy = in.getPrimaryKey() == null;
        if (isNewPolicy) {
            EntityQueryCriteria<POLICY> criteria = new EntityQueryCriteria<POLICY>(dboClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().node(), node));
            POLICY oldPolicyAtTheSameNode = Persistence.service().retrieve(criteria);

            if (oldPolicyAtTheSameNode != null) {
                throw new UserRuntimeException(i18n.tr("Overriding existing policy is forbidden!"));
            }

        }
        super.persist(dbo, in);
    }

    @Override
    public void createNewPolicy(AsyncCallback<POLICY_DTO> callback) {
        POLICY_DTO policyDTO = EntityFactory.create(dtoClass);
        setLowestNodeType(policyDTO);
        callback.onSuccess(policyDTO);
    }
}
