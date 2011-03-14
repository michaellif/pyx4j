/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.TenantsView;
import com.propertyvista.portal.client.ptapp.ui.TenantsViewPresenter;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.rpc.pt.services.TenantsServices;

import com.pyx4j.security.client.ClientContext;

public class TenantsActivity extends WizardStepActivity<PotentialTenantList, TenantsViewPresenter> implements TenantsViewPresenter {

    @Inject
    public TenantsActivity(TenantsView view) {
        super(view, PotentialTenantList.class, (TenantsServices) GWT.create(TenantsServices.class));
    }

    @Override
    protected void createNewEntity(PotentialTenantList newEntity, AsyncCallback<PotentialTenantList> callback) {
        newEntity.tenants().clear();
        PotentialTenantInfo first = newEntity.tenants().$();
        first.email().setValue(ClientContext.getUserVisit().getEmail());
        first.status().setValue(Status.Applicant);
        newEntity.tenants().add(first);
        callback.onSuccess(newEntity);
    }

    // TODO: Vlad - fill few tenenats in to ApplicationProgress!!!

}
