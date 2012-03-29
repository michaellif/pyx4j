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
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.rpc.PolicyDataSystemNotification;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;

public class PolicyManager {

    // TODO keep this in some kind of "PoliciesHeriarchy" class, pass as dependency in the constructor
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends PolicyNode>> HIERARCHY = Arrays.asList(AptUnit.class, Floorplan.class, Building.class, Complex.class,
            Province.class, Country.class, OrganizationPoliciesNode.class);

    /**
     * 
     * @param node
     *            not <code>null</code>.
     * @param policyClass
     *            not <code>null</code>.
     * @return policy at the requested organization policies hierarchy node or <code>null</code>.
     */
    public static <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass) {
        POLICY policy = null;
        if ((node != null) && node.isInstanceOf(OrganizationPoliciesNode.class) && node.isNull()) {
            node = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        }

        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("node must not be null");
        }
        PolicyNode currentNode = node.duplicate();
        if (currentNode.isValueDetached()) {
            Persistence.service().retrieve(currentNode);
        }
        do {
            EntityQueryCriteria<POLICY> criteria = EntityQueryCriteria.create(policyClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().node(), currentNode));
            policy = Persistence.service().retrieve(criteria);

            if (policy != null) {
                break;
            }
            currentNode = parentOf(currentNode);

        } while (currentNode != null);

        if (policy == null) {
            String nodeStringView = null;
            if (node.isValueDetached()) {
                nodeStringView = ((PolicyNode) Persistence.secureRetrieve(node.getInstanceValueClass(), node.getPrimaryKey())).getStringView();
            } else {
                nodeStringView = node.getStringView();
            }
            throw new PolicyNotFoundException(policyClass, nodeStringView);
        }
        return policy;
    }

    public static <POLICY extends Policy> void sendPolicyToClient(final PolicyNode node, final Class<POLICY> policyClass, POLICY policy) {
        PolicyDataSystemNotification data = new PolicyDataSystemNotification();
        data.policy = policy;
        data.policyClass = EntityFactory.getEntityPrototype(policyClass);
        data.node = (PolicyNode) node.createIdentityStub();
        Context.addResponseSystemNotification(data);
    }

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
            return Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));

        } else if (Province.class.equals(nodeClass)) {

            return Persistence.service().retrieve(Country.class, ((Province) node.cast()).country().getPrimaryKey());

        } else if (Country.class.equals(nodeClass)) {

            // we assume that one organization policies node is preloaded and present in the system
            return Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));

        } else if (OrganizationPoliciesNode.class.equals(nodeClass)) {

            return null;

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
