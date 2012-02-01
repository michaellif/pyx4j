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

import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.GuarantorFinancialService;
import com.propertyvista.portal.server.ptapp.services.ApplicationEntityServiceImpl;
import com.propertyvista.portal.server.ptapp.services.util.DigitalSignatureMgr;
import com.propertyvista.server.common.util.PersonGuarantorRetriever;
import com.propertyvista.server.common.util.TenantConverter;

public class GuarantorFinancialServiceImpl extends ApplicationEntityServiceImpl implements GuarantorFinancialService {

    private final static Logger log = LoggerFactory.getLogger(GuarantorFinancialServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<TenantFinancialDTO> callback, Key tenantId) {
        log.debug("Retrieving financial for tenant {}", tenantId);
        callback.onSuccess(retrieveData(new PersonGuarantorRetriever(tenantId, true)));
    }

    @Override
    public void save(AsyncCallback<TenantFinancialDTO> callback, TenantFinancialDTO entity) {
        log.debug("Saving tenantFinancial {}", entity);

        PersonGuarantorRetriever tr = new PersonGuarantorRetriever(entity.getPrimaryKey(), true);
        new TenantConverter.TenantFinancialEditorConverter().copyDTOtoDBO(entity, tr.tenantScreening);

        tr.saveScreening();

        DigitalSignatureMgr.reset(tr.getGuarantor());

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData(tr));
    }

    public TenantFinancialDTO retrieveData(PersonGuarantorRetriever tr) {
        TenantFinancialDTO dto = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        dto.setPrimaryKey(tr.personGuarantor.getPrimaryKey());
        dto.person().set(tr.getPerson());
        return dto;
    }
}
