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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.common.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.ptapp.dto.TenantEditorDTO;
import com.propertyvista.portal.domain.ptapp.dto.TenantListEditorDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.util.TenantConverter;

public class TenantServiceImpl extends ApplicationEntityServiceImpl implements TenantService {

    private final static Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantListEditorDTO> callback, Key tenantId) {
        log.info("Retrieving tenant list");
        TenantListEditorDTO tenants = EntityFactory.create(TenantListEditorDTO.class);

        Lease lease = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, PtAppContext.getCurrentUserApplicationPrimaryKey());
        List<TenantInLease> tenantsInLease = lease.tenants();

        for (TenantInLease tenantInLease : tenantsInLease) {
            PersistenceServicesFactory.getPersistenceService().retrieve(tenantInLease);
            PersistenceServicesFactory.getPersistenceService().retrieve(tenantInLease.tenant());

            tenants.tenants().add(new TenantConverter.TenantEditorConverter().dto(tenantInLease));
        }

        //TODO use policy
        tenants.tenantsMaximum().setValue(6);

        callback.onSuccess(tenants);
    }

    @Override
    public void save(AsyncCallback<TenantListEditorDTO> callback, TenantListEditorDTO tenants) {
        Lease lease = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, PtAppContext.getCurrentUserApplicationPrimaryKey());
        List<TenantInLease> existingTenants = new Vector<TenantInLease>();
        existingTenants.addAll(lease.tenants());
        lease.tenants().clear();
        TenantListEditorDTO ret = EntityFactory.create(TenantListEditorDTO.class);
        for (TenantEditorDTO dto : tenants.tenants()) {
            // Find existing record
            TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
            tenantInLease.setPrimaryKey(dto.getPrimaryKey());
            int idx = existingTenants.indexOf(tenantInLease);
            if (idx == -1) {
                if (!dto.id().isNull()) {
                    throw new SecurityViolationException("Invalid data access");
                }
                dto.changeStatus().setValue(TenantEditorDTO.ChangeStatus.New);
            } else {
                existingTenants.remove(idx);
                PersistenceServicesFactory.getPersistenceService().retrieve(tenantInLease);
                PersistenceServicesFactory.getPersistenceService().retrieve(tenantInLease.tenant());

                if (!EntityGraph.memebersEquals(dto.person().name(), tenantInLease.tenant().person().name(), dto.person().name().firstName(), dto.person()
                        .name().middleName(), dto.person().name().lastName())) {
                    dto.changeStatus().setValue(TenantEditorDTO.ChangeStatus.Updated);
                }
            }

            new TenantConverter.TenantEditorConverter().toDbo(dto, tenantInLease);

            PersistenceServicesFactory.getPersistenceService().persist(tenantInLease.tenant());
            PersistenceServicesFactory.getPersistenceService().persist(tenantInLease);
            lease.tenants().add(tenantInLease);
            ret.tenants().add(new TenantConverter.TenantEditorConverter().dto(tenantInLease));
        }

        PersistenceServicesFactory.getPersistenceService().persist(lease);

        for (TenantInLease orphant : existingTenants) {
            PersistenceServicesFactory.getPersistenceService().delete(orphant);
        }

        ApplicationProgressMgr.syncroizeApplicationProgress(tenants.tenants());

        //TODO use policy
        ret.tenantsMaximum().setValue(6);

        //TODO

//        // we need to load charges and re-calculate them
//        log.info("Load charges and re-calculate them");
//        EntityQueryCriteria<Charges> criteria = EntityQueryCriteria.create(Charges.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().application(), tenants.application()));
//        Charges charges = secureRetrieve(criteria);
//
//        if (charges != null) {
//            ChargesServerCalculation.updatePaymentSplitCharges(charges, tenantsOrig);
//            secureSave(charges);
//            log.info("Re-calculated and saved charges");
//        }
//
//        CampaignManager.fireEvent(CampaignTriger.Registration, tenants);

        callback.onSuccess(ret);
    }
}
