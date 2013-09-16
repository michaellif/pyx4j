/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class InsuranceServiceImpl implements InsuranceService {

    @Override
    public void retreiveInsuranceStatus(AsyncCallback<InsuranceStatusDTO> callback) {
        if (true) {
            new InsuranceServiceMockImpl().retreiveInsuranceStatus(callback);
        } else {
            callback.onSuccess(ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(
                    TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().<Tenant> createIdentityStub()));
        }
    }

}
