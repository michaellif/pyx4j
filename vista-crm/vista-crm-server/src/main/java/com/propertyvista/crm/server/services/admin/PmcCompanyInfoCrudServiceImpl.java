/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author VladL
 */
package com.propertyvista.crm.server.services.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.services.admin.PmcCompanyInfoCrudService;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.settings.PmcCompanyInfo;

public class PmcCompanyInfoCrudServiceImpl implements PmcCompanyInfoCrudService {

    private class CompanyInfoBinder extends CrudEntityBinder<PmcCompanyInfo, PmcCompanyInfoDTO> {

        CompanyInfoBinder() {
            super(PmcCompanyInfo.class, PmcCompanyInfoDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }
    };

    public PmcCompanyInfoCrudServiceImpl() {
    }

    @Override
    public void retrieve(AsyncCallback<PmcCompanyInfoDTO> callback, Key entityId, AbstractCrudService.RetrieveTarget retrieveTarget) {
        PmcCompanyInfo ciBO = Persistence.service().retrieve(EntityQueryCriteria.create(PmcCompanyInfo.class));

        PmcCompanyInfoDTO ciTO = new CompanyInfoBinder().createTO(ciBO);

        ciTO.websiteDnsName().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.site, false));
        ciTO.portalDnsName().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, false));

        callback.onSuccess(ciTO);
    }

    @Override
    public void save(AsyncCallback<Key> callback, PmcCompanyInfoDTO editableEntity) {
        PmcCompanyInfo ciBO = new CompanyInfoBinder().createBO(editableEntity);

        Persistence.service().merge(ciBO);
        Persistence.service().commit();

        callback.onSuccess(ciBO.getPrimaryKey());
    }

    @Override
    public void create(AsyncCallback<Key> callback, PmcCompanyInfoDTO editableEntity) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void init(AsyncCallback<PmcCompanyInfoDTO> callback, AbstractCrudService.InitializationData initializationData) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PmcCompanyInfoDTO>> callback, EntityListCriteria<PmcCompanyInfoDTO> criteria) {
        throw new Error("Invalid Operation");
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("Invalid Operation");
    }
}
