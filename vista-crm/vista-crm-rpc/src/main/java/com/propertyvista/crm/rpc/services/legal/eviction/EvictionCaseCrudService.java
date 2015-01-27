/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.crm.rpc.services.legal.eviction;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.EvictionCaseDTO;

public interface EvictionCaseCrudService extends AbstractCrudService<EvictionCaseDTO> {

    public interface EvictionCaseInitData extends InitializationData {
        Lease lease();
    }

    void hasEvictionFlow(AsyncCallback<Boolean> callback, Key leaseId);

    void issueN4(AsyncCallback<String> callback, EvictionCase caseId);
}
