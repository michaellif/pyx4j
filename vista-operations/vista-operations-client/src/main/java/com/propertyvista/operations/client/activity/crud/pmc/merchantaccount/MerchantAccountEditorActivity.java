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
package com.propertyvista.operations.client.activity.crud.pmc.merchantaccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.MerchantAccountEditorView;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;

public class MerchantAccountEditorActivity extends AbstractEditorActivity<PmcMerchantAccountDTO> {

    public MerchantAccountEditorActivity(CrudAppPlace place) {
        super(//@formatter:off
                place,
 OperationsSite.getViewFactory().instantiate(MerchantAccountEditorView.class),
                GWT.<PmcMerchantAccountCrudService> create(PmcMerchantAccountCrudService.class),
                PmcMerchantAccountDTO.class
        );//@formatter:on
    }

    @Override
    protected void createNewEntity(final AsyncCallback<PmcMerchantAccountDTO> callback) {
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
                PmcMerchantAccountDTO ent = EntityFactory.create(PmcMerchantAccountDTO.class);
                ent.pmc().name().set(result.name());
                ent.merchantAccount().invalid().setValue(Boolean.FALSE);
                callback.onSuccess(ent);
            }
        }, getParentId(), AbstractCrudService.RetrieveTarget.View);
    }
}
