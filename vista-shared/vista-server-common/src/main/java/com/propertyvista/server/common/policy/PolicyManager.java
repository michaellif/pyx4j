/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.policy.framework.BuildingPolicy;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyAtNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.framework.UnitPolicy;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class PolicyManager {

    // TODO keep this in some kind of "PoliciesHeriarchy" class
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends PolicyNode>> HIERARCHY = Arrays.asList(AptUnit.class, Floorplan.class, Building.class, Complex.class,
            Province.class, Country.class, OrganizationPoliciesNode.class);

    /**
     * @param node
     * @return the list of effective policies, i.e. policies that are active at the specified node
     */
    public static List<PolicyAtNode> effectivePolicies(PolicyNode node) {
        List<PolicyAtNode> effectivePolicies = null;

        Class<? extends PolicyNode> requestedNodeClass = (Class<? extends PolicyNode>) node.getInstanceValueClass();
        if (node.isEmpty()) {
            Persistence.service().retrieve(node);
        }
        for (Class<? extends PolicyNode> nodeType : HIERARCHY) {
            if (nodeType.equals(node.getInstanceValueClass())) {
                effectivePolicies = merge(effectivePolicies, policiesAtNode(node));

                PolicyNode parentNode = parentOf(node);
                if (parentNode == null) {
                    throw new Error("Failed to find parent node of " + node.toString());
                } else {
                    node = parentNode;
                }
            }
        }

        // Filter all the unwanted policies
        Iterator<PolicyAtNode> i = effectivePolicies.iterator();
        while (i.hasNext()) {
            Class<? extends Policy> policyClass = (Class<? extends Policy>) i.next().policy().getInstanceValueClass();
            if ((AptUnit.class.equals(requestedNodeClass) | Floorplan.class.equals(requestedNodeClass)) & !UnitPolicy.class.isAssignableFrom(policyClass)) {
                i.remove();
            } else if (Building.class.equals(requestedNodeClass) & !BuildingPolicy.class.isAssignableFrom(policyClass)) {
                i.remove();
            }
        }
        return effectivePolicies;
    }

    /**
     * 
     * @param node
     *            not <code>null</code>.
     * @param policyClass
     *            not <code>null</code>.
     * @return policy at the requested organization policies hierarchy node or <code>null</code> if that policy has no default instance attached to
     *         {@link DefaultPoliciesNode}.
     */
    // TODO refactor this, no realy has to use effectivePolicies(); 
    public static Policy effectivePolicy(PolicyNode node, Class<? extends Policy> policyClass) {
        List<PolicyAtNode> effectivePolicies = effectivePolicies(node);
        for (PolicyAtNode policyAtNode : effectivePolicies) {
            if (policyAtNode.policy().getInstanceValueClass().equals(policyClass)) {
                return policyAtNode.policy().duplicate(policyClass);
            }
        }
        return null;
    }

    private static List<PolicyAtNode> merge(List<PolicyAtNode> effectivePolicies, List<PolicyAtNode> policies) {
        if (effectivePolicies == null) {
            effectivePolicies = new LinkedList<PolicyAtNode>();
        }

        for (PolicyAtNode policy : policies) {
            boolean alreadyEffective = false;

            for (PolicyAtNode effectivePolicy : effectivePolicies) {
                if (effectivePolicy.policy().getInstanceValueClass().equals(policy.getInstanceValueClass())) {
                    alreadyEffective = true;
                    break;
                }
            }
            if (!alreadyEffective) {
                effectivePolicies.add(policy);
            }
        }

        return effectivePolicies;
    }

    private static List<PolicyAtNode> policiesAtNode(PolicyNode node) {
        EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().node(), node));
        List<PolicyAtNode> policiesAtNode = Persistence.service().query(criteria);
        return policiesAtNode;
    };

    // TODO move this method to another class (i.e. something that manages/defines heirarchy)
    public static PolicyNode parentOf(PolicyNode node) {
        Class<? extends PolicyNode> nodeClass = (Class<? extends PolicyNode>) node.getInstanceValueClass();
        if (AptUnit.class.equals(nodeClass)) {

            return Persistence.service().retrieve(Floorplan.class, ((AptUnit) node.cast()).floorplan().getPrimaryKey());

        } else if (Floorplan.class.equals(nodeClass)) {

            return Persistence.service().retrieve(Building.class, ((Floorplan) node.cast()).building().getPrimaryKey());

        } else if (Building.class.equals(nodeClass)) {

            Complex parentComplex = Persistence.service().retrieve(Complex.class, ((Building) node.cast()).complex().getPrimaryKey());
            if (parentComplex != null) {
                return parentComplex;
            } else {
                return Persistence.service().retrieve(Province.class, ((Building) node.cast()).info().address().province().getPrimaryKey());
            }
        } else if (Complex.class.equals(nodeClass)) {
            EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), node.getPrimaryKey()));

            List<Building> buildings = Persistence.service().query(criteria);
            for (Building building : buildings) {
                if (building.complexPrimary().isBooleanTrue()) {
                    return building.info().address().province();
                }
            }
            // if we haven't found a parent: return settings at organization (we assume that one node at must be preloaded)
            return Persistence.service().query(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class)).get(0);

        } else if (Province.class.equals(nodeClass)) {

            return Persistence.service().retrieve(Country.class, ((Province) node.cast()).country().getPrimaryKey());

        } else if (Country.class.equals(nodeClass)) {

            // we assume that one organization policies node is preloaded and present in the system
            return Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));

        } else if (OrganizationPoliciesNode.class.equals(nodeClass)) {
            // the parent of the organization is organization :)        
            return node;

        } else {

            return null;
        }
    }

    // TODO move this method to another class (i.e. something that manages/defines heirarchy)
    public static List<? extends PolicyNode> childrenOf(PolicyNode node) {
        Class<? extends PolicyNode> nodeClass = node != null ? (Class<? extends PolicyNode>) node.getInstanceValueClass() : OrganizationPoliciesNode.class;

        if (AptUnit.class.equals(nodeClass)) {
            return new LinkedList<PolicyNode>();

        } else if (Floorplan.class.equals(nodeClass)) {
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), node));
            return Persistence.service().query(criteria);

        } else if (Building.class.equals(nodeClass)) {
            EntityQueryCriteria<Floorplan> criteria = new EntityQueryCriteria<Floorplan>(Floorplan.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), node));
            return Persistence.service().query(criteria);

        } else if (Complex.class.equals(nodeClass)) {
            EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), node));
            return Persistence.service().query(criteria);

        } else if (Province.class.equals(nodeClass)) {
            EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().info().address().province(), node));
            criteria.add(PropertyCriterion.eq(criteria.proto().complexPrimary(), true));

            List<Building> primaryBuildings = Persistence.service().query(criteria);

            List<PolicyNode> children = new ArrayList<PolicyNode>(primaryBuildings.size());

            for (Building building : primaryBuildings) {
                children.add(building.complex());
            }

            // now add 'orphan' buidlings that have no parent complex

            criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().info().address().province(), node));
            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), (Serializable) null)); // the casting here is only to choose the overloaded method

            children.addAll(Persistence.service().query(criteria));

            return children;

        } else if (Country.class.equals(nodeClass)) {
            EntityQueryCriteria<Province> criteria = new EntityQueryCriteria<Province>(Province.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().country(), node));
            return Persistence.service().query(criteria);
        } else if (OrganizationPoliciesNode.class.equals(nodeClass)) {
            return Persistence.service().query(new EntityQueryCriteria<Country>(Country.class));
        } else {
            throw new Error("Got unknown type of " + PolicyNode.class.getName() + ": '" + nodeClass.getName() + "'");
        }
    }
}
