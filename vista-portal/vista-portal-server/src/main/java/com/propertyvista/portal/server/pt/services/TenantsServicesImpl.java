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
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.rpc.pt.services.TenantsServices;
import com.propertyvista.portal.server.campaign.CampaignManager;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.server.domain.CampaignTriger;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

public class TenantsServicesImpl extends ApplicationEntityServicesImpl implements TenantsServices {
    private final static Logger log = LoggerFactory.getLogger(TenantsServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<PotentialTenantList> callback, Long tenantId) {
        log.info("Retrieving tenant list");

        PotentialTenantList tenants = findApplicationEntity(PotentialTenantList.class);
        if (tenants == null) {
            log.info("Creating new tenant list");
            tenants = EntityFactory.create(PotentialTenantList.class);
            PotentialTenantInfo first = tenants.tenants().$();
            first.email().setValue(Context.getVisit().getUserVisit().getEmail());
            first.status().setValue(Status.Applicant);
            tenants.tenants().add(first);
        }

        callback.onSuccess(tenants);
    }

    @Override
    public void save(AsyncCallback<PotentialTenantList> callback, PotentialTenantList tenants) {
        PotentialTenantList tenantsOrig = findApplicationEntity(PotentialTenantList.class);

        saveApplicationEntity(tenants);

        ApplicationServicesImpl.syncroizeApplicationProgress(tenantsOrig, tenants);

        // we need to load charges and re-calculate them
        log.info("Load charges and re-calculate them");
        EntityQueryCriteria<Charges> criteria = EntityQueryCriteria.create(Charges.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), tenants.application()));
        Charges charges = secureRetrieve(criteria);

        if (charges != null) {
            ChargesServerCalculation.updatePaymentSplitCharges(charges, tenants.application());
            secureSave(charges);
            log.info("Re-calculated and saved charges");
        }

        CampaignManager.fireEvent(CampaignTriger.Registration, tenants);

        callback.onSuccess(tenants);
    }
}
