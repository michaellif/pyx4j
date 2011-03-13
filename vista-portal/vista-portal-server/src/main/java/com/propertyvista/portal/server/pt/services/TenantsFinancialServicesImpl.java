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
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancialList;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.rpc.pt.services.TenantsFinancialServices;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class TenantsFinancialServicesImpl extends ApplicationEntityServicesImpl implements TenantsFinancialServices {
    private final static Logger log = LoggerFactory.getLogger(TenantsFinancialServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<PotentialTenantFinancial> callback, Long tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        EntityQueryCriteria<PotentialTenantFinancial> criteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        PotentialTenantFinancial financial = secureRetrieve(criteria);
        if (financial == null) {
            log.info("Creating new tenant financial");
            financial = createFinancial();
        }

        callback.onSuccess(financial);
    }

    @Override
    public void save(AsyncCallback<PotentialTenantFinancial> callback, PotentialTenantFinancial tenant) {
//        log.info("Saving charges\n{}", PrintUtil.print(summary));

        applyApplication(tenant);
        secureSave(tenant);

        callback.onSuccess(tenant);
    }

    private PotentialTenantFinancial createFinancial() {
        Application application = PtUserDataAccess.getCurrentUserApplication();

        PotentialTenantFinancial financial = EntityFactory.create(PotentialTenantFinancial.class);
        financial.application().set(application);

        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        PotentialTenantFinancialList finList = EntityFactory.create(PotentialTenantFinancialList.class);
        finList.application().set(application);
        finList.tenantFinancials().add(financial);

        for (PotentialTenantInfo pti : tenantList.tenants()) {
            if (pti.relationship().getValue().equals(Relationship.Applicant)) {
                financial.tenant().set(pti);
                break;
            }
        }

        PersistenceServicesFactory.getPersistenceService().persist(finList);

        return financial;
    }
}
