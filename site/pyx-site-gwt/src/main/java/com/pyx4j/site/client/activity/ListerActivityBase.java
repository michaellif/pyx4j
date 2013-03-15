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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.ui.crud.lister.ILister;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

public class ListerActivityBase<E extends IEntity> extends ListerController<E> implements Activity {

    private final AppPlace place;

    private List<Criterion> externalFilters;

    public ListerActivityBase(Place place, ILister<E> view, AbstractListService<E> service, Class<E> entityClass) {
        super(view, service, entityClass);

        this.place = (AppPlace) place;

        getView().getMemento().setCurrentPlace(place);

        EntityFiltersBuilder<E> filters = EntityFiltersBuilder.create(entityClass);
        parseExternalFilters((AppPlace) place, entityClass, filters);
        if (filters.getFilters().size() > 0) {
            externalFilters = filters.getFilters();
        }
    }

    protected void parseExternalFilters(AppPlace place, Class<E> entityClass, EntityFiltersBuilder<E> filters) {
        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            String ownerMemberName = EntityFactory.getEntityMeta(entityClass).getOwnerMemberName();
            IEntity owner = (IEntity) filters.proto().getMember(ownerMemberName);
            filters.eq(owner, EntityFactory.createIdentityStub(owner.getValueClass(), new Key(val)));
        }
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        getView().discard();
        getView().getLister().setExternalFilters(externalFilters);
        getView().setPresenter(this);
        populate();
        containerWidget.setWidget(getView());
    }

    @Override
    public void onStop() {
        getView().storeState(getView().getMemento().getCurrentPlace());
        getView().discard();
    }

    @Override
    public String mayStop() {
        return null;
    }

    @Override
    public void onCancel() {
    }
}
