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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.TenantInfoService;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantRetriever;

public class TenantInfoServiceImpl implements TenantInfoService {

    private final static Logger log = LoggerFactory.getLogger(TenantInfoServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantInfoDTO> callback, Key tenantId) {
        log.debug("Retrieving Info for tenant {}", tenantId);
        callback.onSuccess(retrieveData(new TenantRetriever(tenantId)));
    }

    @Override
    public void save(AsyncCallback<TenantInfoDTO> callback, TenantInfoDTO entity) {
        log.debug("Saving Tenant Info {}", entity);

        TenantRetriever tr = new TenantRetriever(entity.getPrimaryKey());
        new TenantConverter.Tenant2TenantInfo().copyTOtoBO(entity, tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyTOtoBO(entity, tr.getScreening());

        tr.saveCustomer();

        entity = new TenantConverter.Tenant2TenantInfo().createTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyBOtoTO(tr.getScreening(), entity);
        entity.setPrimaryKey(tr.getTenant().getPrimaryKey());

        DigitalSignatureMgr.reset(tr.getCustomer());
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData(tr));
    }

    public TenantInfoDTO retrieveData(TenantRetriever tr) {
        TenantInfoDTO dto = new TenantConverter.Tenant2TenantInfo().createTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyBOtoTO(tr.getScreening(), dto);
        dto.setPrimaryKey(tr.getTenant().getPrimaryKey());
        return dto;
    }
}
