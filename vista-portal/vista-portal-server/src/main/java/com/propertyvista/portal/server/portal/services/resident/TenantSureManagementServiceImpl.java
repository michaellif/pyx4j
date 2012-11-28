/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantSureManagementServiceImpl implements TenantSureManagementService {

    @Override
    public void getStatus(AsyncCallback<TenantSureTenantInsuranceStatusDetailedDTO> callback) {
        TenantSureTenantInsuranceStatusDetailedDTO status = ServerSideFactory.create(TenantSureFacade.class).getStatus(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub());
        callback.onSuccess(status);
    }

    @Override
    public void updatePaymentMethod(AsyncCallback<VoidSerializable> callback, InsurancePaymentMethod paymentMethod) {

        callback.onSuccess(null);
    }

    @Override
    public void cancelTenantSure(AsyncCallback<VoidSerializable> callback) {
        ServerSideFactory.create(TenantSureFacade.class).cancelByTenant(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub());
        callback.onSuccess(null);
    }

}
