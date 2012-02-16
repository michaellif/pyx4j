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
package com.propertyvista.crm.server.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.billing.StartBuildingBillingDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingExecutionServiceImpl implements BillingExecutionService {

    @Override
    public void startBilling(AsyncCallback<String> callback, Key leaseEntityId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseEntityId);
        callback.onSuccess(DeferredProcessRegistry.fork(new BillLeaseDeferredProcess(lease), ThreadPoolNames.BILL_SINGLE));
    }

    @Override
    public void startBilling(AsyncCallback<String> callback, StartBuildingBillingDTO startBuildingBilling) {
        Building building = Persistence.secureRetrieve(Building.class, startBuildingBilling.buildingId().getValue());
        callback.onSuccess(DeferredProcessRegistry.fork(new BillBuildingDeferredProcess(building, startBuildingBilling), ThreadPoolNames.BILL_BUILDING));
    }
}
