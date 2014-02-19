/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.policy;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientSecurityController;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.rpc.PolicyDataSystemNotification;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public class ClientPolicyManager {

    private static class PolicyFindKey {

        PolicyNode node;

        Class<?> policyClass;

        PolicyFindKey(final PolicyNode node, final Class<?> policyClass) {
            this.node = (PolicyNode) node.createIdentityStub();
            this.policyClass = policyClass;
        }

        @Override
        public boolean equals(Object obj) {
            return node.equals(((PolicyFindKey) obj).node) && policyClass.equals(((PolicyFindKey) obj).policyClass);
        }

        @Override
        public int hashCode() {
            int hashCode = node.hashCode();
            hashCode *= 0x1F;
            hashCode += policyClass.hashCode();
            return hashCode;
        }

        @Override
        public String toString() {
            return "Key " + policyClass.getName() + " " + node.toString();
        }
    }

    private static OrganizationPoliciesNode organizationPoliciesNode;

    private static final Map<PolicyFindKey, Policy> cache = new HashMap<PolicyFindKey, Policy>();

    private static PolicyRetrieveService policyRetrieveService;

    public static OrganizationPoliciesNode getOrganizationPoliciesNode() {
        // This is done for cache to Work on the client. cache needs  Node Pk.
        if (organizationPoliciesNode == null) {
            return EntityFactory.create(OrganizationPoliciesNode.class);
        } else {
            return organizationPoliciesNode;
        }
    }

    @SuppressWarnings("unchecked")
    public static <POLICY extends Policy> void obtainEffectivePolicy(final PolicyNode node, final Class<POLICY> policyClass,
            final AsyncCallback<POLICY> callback) {
        Policy policy = cache.get(new PolicyFindKey(node, policyClass));
        if (policy != null) {
            callback.onSuccess((POLICY) policy);
            return;
        }

        policyRetrieveService.obtainEffectivePolicy(new DefaultAsyncCallback<Policy>() {
            @Override
            public void onSuccess(Policy result) {
                if ((organizationPoliciesNode == null) && (result.node() instanceof OrganizationPoliciesNode)) {
                    organizationPoliciesNode = (OrganizationPoliciesNode) result.node();
                }
                cache.put(new PolicyFindKey(result.node(), policyClass), result);
                callback.onSuccess((POLICY) result);
            }
        }, (PolicyNode) node.createIdentityStub(), EntityFactory.getEntityPrototype(policyClass));
    }

    @SuppressWarnings("unchecked")
    public static <POLICY extends Policy> void obtainHierarchicalEffectivePolicy(final IEntity entity, final Class<POLICY> policyClass,
            final AsyncCallback<POLICY> callback) {
        policyRetrieveService.obtainHierarchicalEffectivePolicy(new DefaultAsyncCallback<Policy>() {
            @Override
            public void onSuccess(Policy result) {
                callback.onSuccess((POLICY) result);
            }
        }, entity.createIdentityStub(), EntityFactory.getEntityPrototype(policyClass));
    }

    public static void initialize(PolicyRetrieveService policyRetrieveService) {
        ClientPolicyManager.policyRetrieveService = policyRetrieveService;

        ClientSecurityController.addSecurityControllerHandler(new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                ClientPolicyManager.invalidate();
            }
        });

        RPCManager.addSystemNotificationHandler(new SystemNotificationHandler() {
            @Override
            public void onSystemNotificationReceived(SystemNotificationEvent event) {
                if (event.getSystemNotification() instanceof PolicyDataSystemNotification) {
                    PolicyDataSystemNotification data = (PolicyDataSystemNotification) event.getSystemNotification();
                    if ((organizationPoliciesNode == null) && (data.node instanceof OrganizationPoliciesNode)) {
                        organizationPoliciesNode = (OrganizationPoliciesNode) data.node;
                    }
                    cache.put(new PolicyFindKey(data.node, data.policyClass.getValueClass()), data.policy);
                }
            }
        });
    }

    protected static void invalidate() {
        cache.clear();
        organizationPoliciesNode = null;
    }

    // helpers-specializations:

    public static void setIdComponentEditabilityByPolicy(final IdTarget idTarget, final CComponent<String> idComp, final Key entityKey) {
        idComp.setVisible(true);
        idComp.setEditable(true);
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), IdAssignmentPolicy.class,
                new DefaultAsyncCallback<IdAssignmentPolicy>() {
                    @Override
                    public void onSuccess(IdAssignmentPolicy result) {
                        IdAssignmentItem targetItem = null;
                        for (IdAssignmentItem item : result.items()) {
                            if (item.target().getValue() == idTarget) {
                                targetItem = item;
                                break;
                            }
                        }

                        if (targetItem != null) {
                            switch (targetItem.type().getValue()) {
                            case generatedAlphaNumeric:
                            case generatedNumber:
                                idComp.setEditable(false);
                                idComp.setVisible(entityKey != null);
                                break;
                            case userEditable:
                                idComp.setEditable(true);
                                break;
                            case userAssigned:
                                idComp.setEditable(entityKey == null);
                                break;
                            }
                        }
                    }
                });
    }
}
