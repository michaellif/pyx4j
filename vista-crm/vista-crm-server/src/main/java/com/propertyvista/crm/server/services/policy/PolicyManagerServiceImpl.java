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

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.domain.policy.BuildingPolicy;
import com.propertyvista.domain.policy.NodeType;
import com.propertyvista.domain.policy.PoliciesAtNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.UnitPolicy;
import com.propertyvista.domain.policy.assignment.DefaultPolicies;
import com.propertyvista.domain.policy.assignment.PoliciesAtBuilding;
import com.propertyvista.domain.policy.assignment.PoliciesAtComplex;
import com.propertyvista.domain.policy.assignment.PoliciesAtCountry;
import com.propertyvista.domain.policy.assignment.PoliciesAtOrganization;
import com.propertyvista.domain.policy.assignment.PoliciesAtProvince;
import com.propertyvista.domain.policy.assignment.PoliciesAtUnit;
import com.propertyvista.domain.policy.dto.EffectivePolicyDTO;
import com.propertyvista.domain.policy.dto.EffectivePolicyPresetDTO;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void effectivePolicyPreset(AsyncCallback<EffectivePolicyPresetDTO> callback, Key pk, NodeType nodeType) {
        callback.onSuccess(computeEffectivePolicyPreset(pk, nodeType));
    }

    public static PoliciesAtNode getPolicyPresetAtUnit(Key pk) {
        EntityQueryCriteria<PoliciesAtUnit> criteria = new EntityQueryCriteria<PoliciesAtUnit>(PoliciesAtUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), pk));
        List<PoliciesAtUnit> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtFloorPlan(Key pk) {
        EntityQueryCriteria<PoliciesAtBuilding> criteria = new EntityQueryCriteria<PoliciesAtBuilding>(PoliciesAtBuilding.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), pk));
        List<PoliciesAtBuilding> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtBuilding(Key pk) {
        EntityQueryCriteria<PoliciesAtBuilding> criteria = new EntityQueryCriteria<PoliciesAtBuilding>(PoliciesAtBuilding.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), pk));
        List<PoliciesAtBuilding> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtComplex(Key pk) {
        EntityQueryCriteria<PoliciesAtComplex> criteria = new EntityQueryCriteria<PoliciesAtComplex>(PoliciesAtComplex.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), pk));
        List<PoliciesAtComplex> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtProvince(Key pk) {
        EntityQueryCriteria<PoliciesAtProvince> criteria = new EntityQueryCriteria<PoliciesAtProvince>(PoliciesAtProvince.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().province(), pk));
        List<PoliciesAtProvince> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtCountry(Key pk) {
        EntityQueryCriteria<PoliciesAtCountry> criteria = new EntityQueryCriteria<PoliciesAtCountry>(PoliciesAtCountry.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().country(), pk));
        List<PoliciesAtCountry> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getPolicyPresetAtOrganization() {
        EntityQueryCriteria<PoliciesAtOrganization> criteria = new EntityQueryCriteria<PoliciesAtOrganization>(PoliciesAtOrganization.class);
        List<PoliciesAtOrganization> policyPresetAtNode = Persistence.service().query(criteria);
        if (policyPresetAtNode.isEmpty()) {
            return null;
        }
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    public static PoliciesAtNode getDefaultPolicies() {
        EntityQueryCriteria<DefaultPolicies> criteria = new EntityQueryCriteria<DefaultPolicies>(DefaultPolicies.class);
        List<DefaultPolicies> policyPresetAtNode = Persistence.service().query(criteria);
        assert !policyPresetAtNode.isEmpty() : "Default policy preset was not found in the database, have you forgot to preload?";
        assert policyPresetAtNode.size() == 1;
        return policyPresetAtNode.get(0);
    }

    private static EffectivePolicyPresetDTO merge(EffectivePolicyPresetDTO effectivePreset, PoliciesAtNode presetAtNode) {
        if (effectivePreset == null) {
            effectivePreset = EntityFactory.create(EffectivePolicyPresetDTO.class);
        }
        if (presetAtNode != null) {
            Persistence.service().retrieve(presetAtNode.preset());
            for (Policy policy : presetAtNode.preset().policies()) {
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
                    if (!presetAtNode.equals(effectivePreset.directlyAssignedFrom())) {
                        effectivePolicy.inheritedFrom().set(presetAtNode);
                    }
                    effectivePreset.effectivePolicies().add(effectivePolicy);
                }
            }
        }
        return effectivePreset;
    }

    private static EffectivePolicyPresetDTO computeEffectivePolicyPreset(Key pk, NodeType nodeType) {
        assert nodeType != null;
        assert (pk != null & !nodeType.equals(NodeType.ORGANIZATION)) | nodeType.equals(NodeType.ORGANIZATION);

        EffectivePolicyPresetDTO effectivePreset = EntityFactory.create(EffectivePolicyPresetDTO.class);

        boolean hasParentComplex = false;
        PoliciesAtNode policiesAtNode = null;
        // if a node doesn't have a parent (i.e. unit doesn't have a parent building, we let it use organization policy by default)
        // but, in case of building we make an exception        
        switch (nodeType) {
        case UNIT:
            policiesAtNode = getPolicyPresetAtUnit(pk);
            if (nodeType.equals(NodeType.UNIT)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);

            pk = parentFloorplanOf(pk);
        case FLOORPLAN:
            policiesAtNode = getPolicyPresetAtFloorPlan(pk);
            if (nodeType.equals(NodeType.FLOORPLAN)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);

            pk = parentBuildingOf(pk);
        case BUILDING:
            // it's not clear whether every building has to belong to a complex, so we just assume it doesn't, and if it doesn't belong to a complex
            // we use country as it's parent
            // TODO make it be region when we have regions in the domain.
            policiesAtNode = getPolicyPresetAtBuilding(pk);
            if (nodeType.equals(NodeType.BUILDING)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);

            Key complexPk = parentComplexOf(pk);
            if (complexPk != null) {
                hasParentComplex = true;
                pk = complexPk;
            } else {
                pk = parentProvinceOfBuilding(pk);
            }
        case COMPLEX:
            if (!NodeType.COMPLEX.equals(nodeType) & !hasParentComplex) {
                // just skip: the building we've just checked had no complex assigned to it
            } else {
                // if we were asked to to bring complex info, or we need to get parent of a building
                policiesAtNode = getPolicyPresetAtComplex(pk);
                if (nodeType.equals(NodeType.COMPLEX)) {
                    effectivePreset.directlyAssignedFrom().set(policiesAtNode);
                }
                effectivePreset = merge(effectivePreset, policiesAtNode);

                pk = parentProvinceOfComplex(pk);
                if (pk == null) {
                    // TODO maybe return an empty effectivePreset?  
                    throw new Error("Failed to compute policies on complex with PK(" + pk + "): this complex has no primary building set");
                }
            }
        case PROVINCE:
            policiesAtNode = getPolicyPresetAtProvince(pk);
            if (nodeType.equals(NodeType.COMPLEX)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);

            pk = parentCountryOfProvince(pk);
        case COUNTRY:
            policiesAtNode = getPolicyPresetAtCountry(pk);
            if (nodeType.equals(NodeType.COUNTRY)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);

        case ORGANIZATION:
            policiesAtNode = getPolicyPresetAtOrganization();
            if (nodeType.equals(NodeType.ORGANIZATION)) {
                effectivePreset.directlyAssignedFrom().set(policiesAtNode);
            }
            effectivePreset = merge(effectivePreset, policiesAtNode);
        }
        effectivePreset = merge(effectivePreset, getDefaultPolicies());

        // remove things we don't want to send
        if (!effectivePreset.directlyAssignedFrom().isNull()) {
            // no need to send policies that are included in effecive policies list
            effectivePreset.directlyAssignedFrom().preset().detach();
        }
        Iterator<EffectivePolicyDTO> i = effectivePreset.effectivePolicies().iterator();

        while (i.hasNext()) {
            EffectivePolicyDTO policy = i.next();
            Class<?> policyClass = policy.policy().cast().getInstanceValueClass();
            if ((nodeType.equals(NodeType.UNIT) | nodeType.equals(NodeType.FLOORPLAN)) & !UnitPolicy.class.isAssignableFrom(policyClass)) {
                i.remove();
                continue;
            }
            if (nodeType.equals(NodeType.BUILDING) & !(BuildingPolicy.class.isAssignableFrom(policyClass))) {
                i.remove();
                continue;
            }
            if (!policy.inheritedFrom().isNull()) {
                policy.inheritedFrom().preset().detach();
            }
        }

        return effectivePreset;
    }

    private static Key parentFloorplanOf(Key unitPk) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitPk);
        return unit.belongsTo().getPrimaryKey();
    }

    private static Key parentBuildingOf(Key unitPk) {
        Floorplan floorplan = Persistence.service().retrieve(Floorplan.class, unitPk);
        return floorplan.building().getPrimaryKey();
    }

    private static Key parentComplexOf(Key buildingPk) {
        Building building = Persistence.service().retrieve(Building.class, buildingPk);
        if (building.complex().isNull()) {
            return null;
        } else {
            return building.complex().getPrimaryKey();
        }
    }

    private static Key parentProvinceOfBuilding(Key buildingPk) {
        Building building = Persistence.service().retrieve(Building.class, buildingPk);
        return building.info().address().province().getPrimaryKey();
    }

    private static Key parentCountryOfBuilding(Key buildingPk) {
        Building building = Persistence.service().retrieve(Building.class, buildingPk);
        return building.info().address().country().getPrimaryKey();
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

    private static Key parentProvinceOfComplex(Key complexPk) {
        Complex complex = Persistence.service().retrieve(Complex.class, complexPk);

        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), complex));

        List<Building> buildings = Persistence.service().query(criteria);
        for (Building building : buildings) {
            if (building.complexPrimary().isBooleanTrue()) {
                return building.info().address().province().getPrimaryKey();
            }
        }
        return null;
    }

    private static Key parentCountryOfProvince(Key provincePk) {
        Province province = Persistence.service().retrieve(Province.class, provincePk);
        return province.country().getPrimaryKey();
    }
}
