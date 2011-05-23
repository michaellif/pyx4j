/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.viewers;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.vewers.IViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.AbstractCrudService;

public class ViewerActivityBase<E extends IEntity> extends AbstractActivity implements IViewerView.Presenter {

    private final IViewerView<E> view;

    private final AbstractCrudService<E> service;

    private long entityId = -1;

    @Inject
    public ViewerActivityBase(IViewerView<E> view, AbstractCrudService<E> service) {
        this.view = view;
        this.service = service;
        view.setPresenter(this);
    }

    public ViewerActivityBase<E> withPlace(Place place) {
        String stepArg = ((AppPlace) place).getArgs().get(CrmSiteMap.ARG_NAME_ITEM_ID);
        if (stepArg != null) {
            entityId = Long.valueOf(stepArg);
        }

        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        if (service != null) {
            service.retrieve(new AsyncCallback<E>() {

                @Override
                public void onSuccess(E result) {
                    view.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }

    @Override
    public void edit(AppPlace editPlace) {
        AppSite.getPlaceController().goTo(CrmSiteMap.formItemPlace(editPlace, entityId));
    }
}
