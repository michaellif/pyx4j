/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.application.ApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.application.ApplicationView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.dto.ApplicationDTO;

public class ApplicationEditorActivity extends EditorActivityBase<ApplicationDTO> implements ApplicationEditorView.Presenter {

    private final ApplicationActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public ApplicationEditorActivity(Place place) {
        super((ApplicationEditorView) TenantViewFactory.instance(ApplicationEditorView.class), (AbstractCrudService<ApplicationDTO>) GWT
                .create(ApplicationCrudService.class), ApplicationDTO.class);
        delegate = new ApplicationActivityDelegate((ApplicationView) view);
        withPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return delegate.getBuildingPresenter();
    }

    @Override
    public Presenter getUnitPresenter() {
        return delegate.getUnitPresenter();
    }

    @Override
    public Presenter getTenantPresenter() {
        return delegate.getTenantPresenter();
    }

    @Override
    public void onPopulateSuccess(ApplicationDTO result) {
        super.onPopulateSuccess(result);
        delegate.populate();
    }
}
