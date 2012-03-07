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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
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

    private static final Map<PolicyFindKey, Policy> cache = new HashMap<PolicyFindKey, Policy>();

    @SuppressWarnings("unchecked")
    public static <POLICY extends Policy> void obtainEffectivePolicy(final PolicyNode node, final Class<POLICY> policyClass,
            final AsyncCallback<POLICY> callback) {
        Policy policy = cache.get(new PolicyFindKey(node, policyClass));
        if (policy != null) {
            callback.onSuccess((POLICY) policy);
            return;
        }

        PolicyRetrieveService srv = GWT.create(PolicyRetrieveService.class);
        srv.obtainEffectivePolicy(new DefaultAsyncCallback<Policy>() {
            @Override
            public void onSuccess(Policy result) {
                cache.put(new PolicyFindKey(node, policyClass), result);
                callback.onSuccess((POLICY) result);
            }
        }, (PolicyNode) node.createIdentityStub(), EntityFactory.getEntityPrototype(policyClass));
    }

    public static void initialize() {

        ClientSecurityController.addSecurityControllerHandler(new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                ClientPolicyManager.invalidate();
            }
        });

        RPCManager.addSystemNotificationHandler(new SystemNotificationHandler() {
            @Override
            public void onSystemNotificationReceived(SystemNotificationEvent event) {
                if (event.getSystemNotification() instanceof PolicyDataSystemNotification) {
                    PolicyDataSystemNotification data = (PolicyDataSystemNotification) event.getSystemNotification();
                    cache.put(new PolicyFindKey(data.node, data.policyClass.getValueClass()), data.policy);
                }
            }
        });
    }

    protected static void invalidate() {
        cache.clear();
    }
}
