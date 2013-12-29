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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils;
import com.propertyvista.crm.client.ui.gadgets.util.ListerUtils.ItemSelectCommand;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public abstract class AbstractListerDetailsFactory<E extends IEntity, F extends Serializable> implements CounterDetailsFactory {

    private final SimplePanel panel;

    private final EntityDataTablePanel<E> lister;

    private final ICriteriaProvider<E, F> criteriaProvider;

    private final IFilterDataProvider<F> filterDataProvider;

    private final Class<E> dataClass;

    private final AbstractListService<E> listerService;

    private final Proxy<ListerUserSettings> listerSettingsProxy;

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
    public AbstractListerDetailsFactory(Class<E> dataClass, List<ColumnDescriptor> defaultColumnDescriptors, AbstractListService<E> listerService,
            IFilterDataProvider<F> filterDataProvider, ICriteriaProvider<E, F> criteriaProvider, Proxy<ListerUserSettings> listerSettingsProxy) {
        this.panel = new SimplePanel();
        this.lister = new EntityDataTablePanel<E>(dataClass);
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
        if (listerSettingsProxy == null) {
            this.listerSettingsProxy = new Proxy<ListerUserSettings>() {
                private final ListerUserSettings settings = EntityFactory.create(ListerUserSettings.class);

                @Override
                public ListerUserSettings get() {
                    return settings;
                }

                @Override
                public void save() {
                }

                @Override
                public boolean isModifiable() {
                    return true;
                }
            };
        } else {
            this.listerSettingsProxy = listerSettingsProxy;
        }

        ListerUtils.bind(lister.getDataTablePanel())//@formatter:off
            .columnDescriptors(defaultColumnDescriptors)            
            .setupable(this.listerSettingsProxy.isModifiable())
            .userSettingsProvider(this.listerSettingsProxy)
            .onColumnSelectionChanged(new Command() {
                @Override
                public void execute() {
                    AbstractListerDetailsFactory.this.listerSettingsProxy.save();
                }
            })
            .onItemSelectedCommand(new ItemSelectCommand<E>() {                
                @Override
                public void execute(E item) {
                    AbstractListerDetailsFactory.this.onItemSelected(item);
                                       
                }
            })
            .init();//@formatter:on
    }

    @Override
    public Widget createDetailsWidget() {
        populateLister();
        return panel;
    }

    protected void onItemSelected(E item) {
        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(dataClass).formViewerPlace(item.getPrimaryKey()));
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
