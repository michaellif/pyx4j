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
package com.propertyvista.crm.server.services.policy;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.services.policy.OrganizationPolicyBrowserService;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.server.common.policy.PolicyManager;

public class OrganizationPolicyBrowserServiceImpl implements OrganizationPolicyBrowserService {

    @Override
    public void getChildNodes(AsyncCallback<Vector<PolicyNode>> callback, PolicyNode policyNode) {
        callback.onSuccess(new Vector<PolicyNode>(PolicyManager.childrenOf(policyNode)));
    }

}
