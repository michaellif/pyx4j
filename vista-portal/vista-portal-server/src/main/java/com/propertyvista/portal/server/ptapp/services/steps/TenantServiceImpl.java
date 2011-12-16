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
package com.propertyvista.portal.server.ptapp.services.steps;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.ApplicationProgressMgr;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.portal.server.ptapp.util.ChargesServerCalculation;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class TenantServiceImpl extends ApplicationEntityServiceImpl implements TenantService {

    private final static Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantInApplicationListDTO> callback, Key tenantId) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<TenantInApplicationListDTO> callback, TenantInApplicationListDTO tenants) {
        Application application = PtAppContext.getCurrentUserApplication();

        Lease lease = PtAppContext.getCurrentUserLease();
        TenantInLeaseRetriever.UpdateLeaseTenants(lease);

        List<TenantInLease> existingTenants = new Vector<TenantInLease>(lease.tenants());
        lease.tenants().clear();

        TenantInApplicationListDTO currentTenants = EntityFactory.create(TenantInApplicationListDTO.class);
        for (TenantInLeaseDTO tenantInApplication : tenants.tenants()) {
            // Find existing record
            TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
            tenantInLease.setPrimaryKey(tenantInApplication.getPrimaryKey());
            int idx = existingTenants.indexOf(tenantInLease);
            if (idx == -1) {
                if (!tenantInApplication.id().isNull()) {
                    throw new SecurityViolationException("Invalid data access");
                }
                tenantInApplication.changeStatus().setValue(TenantInLeaseDTO.ChangeStatus.New);
            } else {
                existingTenants.remove(idx);
                Persistence.service().retrieve(tenantInLease);
                if (!EntityGraph.memebersEquals(tenantInApplication.tenant().person().name(), tenantInLease.tenant().person().name(), tenantInApplication
                        .tenant().person().name().firstName(), tenantInApplication.tenant().person().name().middleName(), tenantInApplication.tenant().person()
                        .name().lastName())) {
                    tenantInApplication.changeStatus().setValue(TenantInLeaseDTO.ChangeStatus.Updated);
                }
            }

            // save Tenant in Lease: 
            tenantInLease.lease().set(lease);
            new TenantConverter.TenantEditorConverter().copyDTOtoDBO(tenantInApplication, tenantInLease);
            Persistence.service().merge(tenantInLease.tenant());
            Persistence.service().merge(tenantInLease);
            lease.tenants().add(tenantInLease);

            // update current tenants:
            tenantInApplication.setPrimaryKey(tenantInLease.getPrimaryKey());
            currentTenants.tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
        }

        Persistence.service().merge(lease);

        // remove deleted tenants:
        for (TenantInLease orphan : existingTenants) {
            Persistence.service().delete(orphan);
        }

        DigitalSignatureMgr.update(application, lease.tenants());
        ApplicationProgressMgr.syncroizeApplicationProgress(application, tenants.tenants());

        // re-calculate charges:
        Charges charges = retrieveApplicationEntity(Charges.class);
        if (charges != null) {
            if (ChargesServerCalculation.updatePaymentSplitCharges(charges, lease.tenants())) {
                ApplicationProgressMgr.invalidateChargesStep(application);
                secureSave(charges);

                log.info("Charges have been re-calculated");
            }
        }

//        CampaignManager.fireEvent(CampaignTriger.Registration, tenants);

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(currentTenants);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public TenantInApplicationListDTO retrieveData() {
        Lease lease = PtAppContext.getCurrentUserLease();
        TenantInLeaseRetriever.UpdateLeaseTenants(lease);

        TenantInApplicationListDTO tenants = EntityFactory.create(TenantInApplicationListDTO.class);
        for (TenantInLease tenantInLease : lease.tenants()) {
            Persistence.service().retrieve(tenantInLease);
            tenants.tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
        }

        //TODO use policy
        tenants.tenantsMaximum().setValue(6);

        return tenants;
    }
}
