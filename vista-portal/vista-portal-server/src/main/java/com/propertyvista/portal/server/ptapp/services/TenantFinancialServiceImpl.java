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
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class TenantFinancialServiceImpl extends ApplicationEntityServiceImpl implements TenantFinancialService {

    private final static Logger log = LoggerFactory.getLogger(TenantFinancialServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<PotentialTenantFinancial> callback, Key tenantId) {
        log.debug("Retrieving summary for tenant {}", tenantId);
        EntityQueryCriteria<PotentialTenantFinancial> criteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), tenantId));
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        PotentialTenantFinancial financial = secureRetrieve(criteria);
        if (financial == null) {
            log.debug("Creating new tenant financial for {}", tenantId);
            financial = createFinancial(tenantId);
        }

        callback.onSuccess(financial);
    }

    @Override
    public void save(AsyncCallback<PotentialTenantFinancial> callback, PotentialTenantFinancial tenantFinancial) {
        log.debug("Saving tenantFinancial {}", tenantFinancial);

        saveApplicationEntity(tenantFinancial);
        callback.onSuccess(tenantFinancial);
    }

    private PotentialTenantFinancial createFinancial(Key tenantId) {
        Application application = PtAppContext.getCurrentUserApplication();

        EntityQueryCriteria<PotentialTenantInfo> criteria = EntityQueryCriteria.create(PotentialTenantInfo.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), tenantId));
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantInfo tenant = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if ((tenant == null) || (tenant.isEmpty())) {
            throw new Error("Tenenat not found");
        }

        PotentialTenantFinancial financial = EntityFactory.create(PotentialTenantFinancial.class);
        financial.id().setValue(tenantId);
        financial.application().set(application);

        PersistenceServicesFactory.getPersistenceService().persist(financial);

        return financial;
    }
}
