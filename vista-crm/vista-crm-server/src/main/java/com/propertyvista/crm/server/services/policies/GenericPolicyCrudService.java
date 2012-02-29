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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.framework.PolicyNode;

public abstract class GenericPolicyCrudService<POLICY extends Policy, POLICY_DTO extends POLICY> extends GenericCrudServiceDtoImpl<POLICY, POLICY_DTO> {

    private final static I18n i18n = I18n.get(GenericPolicyCrudService.class);

    public GenericPolicyCrudService(Class<POLICY> dboClass, Class<POLICY_DTO> dtoClass) {
        super(dboClass, dtoClass);

        // TODO check how multiple parametrized inheritance may be acheived (i need "POLICY_DTO extends POLICY & PolicyDTOBase (maybe PolicyDTOBase should extend Policy") 
        if (!PolicyDTOBase.class.isAssignableFrom(dtoClass)) {
            throw new Error("POLICY_DTO must extends PolicyDTOBase");
        }
    }

    @Override
    protected void enhanceDTO(POLICY in, POLICY_DTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        if (fromList) {
            PolicyNode castedNode = ((PolicyDTOBase) dto).node().cast();
            ((PolicyDTOBase) dto).nodeType().setValue(castedNode.getEntityMeta().getCaption());
            ((PolicyDTOBase) dto).nodeRepresentation().setValue(castedNode.getStringView());
        }
    }

    @Override
    protected void persistDBO(POLICY dbo, POLICY_DTO in) {
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

        EntityQueryCriteria<POLICY> criteria = new EntityQueryCriteria<POLICY>(dboClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().node(), node));
        POLICY oldPolicyAtTheSameNode = Persistence.service().retrieve(criteria);

        if (oldPolicyAtTheSameNode != null) {
            throw new Error(i18n.tr("not allowed to override existing policy"));
        }

        super.persistDBO(dbo, in);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        super.delete(callback, entityId);
        Persistence.service().commit();
    }
}
