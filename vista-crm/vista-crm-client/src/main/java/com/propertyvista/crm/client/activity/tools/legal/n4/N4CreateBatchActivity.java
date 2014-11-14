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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.ui.visor.IVisorEditor;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.tools.common.DeferredProcessController;
import com.propertyvista.crm.client.ui.tools.common.datagrid.MultiSelectorState;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel;
import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel.MultiSelectorCellModelFactory;
import com.propertyvista.crm.client.ui.tools.legal.n4.N4CreateBatchView;
import com.propertyvista.crm.client.ui.tools.legal.n4.visors.N4BatchSettingsVisor;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4GenerationDefaultParamsDTO;
import com.propertyvista.crm.rpc.services.legal.N4CreateBatchService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class N4CreateBatchActivity extends AbstractActivity implements N4CreateBatchView.Presenter {

    private static final I18n i18n = I18n.get(N4CreateBatchActivity.class);

    private final N4CreateBatchService service;

    private final N4CreateBatchView view;

    private final ListDataProvider<LegalNoticeCandidateDTO> searchResultsProvider;

    private final DeferredProcessController deferredProcessContoller;

    protected Vector<LegalNoticeCandidateDTO> items;

    private MultiSelectionModel<LegalNoticeCandidateDTO> selectionModel;

    private MultiSelectorCellModelFactory selectionStatesModelFactory;

    private N4BatchRequestDTO batchRequest;

    private IList<Employee> agents;

    public N4CreateBatchActivity(CrudAppPlace crudPlace) {
        service = GWT.<N4CreateBatchService> create(N4CreateBatchService.class);
        view = CrmSite.getViewFactory().getView(N4CreateBatchView.class);
        selectionStatesModelFactory = new MultiSelectorCellModelFactory(new ArrayList<>());

        selectionModel = new MultiSelectionModel<LegalNoticeCandidateDTO>(new ProvidesKey<LegalNoticeCandidateDTO>() {
            @Override
            public Object getKey(LegalNoticeCandidateDTO item) {
                return item.leaseId().getPrimaryKey();
            }
        });
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                view.setCreateBatchEnabled(!selectionModel.getSelectedSet().isEmpty());
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
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        initSettings(new DefaultAsyncCallback<N4CandidateSearchCriteriaDTO>() {
            @Override
            public void onSuccess(N4CandidateSearchCriteriaDTO result) {
                view.setPresenter(N4CreateBatchActivity.this);
                view.setSearchEnabled(true);
                view.setCreateBatchEnabled(false);
                searchResultsProvider.addDataDisplay(view.searchResults());
                ((AbstractHasData<LegalNoticeCandidateDTO>) view.searchResults()).setSelectionModel(selectionModel,
                        DefaultSelectionEventManager.<LegalNoticeCandidateDTO> createCheckboxManager(0));
                panel.setWidget(view);

            }
        });
    }

    @Override
    public Object getKey(LegalNoticeCandidateDTO item) {
        return item.leaseId().getPrimaryKey();
    }

    @Override
    public void search() {
        view.setSearchEnabled(false);
        view.setProgress(0, 0, i18n.tr("Starting search..."));

        searchResultsProvider.getList().clear();
        searchResultsProvider.flush();

        selectionModel.clear();

        service.searchForItems(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredProcessCorellationId) {
                deferredProcessContoller.startCheckingProgress(deferredProcessCorellationId, new DeferredProgressListener() {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        retrieveFoundCandidates();
                    }

                    @Override
                    public void onDeferredProgress(DeferredProcessProgressResponse result) {
                        view.setProgress(result.getProgress(), result.getProgressMaximum(), result.getMessage());
                    }

                    @Override
                    public void onDeferredError(DeferredProcessProgressResponse result) {
                        view.displayMessage(result.getMessage(), Type.Error);
                        view.setSearchEnabled(true);
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
                selectionModel.setSelected(c, true);
            }
        }
    }

    public MultiSelectionModel<LegalNoticeCandidateDTO> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void createBatch() {
        IVisorEditor.Controller visorController = new IVisorEditor.Controller() {//@formatter:off
            private N4BatchSettingsVisor visor;
            { visor = new N4BatchSettingsVisor(this); }

            @Override public void show() {
                visor.populate(batchRequest);
                visor.setAgents(agents);
                view.showVisor(visor);
            }

            @Override public void hide() { view.hideVisor(); }

            @Override public void save() { apply(); hide(); }

            @Override
            public void apply() { acceptSelected(); }

        };//@formatter:on
        visorController.show();
    }

    @Override
    public void sortFoundCandidates(String memberPath, boolean isAscending) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancelDownload(String reportUrl) {
        GWT.<DownloadableService> create(DownloadableService.class).cancelDownload(null, reportUrl);
    }

    private void acceptSelected() {
        N4BatchRequestDTO batchRequest = this.batchRequest.duplicate(N4BatchRequestDTO.class);
        for (LegalNoticeCandidateDTO noticeCandidate : selectionModel.getSelectedSet()) {
            batchRequest.targetDelinquentLeases().add(noticeCandidate.leaseId().<Lease> duplicate());
        }
        service.process(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredProcessCorelationId) {
                startProgressMonitoring(deferredProcessCorelationId);
            }
        }, batchRequest);
    }

    private void initSettings(final AsyncCallback<N4CandidateSearchCriteriaDTO> callback) {
        (GWT.<N4CreateBatchService> create(N4CreateBatchService.class)).initSettings(new DefaultAsyncCallback<N4GenerationDefaultParamsDTO>() {
            @Override
            public void onSuccess(N4GenerationDefaultParamsDTO result) {
                batchRequest = result.batchRequest().duplicate();
                agents = result.availableAgents();
                callback.onSuccess(EntityFactory.create(N4CandidateSearchCriteriaDTO.class));
            }
        });
    }

    private void retrieveFoundCandidates() {
        service.getFoundItems(new DefaultAsyncCallback<Vector<LegalNoticeCandidateDTO>>() {
            @Override
            public void onSuccess(Vector<LegalNoticeCandidateDTO> result) {
                selectionModel.clear();
                searchResultsProvider.setList(result);
                view.setSearchEnabled(true);
            }
        });
    }

    private void startProgressMonitoring(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog("", i18n.tr("Processing..."), false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
                onEndProgress(result);
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

    private void onEndProgress(DeferredProcessProgressResponse result) {
        DeferredReportProcessProgressResponse reportProgress = (DeferredReportProcessProgressResponse) result;
        if (reportProgress.getDownloadLink() == null) {
            search();
        } else {
            String downloadUrl = GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping + "/" + reportProgress.getDownloadLink();
            view.displayN4GenerationReportDownloadLink(downloadUrl);
        }
    }

}
