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

import com.propertyvista.portal.rpc.ptapp.dto.TenantInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;
import com.propertyvista.portal.server.ptapp.util.TenantConverter;

public class TenantInfoServiceImpl implements TenantInfoService {

    private final static Logger log = LoggerFactory.getLogger(TenantInfoServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantInfoDTO> callback, Key tenantId) {
        log.debug("Retrieving Info for tenant {}", tenantId);

        TenantRetriever r = new TenantRetriever(tenantId);

        TenantInfoDTO dto = new TenantConverter.Tenant2TenantInfo().createDTO(r.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(r.tenantScreening, dto);
        dto.setPrimaryKey(r.tenantInLease.getPrimaryKey());

        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<TenantInfoDTO> callback, TenantInfoDTO dto) {
        log.debug("Saving Tenant Info {}", dto);

        TenantRetriever r = new TenantRetriever(dto.getPrimaryKey());

        new TenantConverter.Tenant2TenantInfo().copyDTOtoDBO(dto, r.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDTOtoDBO(dto, r.tenantScreening);

        PersistenceServicesFactory.getPersistenceService().merge(r.tenant);
        PersistenceServicesFactory.getPersistenceService().merge(r.tenantScreening);

        dto = new TenantConverter.Tenant2TenantInfo().createDTO(r.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(r.tenantScreening, dto);
        dto.setPrimaryKey(r.tenantInLease.getPrimaryKey());

        callback.onSuccess(dto);
    }
}
