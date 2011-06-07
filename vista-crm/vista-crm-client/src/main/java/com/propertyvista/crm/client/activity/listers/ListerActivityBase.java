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

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

import com.propertyvista.crm.client.ui.listers.FilterData;
import com.propertyvista.crm.client.ui.listers.IListerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;

public class ListerActivityBase<E extends IEntity> extends AbstractActivity implements IListerView.Presenter {

    private final IListerView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    @Inject
    public ListerActivityBase(IListerView<E> view, AbstractCrudService<E> service, Class<E> entityClass) {
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;
        view.setPresenter(this);
    }

    public ListerActivityBase<E> withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populateData(0);
    }

    @Override
    public void populateData(final int pageNumber) {
        EntitySearchCriteria<E> criteria = new EntitySearchCriteria<E>(entityClass);
        criteria.setPageSize(view.getPageSize());
        criteria.setPageNumber(pageNumber);

        service.search(new AsyncCallback<EntitySearchResult<E>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(EntitySearchResult<E> result) {
                view.populateData(result.getData(), pageNumber, result.hasMoreData());
            }
        }, criteria);
    }

    @Override
    public void applyFiletering(List<FilterData> filters) {
        EntityQueryCriteria<E> criteria = new EntityQueryCriteria<E>(entityClass);
        for (FilterData fd : filters) {
            switch (fd.getOperand()) {
            case is:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.EQUAL, fd.getValue()));
                break;
            case isNot:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.NOT_EQUAL, fd.getValue()));
                break;
            case contains:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.IN, fd.getValue()));
                break;
            case greaterThen:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.GREATER_THAN, fd.getValue()));
                break;
            case lessThen:
                criteria.add(new PropertyCriterion(fd.getMemberPath(), Restriction.LESS_THAN, fd.getValue()));
                break;
            }
        }

        // TODO (VladS/L) - search using formed criteria - not implemented in service still...
    }
}
