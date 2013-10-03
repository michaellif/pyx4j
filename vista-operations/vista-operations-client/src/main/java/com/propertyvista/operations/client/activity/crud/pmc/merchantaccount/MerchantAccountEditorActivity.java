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

import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.mercantaccount.MerchantAccountEditorView;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.services.PmcMerchantAccountCrudService;

public class MerchantAccountEditorActivity extends AbstractEditorActivity<PmcMerchantAccountDTO> {

    public MerchantAccountEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(MerchantAccountEditorView.class), GWT
                .<PmcMerchantAccountCrudService> create(PmcMerchantAccountCrudService.class), PmcMerchantAccountDTO.class);
    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        PmcMerchantAccountCrudService.PmcMerchantAccountInitializationData id = EntityFactory
                .create(PmcMerchantAccountCrudService.PmcMerchantAccountInitializationData.class);
        id.parent().set(EntityFactory.createIdentityStub(Pmc.class, getParentId()));
        callback.onSuccess(id);
    }
}
