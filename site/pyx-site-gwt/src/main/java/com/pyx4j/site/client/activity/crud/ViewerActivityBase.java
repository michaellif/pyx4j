/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.activity.crud;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IViewerView;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class ViewerActivityBase<E extends IEntity> extends AbstractActivity implements IViewerView.Presenter {

    protected final IViewerView<E> view;

    protected final AbstractCrudService<E> service;

    protected Key entityId;

    Class<? extends CrudAppPlace> placeClass;

    public ViewerActivityBase(IViewerView<E> view, AbstractCrudService<E> service) {
        this.view = view;
        this.service = service;
        view.setPresenter(this);
    }

    public ViewerActivityBase<E> withPlace(Place place) {
        entityId = null;

        placeClass = ((CrudAppPlace) place).getClass();

        String id;
        if ((id = ((CrudAppPlace) place).getArg(CrudAppPlace.ARG_NAME_ITEM_ID)) != null) {
            entityId = new Key(id);
        }
        assert (entityId != null);
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
                onPopulateSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, entityId);
    }

    public void onPopulateSuccess(E result) {
        view.populate(result);
    }

    @Override
    public void edit() {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
        place.formEditorPlace(entityId);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void cancel() {
        History.back();
    }
}
