/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.sync;

import com.pyx4j.unit.server.TestServiceFactory;

import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;

public class TenantServiceSync {
    private Tenant tenantList;

    public Tenant retrieve() {
        tenantList = null;

        TenantService service = TestServiceFactory.create(TenantService.class);

//        service.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
//            @Override
//            public void onSuccess(PotentialTenantList result) {
//                tenantList = result;
//                Assert.assertEquals("We expect first Tenant prepopulated", 1, result.tenants().size());
//            }
//        }, null);
        return tenantList;
    }
}
