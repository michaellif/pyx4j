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
 */
package com.propertyvista.portal.rpc;

import java.io.Serializable;

import com.pyx4j.entity.core.IEntity;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;

public class PolicyDataSystemNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    public PolicyNode node;

    public IEntity policyClass;

    public Policy policy;
}
