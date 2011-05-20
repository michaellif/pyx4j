/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.listers;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.client.ui.listers.IListerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;

public class ListerActivityBase<B extends IEntity, E extends B> extends AbstractActivity implements IListerView.Presenter {

    private final IListerView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<B> entityClass;

    @Inject
    public ListerActivityBase(IListerView<E> view, AbstractCrudService<E> service, Class<B> entityClass) {
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    public ListerActivityBase<B, E> withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populateData(0);
    }

    @Override
    public void populateData(final int pageNumber) {
        @SuppressWarnings("unchecked")
        EntitySearchCriteria<E> criteria = (EntitySearchCriteria<E>) new EntitySearchCriteria<B>(entityClass);
        criteria.setPageSize(view.getPageSize());
        criteria.setPageNumber(pageNumber);

        service.search(new AsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                view.populateData(result.getData(), pageNumber, result.hasMoreData());
            }
        }, criteria);
    }
}
