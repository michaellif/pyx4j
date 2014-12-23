/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionCaseEditorView;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService.EvictionCaseInitData;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseEditorActivity extends CrmEditorActivity<EvictionCaseDTO> {

    public EvictionCaseEditorActivity(CrudAppPlace place) {
        super(EvictionCaseDTO.class, place, CrmSite.getViewFactory().getView(EvictionCaseEditorView.class), GWT
                .<EvictionCaseCrudService> create(EvictionCaseCrudService.class));
    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        EvictionCaseInitData initData = EntityFactory.create(EvictionCaseInitData.class);
        initData.lease().set(EntityFactory.createIdentityStub(Lease.class, getParentId()));
        callback.onSuccess(initData);
    }

}
