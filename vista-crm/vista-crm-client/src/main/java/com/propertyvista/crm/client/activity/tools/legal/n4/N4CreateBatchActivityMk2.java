/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.legal.n4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.tools.common.DeferredProcessController;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorState;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel.MultiSelectorCellModelFactory;
import com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;

public class N4CreateBatchActivityMk2 extends AbstractActivity implements N4CreateBatchView.Presenter {

    private final N4CreateBatchService service;

    private final N4CreateBatchView view;

    private final ListDataProvider<LegalNoticeCandidateDTO> searchResultsProvider;

    private final DeferredProcessController deferredProcessContoller;

    protected Vector<LegalNoticeCandidateDTO> items;

    private MultiSelectionModel<LegalNoticeCandidateDTO> selectionModel;

    private MultiSelectorCellModelFactory selectionStatesModelFactory;

    public N4CreateBatchActivityMk2(CrudAppPlace crudPlace) {
        service = GWT.<N4CreateBatchService> create(N4CreateBatchService.class);
        view = CrmSite.getViewFactory().getView(N4CreateBatchView.class);
        selectionStatesModelFactory = new MultiSelectorCellModelFactory(new ArrayList<>());

        selectionModel = new MultiSelectionModel<LegalNoticeCandidateDTO>(new ProvidesKey<LegalNoticeCandidateDTO>() {
            @Override
            public Object getKey(LegalNoticeCandidateDTO item) {
                return item.leaseId().getPrimaryKey();
            }
        });
        searchResultsProvider = new ListDataProvider<LegalNoticeCandidateDTO>(new LinkedList<LegalNoticeCandidateDTO>(), this);
        deferredProcessContoller = new DeferredProcessController();
    }

    //@formatter:off
    // THE Following stuff is not used
    @Override public AppPlace getPlace() { return null; }  
    @Override public void populate() {}
    @Override public void refresh() {}
    //@formatter:on

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        searchResultsProvider.addDataDisplay(view.searchResults());
        ((AbstractHasData<LegalNoticeCandidateDTO>) view.searchResults()).setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<LegalNoticeCandidateDTO> createCheckboxManager(0));
        panel.setWidget(view);
    }

    @Override
    public Object getKey(LegalNoticeCandidateDTO item) {
        return item.leaseId().getPrimaryKey();
    }

    @Override
    public void search() {
        searchResultsProvider.getList().clear();
        searchResultsProvider.flush();
        service.searchForItems(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredProcessCorellationId) {
                N4CreateBatchActivityMk2.this.deferredProcessContoller.startCheckingProgress(deferredProcessCorellationId, new DeferredProgressListener() {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        N4CreateBatchActivityMk2.this.retrieveFoundCandidates();
                    }

                    @Override
                    public void onDeferredProgress(DeferredProcessProgressResponse result) {
                        N4CreateBatchActivityMk2.this.view.setProgress(result.getProgress(), result.getProgressMaximum(), result.getMessage());
                    }

                    @Override
                    public void onDeferredError(DeferredProcessProgressResponse result) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }, view.getSearchCriteria());
    }

    @Override
    public SelectionPresetModel getSelectionState() {
        if (getSelectionModel() != null && getSelectionModel() instanceof MultiSelectionModel) {
            MultiSelectionModel<LegalNoticeCandidateDTO> selectionModel = (getSelectionModel());
            MultiSelectorState state = selectionModel.getSelectedSet().size() > 0 ? MultiSelectorState.Some : MultiSelectorState.None;
            state = selectionModel.getSelectedSet().size() == searchResultsProvider.getList().size() ? MultiSelectorState.All : state;

            if (state == MultiSelectorState.All) {
                return selectionStatesModelFactory.makeAll();
            } else if (state == MultiSelectorState.Some) {
                return selectionStatesModelFactory.makeSome();
            }
            return selectionStatesModelFactory.makeNone();

        } else {
            return selectionStatesModelFactory.makeNone();
        }
    }

    @Override
    public void updateSelection(SelectionPresetModel value) {
        if (value.getState() == MultiSelectorState.None || value.getState() == MultiSelectorState.Some) {
            selectionModel.clear();
        } else {
            for (LegalNoticeCandidateDTO c : searchResultsProvider.getList()) {
                selectionModel.setSelected(c, false);
            }
        }
    }

    public MultiSelectionModel<LegalNoticeCandidateDTO> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void createBatch() {
        // TODO Auto-generated method stub
    }

    @Override
    public void sortFoundCandidates(String memberPath, boolean isAscending) {
        // TODO Auto-generated method stub
    }

    private void retrieveFoundCandidates() {
        service.getFoundItems(new DefaultAsyncCallback<Vector<LegalNoticeCandidateDTO>>() {
            @Override
            public void onSuccess(Vector<LegalNoticeCandidateDTO> result) {
                searchResultsProvider.setList(result);
            }
        });
    }

}
