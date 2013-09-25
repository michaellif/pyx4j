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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantFinancialService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantRetriever;

public class TenantFinancialServiceImpl extends ApplicationEntityServiceImpl implements TenantFinancialService {

    private final static Logger log = LoggerFactory.getLogger(TenantFinancialServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantFinancialDTO> callback, Key tenantId) {
        log.debug("Retrieving financial for tenant {}", tenantId);
        callback.onSuccess(retrieveData(new TenantRetriever(tenantId, true)));
    }

    @Override
    public void save(AsyncCallback<TenantFinancialDTO> callback, TenantFinancialDTO entity) {
        log.debug("Saving tenantFinancial {}", entity);

        // TODO: check new/deleted PersonalGuarantor and correct Guarantors accordingly!..

        Lease lease = PtAppContext.retrieveCurrentUserLease();
        List<LeaseTermGuarantor> currentGuarantors = lease.currentTerm().version().guarantors();

        TenantRetriever tr = new TenantRetriever(entity.getPrimaryKey(), true);
        new TenantConverter.TenantFinancialEditorConverter().copyTOtoBO(entity, tr.getScreening());

        for (LeaseTermGuarantor pg : entity.guarantors()) {
            int idx = currentGuarantors.indexOf(pg);
            if (idx >= 0) {
                currentGuarantors.remove(idx);
            } else {
                // new item - perform initialization:
                pg.tenant().set(tr.getTenant());
                pg.leaseTermV().set(lease.currentTerm().version());
                Persistence.service().merge(pg);
            }
        }

        // remove deleted guarantors:
        for (LeaseTermGuarantor orphan : currentGuarantors) {
            Persistence.service().delete(orphan);
        }

        tr.saveScreening();

        DigitalSignatureMgr.reset(tr.getCustomer());
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData(tr));
    }

    public TenantFinancialDTO retrieveData(TenantRetriever tr) {
        TenantFinancialDTO dto = new TenantConverter.TenantFinancialEditorConverter().createTO(tr.getScreening());
        dto.setPrimaryKey(tr.getTenant().getPrimaryKey());
        dto.person().set(tr.getPerson());

//        SharedData.init();
//
//        IncomeInfoEmployer income1 = EntityFactory.create(IncomeInfoEmployer.class);
//        income1.set(PTGenerator.createEmployer());
//        dto.incomes2().add(income1);
//
//        IncomeInfoSelfEmployed income2 = EntityFactory.create(IncomeInfoSelfEmployed.class);
//        income2.set(PTGenerator.createSelfEmployed());
//        dto.incomes2().add(income2);

        return dto;
    }
}
