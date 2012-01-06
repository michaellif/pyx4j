/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.policies;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.PolicyNode;

public interface OrganizationPolicyBrowserService extends IService {

    /**
     * Retrieve child nodes of a {@link PolicyNode}.
     * 
     * @param callback
     *            the return value.
     * @param policyNode
     *            can be <code>null</code> if someone wants to get the organization node (or it can be instance of {@link OrganizationPoliciesNode} with no PK).
     *            To
     *            get children organization node use instance of {@link OrganizationPoliciesNode} (can be with no PK). the rest of child nodes must be queried
     *            using a policyNode with associated PK.
     * @return see <code>callback</code> parameter.
     */
    void getChildNodes(AsyncCallback<Vector<PolicyNode>> callback, PolicyNode policyNode);
}
