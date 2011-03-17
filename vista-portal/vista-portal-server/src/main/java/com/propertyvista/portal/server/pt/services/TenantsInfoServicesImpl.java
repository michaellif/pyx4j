/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.rpc.pt.services.TenantsInfoServices;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class TenantsInfoServicesImpl extends ApplicationEntityServicesImpl implements TenantsInfoServices {
    private final static Logger log = LoggerFactory.getLogger(TenantsInfoServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<PotentialTenantInfo> callback, Long tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        EntityQueryCriteria<PotentialTenantInfo> criteria = EntityQueryCriteria.create(PotentialTenantInfo.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), tenantId));
        PotentialTenantInfo tenant = secureRetrieve(criteria);
        if (tenant == null) {
            log.info("Creating new tenant");
            tenant = EntityFactory.create(PotentialTenantInfo.class);
        }

        callback.onSuccess(tenant);
    }

    @Override
    public void save(AsyncCallback<PotentialTenantInfo> callback, PotentialTenantInfo tenant) {
//        log.info("Saving charges\n{}", PrintUtil.print(summary));

        applyApplication(tenant);
        secureSave(tenant);

        callback.onSuccess(tenant);
    }
}
