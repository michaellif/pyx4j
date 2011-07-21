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

import com.propertyvista.portal.domain.ptapp.dto.TenantFinancialEditorDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.portal.server.ptapp.util.TenantConverter;
import com.propertyvista.server.domain.generator.TenantSummaryDTO;

public class TenantFinancialServiceImpl extends ApplicationEntityServiceImpl implements TenantFinancialService {

    private final static Logger log = LoggerFactory.getLogger(TenantFinancialServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantFinancialEditorDTO> callback, Key tenantId) {
        log.debug("Retrieving financials for tenant {}", tenantId);
        TenantSummaryDTO summary = TenantInfoServiceImpl.getTenantSummaryDTO(tenantId);

        TenantFinancialEditorDTO dto = new TenantConverter.TenantFinancialEditorConverter().dto(summary);
        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<TenantFinancialEditorDTO> callback, TenantFinancialEditorDTO dto) {
        log.debug("Saving tenantFinancial {}", dto);
        TenantSummaryDTO summary = TenantInfoServiceImpl.getTenantSummaryDTO(dto.getPrimaryKey());

        new TenantConverter.TenantFinancialEditorConverter().toDbo(dto, summary);

        PersistenceServicesFactory.getPersistenceService().merge(summary.tenantScreening());

        dto = new TenantConverter.TenantFinancialEditorConverter().dto(summary);
        callback.onSuccess(dto);
    }

}
