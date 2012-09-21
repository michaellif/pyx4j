/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components.details;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;

public abstract class AbstractListerDetailsFactory<E extends IEntity, F extends Serializable> implements CounterDetailsFactory {

    public interface ICriteriaProvider<E extends IEntity, F extends Serializable> {

        void makeCriteria(AsyncCallback<EntityListCriteria<E>> callback, F filterData);

    }

    public interface IFilterDataProvider<F extends Serializable> {

        F getFilterData();

        void addFilterDataChangedHandler(IFilterDataChangedHandler<F> handler);

    }

    public interface IFilterDataChangedHandler<F extends Serializable> {

        void handleFilterDataChange(F updatedFilterData);

    }

    private final SimplePanel panel;

    private final BasicLister<E> lister;

    private final ICriteriaProvider<E, F> criteriaProvider;

    private final IFilterDataProvider<F> filterDataProvider;

    private final Class<E> dataClass;

    private final AbstractListService<E> listerService;

    /**
     * 
     * @param dataClass
     * @param lister
     * @param listerService
     * @param filterDataProvider
     *            provides filter data that is to be used to convert it to criteria by the criteria provider
     * @param criteriaProvider
     *            provides filter criteria for the lister
     */
    public AbstractListerDetailsFactory(Class<E> dataClass, BasicLister<E> lister, AbstractListService<E> listerService,
            IFilterDataProvider<F> filterDataProvider, ICriteriaProvider<E, F> criteriaProvider) {
        this.panel = new SimplePanel();
        this.lister = lister;
        this.listerService = listerService;

        this.dataClass = dataClass;
        this.filterDataProvider = filterDataProvider;
        this.filterDataProvider.addFilterDataChangedHandler(new IFilterDataChangedHandler<F>() {
            @Override
            public void handleFilterDataChange(F filterData) {
                populateLister();
            }
        });

        this.criteriaProvider = criteriaProvider;
    }

    @Override
    public Widget createDetailsWidget() {
        populateLister();
        return panel;
    }

    private void populateLister() {
        criteriaProvider.makeCriteria(new AsyncCallback<EntityListCriteria<E>>() {

            @Override
            public void onSuccess(EntityListCriteria<E> result) {
                ListerDataSource<E> listerDataSource = new ListerDataSource<E>(dataClass, listerService);
                List<Criterion> criteria = result.getFilters();
                if (criteria != null) {
                    listerDataSource.setPreDefinedFilters(criteria);
                } else {
                    listerDataSource.clearPreDefinedFilters();
                }
                lister.setDataSource(listerDataSource);
                lister.obtain(0);

                panel.setWidget(lister);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    panel.setWidget(new HTML(((UserRuntimeException) caught).getLocalizedMessage()));
                } else {
                    throw new RuntimeException(caught);
                }
            }

        }, filterDataProvider.getFilterData());
    }

}
