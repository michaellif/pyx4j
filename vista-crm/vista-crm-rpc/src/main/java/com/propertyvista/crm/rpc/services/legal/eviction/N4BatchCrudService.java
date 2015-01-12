/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.rpc.services.legal.eviction;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;

public interface N4BatchCrudService extends AbstractCrudService<N4BatchDTO> {

    void createBatches(AsyncCallback<N4BatchDTO> callback, Vector<Lease> leaseCandidates);

    void serviceBatch(AsyncCallback<String> callback, N4Batch batchId);
}
