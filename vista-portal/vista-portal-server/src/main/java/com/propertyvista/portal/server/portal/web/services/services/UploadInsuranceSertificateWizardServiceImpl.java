/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.portal.rpc.portal.web.services.services.UploadInsuranceSertificateWizardService;

public class UploadInsuranceSertificateWizardServiceImpl implements UploadInsuranceSertificateWizardService {

    @Override
    public void init(AsyncCallback<InsuranceGeneric> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<InsuranceGeneric> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        // TODO Auto-generated method stub

    }

    @Override
    public void create(AsyncCallback<Key> callback, InsuranceGeneric editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<Key> callback, InsuranceGeneric editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<InsuranceGeneric>> callback, EntityListCriteria<InsuranceGeneric> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

}
