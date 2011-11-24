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
import com.propertvista.generator.PTGenerator;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class TenantFinancialServiceImpl extends ApplicationEntityServiceImpl implements TenantFinancialService {

    private final static Logger log = LoggerFactory.getLogger(TenantFinancialServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantFinancialDTO> callback, Key tenantId) {
        log.debug("Retrieving financial for tenant {}", tenantId);
        callback.onSuccess(retrieveData(new TenantInLeaseRetriever(tenantId, true)));
    }

    @Override
    public void save(AsyncCallback<TenantFinancialDTO> callback, TenantFinancialDTO dto) {
        log.debug("Saving tenantFinancial {}", dto);

        TenantInLeaseRetriever r = new TenantInLeaseRetriever(dto.getPrimaryKey(), true);

        new TenantConverter.TenantFinancialEditorConverter().copyDTOtoDBO(dto, r.tenantScreening);

        Persistence.service().merge(r.tenantScreening);

        dto = new TenantConverter.TenantFinancialEditorConverter().createDTO(r.tenantScreening);
        dto.setPrimaryKey(r.tenantInLease.getPrimaryKey());

        callback.onSuccess(dto);
    }

    public TenantFinancialDTO retrieveData(TenantInLeaseRetriever tr) {

        TenantFinancialDTO dto = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        dto.setPrimaryKey(tr.tenantInLease.getPrimaryKey());

        SharedData.init();

        IncomeInfoEmployer income1 = EntityFactory.create(IncomeInfoEmployer.class);
        income1.set(PTGenerator.createEmployer());
        dto.incomes2().add(income1);

        IncomeInfoSelfEmployed income2 = EntityFactory.create(IncomeInfoSelfEmployed.class);
        income2.set(PTGenerator.createSelfEmployed());
        dto.incomes2().add(income2);

        return dto;
    }
}
