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
 */
package com.propertyvista.biz.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.CountryPolicyNode;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.ref.ProvincePolicyNode;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;
import com.propertyvista.shared.config.VistaFeatures;

class PolicyManager {

    // TODO keep this in some kind of "PoliciesHeriarchy" class, pass as dependency in the constructor
    private static final List<Class<? extends PolicyNode>> HIERARCHY = Arrays.asList(AptUnit.class, Floorplan.class, Building.class, Complex.class,
            ProvincePolicyNode.class, CountryPolicyNode.class, OrganizationPoliciesNode.class);

    /**
     *
     * @param node
     *            not <code>null</code>.
     * @param policyClass
     *            not <code>null</code>.
     * @return policy at the requested organization policies hierarchy node or throw {@link PolicyNotFoundException}
     * @throws {@link PolicyNotFoundException} when policy is not found
     */
    public static <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass) {
        POLICY policy = null;
        if (node != null && node.isInstanceOf(OrganizationPoliciesNode.class) && node.isNull()) {
            node = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        }
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("PolicyNode is 'null' querying policy " + policyClass.getSimpleName());
        }

        PolicyNode currentNode = node;
        policy = CacheService.get(policyCacheKey(policyClass, currentNode));
        if (policy != null) {
            return policy;
        }

        do {
            Persistence.ensureRetrieve(currentNode, AttachLevel.Attached);
            EntityQueryCriteria<POLICY> criteria = EntityQueryCriteria.create(policyClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().node(), currentNode));
            policy = Persistence.service().retrieve(criteria);
            if (policy != null) {
                break;
            }
            currentNode = parentOf(currentNode);
        } while (currentNode != null);

        if (policy == null) {
            policy = createEmptyPolicy(node, policyClass);
            if (policy == null) {
                Persistence.ensureRetrieve(node, AttachLevel.ToStringMembers);
                throw new PolicyNotFoundException(policyClass, node.getStringView());
            }
        }

        policy = correctAccordingToVistaFeatures(policy);
        CacheService.put(policyCacheKey(policyClass, node), policy);
        return policy;
    }

    private static String policyCacheKey(final Class<? extends Policy> policyClass, PolicyNode node) {
        return PolicyManager.class.getName() + policyClass.getSimpleName() + node.getEntityMeta().getEntityClass().getSimpleName() + node.getPrimaryKey();
    }

    static void resetPolicyCache() {
        CacheService.reset();
    }

    // TODO move this method to another class (i.e. something that manages/defines heirarchy)
    /**
     *
     * @param node
     * @return <code>null</code> for organizational policy node, otherwise parent node or if there's not a parent node organizational policy node
     */
    public static PolicyNode parentOf(PolicyNode node) {
        @SuppressWarnings("unchecked")
        Class<? extends PolicyNode> nodeClass = (Class<? extends PolicyNode>) node.getInstanceValueClass();
        if (OrganizationPoliciesNode.class.equals(nodeClass)) {
            return null;
        }

        PolicyNode policyNode = null;
        do {
            if (AptUnit.class.equals(nodeClass)) {
                policyNode = Persistence.service().retrieve(Floorplan.class, ((AptUnit) node.cast()).floorplan().getPrimaryKey());
                break;
            }
            if (Floorplan.class.equals(nodeClass)) {
                policyNode = Persistence.service().retrieve(Building.class, ((Floorplan) node.cast()).building().getPrimaryKey());
                break;
            }
            if (Building.class.equals(nodeClass)) {
                Complex parentComplex = Persistence.service().retrieve(Complex.class, ((Building) node.cast()).complex().getPrimaryKey());
                if (parentComplex != null) {
                    policyNode = parentComplex;
                } else {
                    Building building = (Building) node.cast();
                    EntityQueryCriteria<ProvincePolicyNode> criteria = new EntityQueryCriteria<>(ProvincePolicyNode.class);
                    criteria.eq(criteria.proto().province(),
                            ISOProvince.forName(building.info().address().province().getValue(), building.info().address().country().getValue()));
                    policyNode = Persistence.service().retrieve(criteria);
                }
                break;
            }
            if (Complex.class.equals(nodeClass)) {
                Building primaryBuilding = null;
                {
                    EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
                    criteria.eq(criteria.proto().complex(), node.getPrimaryKey());
                    criteria.eq(criteria.proto().complexPrimary(), true);
                    primaryBuilding = Persistence.service().retrieve(criteria);
                }
                if (primaryBuilding != null) {
                    EntityQueryCriteria<ProvincePolicyNode> criteria = new EntityQueryCriteria<>(ProvincePolicyNode.class);
                    criteria.eq(criteria.proto().province(),
                            ISOProvince.forName(primaryBuilding.info().address().province().getValue(), primaryBuilding.info().address().country().getValue()));
                    policyNode = Persistence.service().retrieve(criteria);
                }
                break;
            }
            if (ProvincePolicyNode.class.equals(nodeClass)) {
                ISOProvince province = ((ProvincePolicyNode) node.cast()).province().getValue();
                if (province != null) {
                    EntityQueryCriteria<CountryPolicyNode> criteria = new EntityQueryCriteria<>(CountryPolicyNode.class);
                    criteria.eq(criteria.proto().country(), province.country);
                    policyNode = Persistence.service().retrieve(criteria);
                }
                break;
            }
            if (CountryPolicyNode.class.equals(nodeClass)) {
                // we assume that one organization policies node is preloaded and present in the system
                policyNode = Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));
                break;
            }

        } while (false);

        // this is default: i.e. if any node has no parent we return organization policies node
        if (policyNode == null) {
            policyNode = Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));
        }

        return policyNode;
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

        } else if (ProvincePolicyNode.class.equals(nodeClass)) {
            Persistence.ensureRetrieve(node, AttachLevel.Attached);
            String provName = ((ProvincePolicyNode) node.cast()).province().getValue().name;

            List<PolicyNode> children = new ArrayList<PolicyNode>();

            // for buildings that have parent Complexes - return the Complexes
            EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.eq(criteria.proto().info().address().province(), provName);
            criteria.eq(criteria.proto().complexPrimary(), true);
            for (Building building : Persistence.service().query(criteria)) {
                children.add(building.complex());
            }

            // now add 'orphan' buildings that have no parent complex
            criteria = new EntityQueryCriteria<Building>(Building.class);
            criteria.eq(criteria.proto().info().address().province(), provName);
            criteria.eq(criteria.proto().complex(), (Serializable) null);
            children.addAll(Persistence.service().query(criteria));

            return children;

        } else if (CountryPolicyNode.class.equals(nodeClass)) {
            Persistence.ensureRetrieve(node, AttachLevel.Attached);
            List<ISOProvince> provList = ISOProvince.forCountry(((CountryPolicyNode) node.cast()).country().getValue());
            if (provList == null || provList.size() == 0) {
                return null;
            }
            EntityQueryCriteria<ProvincePolicyNode> criteria = new EntityQueryCriteria<>(ProvincePolicyNode.class);
            criteria.add(PropertyCriterion.in(criteria.proto().province(), provList));
            return Persistence.service().query(criteria);
        } else if (OrganizationPoliciesNode.class.equals(nodeClass)) {
            // no CountryPolicyNode is currently used, so we return next level nodes - ProvincePolicyNodes
            return Persistence.service().query(new EntityQueryCriteria<>(ProvincePolicyNode.class));
        } else {
            throw new Error("Got unknown type of " + PolicyNode.class.getName() + ": '" + nodeClass.getName() + "'");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends PolicyNode> List<T> descendantsOf(PolicyNode parent, Class<T> descendant) {
        // stop right here if the parent is what we are looking for
        if (parent.getInstanceValueClass().equals(descendant)) {
            return Arrays.asList(parent.<T> cast());
        }

        Class<? extends PolicyNode> nodeClass = parent != null ? (Class<? extends PolicyNode>) parent.getInstanceValueClass() : OrganizationPoliciesNode.class;
        if (!HIERARCHY.contains(descendant)) {
            throw new IllegalArgumentException("Unknown node type: " + descendant.getSimpleName());
        }
        int hPos = HIERARCHY.indexOf(descendant);
        if (hPos >= HIERARCHY.indexOf(nodeClass)) {
            throw new IllegalArgumentException(descendant.getSimpleName() + " is not a descendant of " + nodeClass.getSimpleName());
        }

        // continue down the hierarchy
        List<T> resultList = new ArrayList<T>();
        List<PolicyNode> parentList = Arrays.asList(parent);
        while (parentList != null && parentList.size() > 0) {
            List<PolicyNode> childList = new ArrayList<PolicyNode>();
            for (PolicyNode n : parentList) {
                for (PolicyNode c : childrenOf(n)) {
                    // some nodes, such as Complex, are "transparent", so we get their children if
                    // parent does not exist - that means we have to check for child class here
                    if (c.getInstanceValueClass().equals(descendant)) {
                        resultList.add(c.<T> cast());
                    } else {
                        childList.add(c);
                    }
                }
            }
            if (HIERARCHY.indexOf(parentList.get(0).getInstanceValueClass()) <= hPos) {
                break;
            } else {
                parentList = childList;
            }
        }

        return resultList;
    }

    private static <POLICY extends Policy> POLICY correctAccordingToVistaFeatures(POLICY policy) {
        if (policy instanceof IdAssignmentPolicy) {
            // tune up items in case of YardyInegration mode:
            if (VistaFeatures.instance().yardiIntegration()) {
                for (IdAssignmentItem item : ((IdAssignmentPolicy) policy).items()) {
                    if (IdTarget.systemManagedWhenYardiIntergation().contains(item.target().getValue())) {
                        item.type().setValue(IdAssignmentType.systemManaged);
                    }
                }
            }
        }

        return policy;
    }

    private static <POLICY extends Policy> POLICY createEmptyPolicy(PolicyNode policyNode, Class<POLICY> policyClass) {
        if (EvictionFlowPolicy.class.isAssignableFrom(policyClass)) {
            try {
                POLICY policy = EntityFactory.create(policyClass);
                policy.node().set(policyNode);
                Persistence.service().persist(policy);
                Persistence.service().commit();
                return policy;
            } catch (Throwable ignore) {
            }
        }
        return null;
    }

}
