/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.onboardingmerchantaccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.pmc.OnboardingMerchantAccountEditorView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.OnboardingMerchantAccountCrudService;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class OnboardingMerchantAccountEditorActivity extends EditorActivityBase<OnboardingMerchantAccountDTO> {

    public OnboardingMerchantAccountEditorActivity(CrudAppPlace place) {
        super(//@formatter:off
                place,
                ManagementVeiwFactory.instance(OnboardingMerchantAccountEditorView.class),
                GWT.<OnboardingMerchantAccountCrudService> create(OnboardingMerchantAccountCrudService.class),
                OnboardingMerchantAccountDTO.class
        );//@formatter:on
    }

    @Override
    protected void createNewEntity(final AsyncCallback<OnboardingMerchantAccountDTO> callback) {
        if (getParentId() == null) {
            throw new UserRuntimeException("Invalid URL");
        }
        PmcCrudService srv = GWT.create(PmcCrudService.class);
        srv.retrieve(new AsyncCallback<PmcDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(PmcDTO result) {
                OnboardingMerchantAccountDTO ent = EntityFactory.create(OnboardingMerchantAccountDTO.class);
                ent.pmc().name().set(result.name());
                callback.onSuccess(ent);
            }
        }, getParentId(), AbstractCrudService.RetrieveTraget.View);
    }
}
