/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2014
 * @author smolka
 */
package com.propertyvista.crm.server.security.access;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.ThreadPolicyHandle;

public class ThreadPolicyHandleAccessRule implements DatasetAccessRule<ThreadPolicyHandle> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<ThreadPolicyHandle> criteria) {

        criteria.eq(criteria.proto().policyConsumer(), CrmAppContext.getCurrentUserEmployee());
    }

}
