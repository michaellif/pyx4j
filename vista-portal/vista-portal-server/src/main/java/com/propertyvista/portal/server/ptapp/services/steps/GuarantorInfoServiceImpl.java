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
import com.propertyvista.portal.rpc.ptapp.services.steps.GuarantorInfoService;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.util.PersonGuarantorRetriever;
import com.propertyvista.server.common.util.TenantConverter;

public class GuarantorInfoServiceImpl implements GuarantorInfoService {

    private final static Logger log = LoggerFactory.getLogger(GuarantorInfoServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantInfoDTO> callback, Key tenantId) {
        log.debug("Retrieving Info for tenant {}", tenantId);
        callback.onSuccess(retrieveData(new PersonGuarantorRetriever(tenantId)));
    }

    @Override
    public void save(AsyncCallback<TenantInfoDTO> callback, TenantInfoDTO entity) {
        log.debug("Saving Tenant Info {}", entity);

        PersonGuarantorRetriever tr = new PersonGuarantorRetriever(entity.getPrimaryKey());
        new TenantConverter.Guarantor2TenantInfo().copyDTOtoDBO(entity, tr.getGuarantor());
        new TenantConverter.TenantScreening2TenantInfo().copyDTOtoDBO(entity, tr.tenantScreening);

        tr.saveTenant();
        tr.saveScreening();

        entity = new TenantConverter.Guarantor2TenantInfo().createDTO(tr.getGuarantor());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, entity);
        entity.setPrimaryKey(tr.personGuarantor.getPrimaryKey());

        DigitalSignatureMgr.reset(tr.getGuarantor().customer());
        Persistence.service().commit();

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData(tr));
    }

    public TenantInfoDTO retrieveData(PersonGuarantorRetriever tr) {
        TenantInfoDTO dto = new TenantConverter.Guarantor2TenantInfo().createDTO(tr.getGuarantor());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, dto);
        dto.setPrimaryKey(tr.personGuarantor.getPrimaryKey());
        return dto;
    }
}
