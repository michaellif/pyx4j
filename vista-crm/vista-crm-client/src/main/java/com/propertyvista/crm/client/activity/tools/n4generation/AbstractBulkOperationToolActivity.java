/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.n4generation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.tools.n4generation.base.BulkOperationToolView;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;
import com.propertyvista.crm.rpc.services.legal.AbstractBulkOperationService;

public abstract class AbstractBulkOperationToolActivity<Settings extends IEntity, Item extends BulkEditableEntity, AcceptedItems> extends AbstractActivity
        implements BulkOperationToolView.Presenter {

    private static final I18n i18n = I18n.get(AbstractBulkOperationToolActivity.class);

    private final BulkOperationToolView<Settings, Item> view;

    private List<Item> items;

    private final AbstractBulkOperationService<Settings, Item, AcceptedItems> service;

    private final AppPlace place;

    private final Class<Settings> settingsClass;

    public AbstractBulkOperationToolActivity(AppPlace place, BulkOperationToolView<Settings, Item> view,
            AbstractBulkOperationService<Settings, Item, AcceptedItems> service, Class<Settings> settingsClass) {
        this.place = place;
        this.view = view;
        this.service = service;
        this.items = new LinkedList<Item>();
        this.settingsClass = settingsClass;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(getView());
        getView().setPresenter(this);
        initSettings(new DefaultAsyncCallback<Settings>() {
            @Override
            public void onSuccess(Settings result) {
                initView(result);
            }
        });
    }

    @Override
    public void acceptSelected() {
        if (!items.isEmpty() && (getView().isEverythingSelected() || !getView().getSelectedItems().isEmpty())) {
            service.process(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String deferredCorrelationId) {
                    startAccetanceProgress(deferredCorrelationId);
                }
            }, makeProducedItems(getView().isEverythingSelected() ? items : getView().getSelectedItems()));
        } else {
            getView().showMessage(i18n.tr("Please select some items first"));
        }
    }

    @Override
    public void search() {
        getView().setLoading(true);
        service.getItems(new DefaultAsyncCallback<Vector<Item>>() {
            @Override
            public void onSuccess(Vector<Item> items) {
                AbstractBulkOperationToolActivity.this.items = items;
                AbstractBulkOperationToolActivity.this.getView().resetVisibleRange();
                AbstractBulkOperationToolActivity.this.populateItems();
                AbstractBulkOperationToolActivity.this.getView().setLoading(false);
            }
        }, getView().getSettings().<Settings> duplicate());
    }

    public BulkOperationToolView<Settings, Item> getView() {
        return view;
    }

    @Override
    public void populate() {
        // no need to implement
    }

    @Override
    public void refresh() {
        // no need to implement
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void updateVisibleItems() {
        getView().setLoading(true);
        populateItems();
        getView().setLoading(false);
    }

    protected abstract AcceptedItems makeProducedItems(List<Item> list);

    protected abstract void onSelectedProccessSuccess(DeferredProcessProgressResponse result);

    protected void initSettings(AsyncCallback<Settings> callback) {
        callback.onSuccess(EntityFactory.create(settingsClass));
    }

    protected void initView(Settings settings) {
        getView().setSettings(settings);
        getView().setRowData(0, 0, Collections.<Item> emptyList());
        getView().setLoading(false);
        getView().setBulkOperationEnabled(true);
        getView().setSearchEnabled(true);
    }

    private void populateItems() {
        int start = getView().getVisibleRange().getStart();
        int end = Math.min(items.size(), start + getView().getVisibleRange().getLength());

        getView().setRowData(getView().getVisibleRange().getStart(), items.size(), items.subList(start, end));
    }

    private void startAccetanceProgress(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog("", i18n.tr("Processing..."), false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
                AbstractBulkOperationToolActivity.this.onSelectedProccessSuccess(result);
                search();
            }

            @Override
            protected void onDeferredCompleate() {
                super.onDeferredCompleate();
                this.hide();
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }

}
