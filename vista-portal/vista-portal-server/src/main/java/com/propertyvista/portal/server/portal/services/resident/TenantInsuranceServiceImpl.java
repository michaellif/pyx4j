/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.TenantInsuranceFacade;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantInsuranceServiceImpl implements TenantInsuranceService {

    @Override
    public void getTenantInsuranceStatus(AsyncCallback<TenantInsuranceStatusDTO> callback) {

        TenantInsuranceStatusDTO tenantInsuranceStatus = ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub());

        callback.onSuccess(tenantInsuranceStatus);
    }

}
