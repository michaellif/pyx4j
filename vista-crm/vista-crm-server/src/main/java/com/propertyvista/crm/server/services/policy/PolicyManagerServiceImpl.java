/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policy;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.domain.policy.EffectivePolicyDTO;
import com.propertyvista.domain.policy.EffectivePolicyPresetDTO;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyPreset;
import com.propertyvista.domain.policy.PolicyPresetAtNode;
import com.propertyvista.domain.policy.PolicyPresetAtNode.NodeType;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void effectivePolicyPreset(AsyncCallback<EffectivePolicyPresetDTO> callback, Key pk, NodeType nodeType) {
        callback.onSuccess(computeEffectivePolicyPreset(pk, nodeType));
    }

    @Override
    public void assignedPolicyPreset(AsyncCallback<PolicyPreset> callback, Key pk, NodeType nodeType) {
        callback.onSuccess(policyPresetAtNode(pk, nodeType).policyPreset());
    }

    private static PolicyPresetAtNode policyPresetAtNode(Key pk, PolicyPresetAtNode.NodeType nodeType) {
        if (!nodeType.equals(NodeType.organization) & pk == null) {
            return null;
        }

        EntityQueryCriteria<PolicyPresetAtNode> criteria = new EntityQueryCriteria<PolicyPresetAtNode>(PolicyPresetAtNode.class);
        switch (nodeType) {
        case unit:
            criteria.add(PropertyCriterion.eq(criteria.proto().unit(), pk));
            break;
        case building:
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), pk));
            break;
        // TODO add region
        case complex:
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), pk));
            break;
        case country:
            criteria.add(PropertyCriterion.eq(criteria.proto().country(), pk));
            break;
        default:
        }
        criteria.add(PropertyCriterion.eq(criteria.proto().nodeType(), nodeType));
        List<PolicyPresetAtNode> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        if (policyPresetAtNode.size() > 1) {
            // FIXME write other error message
            throw new Error("Warning!!! database integrity has been compromized, initiating self desctruction procedure");
        }
        return policyPresetAtNode.get(0);
    }

    private static EffectivePolicyPresetDTO merge(EffectivePolicyPresetDTO effectivePreset, PolicyPresetAtNode presetAtNode) {
        if (effectivePreset == null) {
            effectivePreset = EntityFactory.create(EffectivePolicyPresetDTO.class);
        }
        if (presetAtNode != null) {
            Persistence.service().retrieve(presetAtNode.policyPreset());
            for (Policy policy : presetAtNode.policyPreset().policies()) {
                boolean alreadyEffective = false;
                for (EffectivePolicyDTO effectivePolicy : effectivePreset.effectivePolicies()) {
                    if (effectivePolicy.policy().getInstanceValueClass().equals(policy.getInstanceValueClass())) {
                        alreadyEffective = true;
                        break;
                    }
                }
                if (!alreadyEffective) {
                    EffectivePolicyDTO effectivePolicy = EntityFactory.create(EffectivePolicyDTO.class);
                    effectivePolicy.policy().set(policy);
                    effectivePolicy.inheritedFrom().set(presetAtNode);
                    effectivePreset.effectivePolicies().add(effectivePolicy);
                }
            }
        }
        return effectivePreset;
    }

    private static EffectivePolicyPresetDTO computeEffectivePolicyPreset(Key pk, PolicyPresetAtNode.NodeType nodeType) {
        assert nodeType != null;
        assert (pk != null & !nodeType.equals(NodeType.organization)) | nodeType.equals(NodeType.organization);

        EffectivePolicyPresetDTO effectivePreset = EntityFactory.create(EffectivePolicyPresetDTO.class);

        // TODO i don't like repeating this search when merging effective policy, but nevertheless it should work, think about it later 
        effectivePreset.assignedPolicyPreset().set(policyPresetAtNode(pk, nodeType));
        if (!effectivePreset.assignedPolicyPreset().isNull()) {
            Persistence.service().retrieve(effectivePreset.assignedPolicyPreset().policyPreset());
        }
        boolean hasParentComplex = false;

        // if a node doesn't have a parent (i.e. unit doesn't have a parent building, we let it use organization policy by default)
        // but, in case of building we make an exception        
        switch (nodeType) {
        case unit:
            effectivePreset = merge(effectivePreset, policyPresetAtNode(pk, NodeType.unit));
            pk = parentBuildingOf(pk);
        case building:
            // it's not clear whether every building has to belong to a complex, so we just assume it doesn't, and if it doesn't belong to a complex
            // we use country as it's parent
            // TODO make it be region when we have regions in the domain.
            effectivePreset = merge(effectivePreset, policyPresetAtNode(pk, NodeType.building));
            Key complexPk = parentComplexOf(pk);
            if (complexPk != null) {
                hasParentComplex = true;
                pk = complexPk;
            } else {
                pk = parentCountryOfBuilding(pk);
            }
        case complex:
            if (NodeType.building.equals(nodeType) & !hasParentComplex) {
                // just skip: the building we've just checked had no complex assigned to it
            } else {
                effectivePreset = merge(effectivePreset, policyPresetAtNode(pk, NodeType.complex));
                pk = parentCountryOfComplex(pk);
            }
        case region:
            // TODO 
        case country:
            effectivePreset = merge(effectivePreset, policyPresetAtNode(pk, NodeType.country));
        case organization:
            effectivePreset = merge(effectivePreset, policyPresetAtNode(pk, NodeType.organization));
        }
        for (EffectivePolicyDTO effectivePolicy : effectivePreset.effectivePolicies()) {
            if (nodeType.equals(effectivePolicy.inheritedFrom().nodeType().getValue())) {
                effectivePolicy.inheritedFrom().set(null);
            } else {
                effectivePolicy.inheritedFrom().policyPreset().detach();
            }

        }
        return effectivePreset;
    }

    private static Key parentBuildingOf(Key unitPk) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitPk);
        return unit.belongsTo().getPrimaryKey();
    }

    private static Key parentComplexOf(Key buildingPk) {
        Building building = Persistence.service().retrieve(Building.class, buildingPk);
        if (building.complex().isNull()) {
            return null;
        } else {
            return building.complex().getPrimaryKey();
        }
    }

    private static Key parentCountryOfComplex(Key complexPk) {
        Complex complex = Persistence.service().retrieve(Complex.class, complexPk);

        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), complex));

        List<Building> buildings = Persistence.service().query(criteria);
        for (Building building : buildings) {
            if (building.complexPrimary().isBooleanTrue()) {
                return building.info().address().country().getPrimaryKey();
            }
        }
        return null;
    }

    private static Key parentCountryOfBuilding(Key buildingPk) {
        Building building = Persistence.service().retrieve(Building.class, buildingPk);
        return building.info().address().country().getPrimaryKey();
    }

}
