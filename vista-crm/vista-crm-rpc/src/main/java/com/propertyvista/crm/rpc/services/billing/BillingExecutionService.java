/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.tenant.lease.Lease;

public interface BillingExecutionService extends IService {

    /**
     * Run single bill
     */
    void startBilling(AsyncCallback<String> callback, Lease leaseEntityId);

    /**
     * Run single billing for selected Building
     */
    void startBilling(AsyncCallback<String> callback, StartBuildingBillingDTO startBuildingBilling);

}
