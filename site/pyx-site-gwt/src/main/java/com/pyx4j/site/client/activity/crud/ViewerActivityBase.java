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
package com.pyx4j.site.client.activity.crud;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IViewerView;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;


public class ViewerActivityBase<E extends IEntity> extends AbstractActivity implements IViewerView.Presenter {

    private final IViewerView<E> view;

    private final AbstractCrudService<E> service;

    private Key entityId;

    public ViewerActivityBase(IViewerView<E> view, AbstractCrudService<E> service) {
        this.view = view;
        this.service = service;
        view.setPresenter(this);
    }

    public ViewerActivityBase<E> withPlace(Place place) {
        entityId = new Key(((AppPlace) place).getArg(CrudAppPlace.ARG_NAME_ITEM_ID));
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        service.retrieve(new AsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                view.populate(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, entityId);
    }

    @Override
    public void edit(Class<? extends CrudAppPlace> editPlaceClass) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(editPlaceClass);
        place.formEditorPlace(entityId);
        AppSite.getPlaceController().goTo(place);
    }
}
