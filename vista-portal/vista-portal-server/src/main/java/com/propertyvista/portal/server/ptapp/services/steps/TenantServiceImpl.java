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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.ApplicationProgressMgr;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.portal.server.ptapp.util.ChargesServerCalculation;
import com.propertyvista.server.common.util.TenantConverter;

public class TenantServiceImpl extends ApplicationEntityServiceImpl implements TenantService {

    private final static Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantInApplicationListDTO> callback, Key tenantId) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<TenantInApplicationListDTO> callback, TenantInApplicationListDTO tenants) {
        OnlineApplication application = PtAppContext.retrieveCurrentUserApplication();

        Lease lease = PtAppContext.retrieveCurrentUserLease();
        Persistence.service().retrieve(lease.version().tenants());

        List<Tenant> existingTenants = new Vector<Tenant>(lease.version().tenants());
        lease.version().tenants().clear();

        TenantInApplicationListDTO currentTenants = EntityFactory.create(TenantInApplicationListDTO.class);
        int no = 0;
        for (TenantInLeaseDTO tenantInApplication : tenants.tenants()) {
            // Find existing record
            Tenant tenantInLease = EntityFactory.create(Tenant.class);
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
                if (!EntityGraph.memebersEquals(tenantInApplication.customer().person().name(), tenantInLease.customer().person().name(), tenantInApplication
                        .customer().person().name().firstName(), tenantInApplication.customer().person().name().middleName(), tenantInApplication.customer()
                        .person().name().lastName())) {
                    tenantInApplication.changeStatus().setValue(TenantInLeaseDTO.ChangeStatus.Updated);
                }
            }

            // save Tenant in Lease: 
            tenantInLease.leaseV().set(lease.version());
            tenantInLease.orderInLease().setValue(no++);
            new TenantConverter.TenantEditorConverter().copyDTOtoDBO(tenantInApplication, tenantInLease);
            Persistence.service().merge(tenantInLease.customer());
            Persistence.service().merge(tenantInLease);
            lease.version().tenants().add(tenantInLease);

            // update current tenants:
            tenantInApplication.setPrimaryKey(tenantInLease.getPrimaryKey());
            currentTenants.tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
        }

        // remove deleted tenants:
        for (Tenant orphan : existingTenants) {
            Persistence.service().delete(orphan);
        }

        DigitalSignatureMgr.update(application, lease.version().tenants());
        ApplicationProgressMgr.syncronizeApplicationProgress(application, tenants.tenants());

        // re-calculate charges:
        Charges charges = retrieveApplicationEntity(Charges.class);
        if (charges != null) {
            if (ChargesServerCalculation.updatePaymentSplitCharges(charges, lease.version().tenants())) {
                ApplicationProgressMgr.invalidateChargesStep(application);
                Persistence.secureSave(charges);

                log.info("Charges have been re-calculated");
            }
        }

        Persistence.service().commit();

//        CampaignManager.fireEvent(CampaignTriger.Registration, tenants);

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(currentTenants);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public TenantInApplicationListDTO retrieveData() {
        Lease lease = PtAppContext.retrieveCurrentUserLease();
        Persistence.service().retrieve(lease.version().tenants());

        TenantInApplicationListDTO tenants = EntityFactory.create(TenantInApplicationListDTO.class);
        for (Tenant tenantInLease : lease.version().tenants()) {
            Persistence.service().retrieve(tenantInLease);
            tenants.tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
        }

        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit(), RestrictionsPolicy.class);
        if (restrictionsPolicy == null) {
            throw new Error("There is no RestrictionsPolicy for the Unit!?.");
        }

        // calculate allowed number of occupants:
        Persistence.service().retrieve(lease.unit());
        tenants.tenantsMaximum().setValue((int) (lease.unit().info()._bedrooms().getValue() * restrictionsPolicy.occupantsPerBedRoom().getValue()));

        return tenants;
    }
}
