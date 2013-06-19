/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.reports.IReportsView;
import com.pyx4j.site.client.ui.reports.ReportSettingsManagementVizor;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;
import com.pyx4j.site.rpc.customization.ICustomizationPersistenceService;
import com.pyx4j.site.rpc.reports.IReportsService;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

public abstract class AbstractReportActivity extends AbstractActivity implements IReportsView.Presenter {

    private static final I18n i18n = I18n.get(AbstractReportActivity.class);

    public static class ReportSettingsManagementVizorController extends AbstractVisorController {

        private final ReportSettingsManagementVizor visor;

        public ReportSettingsManagementVizorController(IPane parentView, final IReportsView.Presenter presenter) {
            super(parentView);
            visor = new ReportSettingsManagementVizor(this) {

                @Override
                public void onLoadRequest(String selectedReportSettingsId) {
                    presenter.loadSettings(selectedReportSettingsId);
                }

                @Override
                public void onDeleteRequest(String selectedReportSettingsId) {
                    presenter.deleteSettings(selectedReportSettingsId);
                }
            };
            visor.setAvailableReportSettingsIds(null);

        }

        @Override
        public void show() {
            getParentView().showVisor(visor);
        }

        public void setAvailableReportSettingsIds(List<String> reportSettingsIds) {
            visor.setAvailableReportSettingsIds(reportSettingsIds);
        }

    }

    private static class CachedReportData {

        ReportMetadata reportMetadata;

        Object data;
    }

    private static final Map<String, CachedReportData> reportDataCache = new HashMap<String, AbstractReportActivity.CachedReportData>();

    protected final IReportsView view;

    private final IReportsService reportsService;

    private final ReportsAppPlace place;

    private final ICustomizationPersistenceService<ReportMetadata> reportsSettingsPersistenceService;

    private ReportSettingsManagementVizorController reportSettingsManagementVizorController;

    private final String downloadServletPath;

    private final DeferredProcessService deferredProccessService;

    protected Timer progressTimer;

    public AbstractReportActivity(IReportsService reportsService, ICustomizationPersistenceService<ReportMetadata> reportsSettingsPersistenceService,
            IReportsView view, ReportsAppPlace place, String dowloadServletPath) {
        this.reportsService = reportsService;
        this.reportsSettingsPersistenceService = reportsSettingsPersistenceService;

        this.view = view;
        if (place.getReportMetadata() == null) {
            for (Class<? extends ReportMetadata> reportMetadataClass : view.getSupportedReportMetadata()) {
                if (GWTJava5Helper.getSimpleName(reportMetadataClass).equals(place.getReportMetadataName())) {
                    place = new ReportsAppPlace(EntityFactory.create(reportMetadataClass));
                    break;
                }
            }
            if (place.getReportMetadata() == null) {
                throw new Error("Report '" + place.getReportMetadataName() + "' is not supported!");
            }
        }
        this.place = place;
        this.view.setPresenter(this);
        this.downloadServletPath = dowloadServletPath;
        this.deferredProccessService = GWT.<DeferredProcessService> create(DeferredProcessService.class);
    }

    public ReportSettingsManagementVizorController getReportSettingsManagementVizorController() {
        if (reportSettingsManagementVizorController == null) {
            reportSettingsManagementVizorController = new ReportSettingsManagementVizorController(view, this);
        }
        return reportSettingsManagementVizorController;
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.getMemento().setCurrentPlace(getPlace());
        view.restoreState();
    }

    @Override
    public void apply(final ReportMetadata reportMetadata) {
        CachedReportData cachedData = reportDataCache.get(GWTJava5Helper.getSimpleName(reportMetadata.getInstanceValueClass()));
        if (cachedData != null && EntityGraph.fullyEqualValues(reportMetadata, cachedData.reportMetadata)) {
            view.setReportData(cachedData.data);
        } else {
            reportsService.generateReportAsync(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String deferredProcessId) {
                    view.startReportGenerationProgress(deferredProcessId, new DeferredProgressListener() {

                        @Override
                        public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                            reportsService.getReport(new DefaultAsyncCallback<Serializable>() {

                                @Override
                                public void onSuccess(Serializable result) {
                                    CachedReportData cachedReportData = new CachedReportData();
                                    cachedReportData.data = result;
                                    cachedReportData.reportMetadata = reportMetadata.duplicate();
                                    reportDataCache.put(GWTJava5Helper.getSimpleName(reportMetadata.getInstanceValueClass()), cachedReportData);
                                    view.setReportData(result);
                                }

                            });
                        }

                        @Override
                        public void onDeferredProgress(DeferredProcessProgressResponse result) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onDeferredError(DeferredProcessProgressResponse result) {
                            view.setError(result.getMessage());
                        }
                    });
                }
            }, reportMetadata);
        }
    }

    @Override
    public void export(ReportMetadata settings) {
        ReportDialog d = new ReportDialog(i18n.tr("Exporting Report"), "");
        d.setDownloadServletPath(downloadServletPath);
        ReportRequest request = new ReportRequest();
        final String METADATA_KEY = "METADATA";
        HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(METADATA_KEY, settings);
        request.setParameters(parameters);
        d.start(new ReportService<IEntity>() {

            @Override
            public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
                reportsService.export(callback, (ReportMetadata) reportRequest.getParameters().get(METADATA_KEY));
            }

            @Override
            public void cancelDownload(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
                reportsService.cancelExportedReport(callback, downloadUrl);
            }

        }, request);
    }

    @Override
    public void refresh(ReportMetadata reportMetadata) {
        if (reportMetadata != null) {
            reportDataCache.remove(GWTJava5Helper.getSimpleName(reportMetadata.getInstanceValueClass()));
        }
        apply(reportMetadata);
    }

    @Override
    public void loadSettings(final String id) {
        reportsSettingsPersistenceService.load(new DefaultAsyncCallback<ReportMetadata>() {

            @Override
            public void onSuccess(ReportMetadata result) {
                view.setReportSettings(result, id);
            }

        }, id, (ReportMetadata) EntityFactory.getEntityPrototype(retrieveReportSettings(place).getInstanceValueClass()));
    }

    @Override
    public void saveSettings(ReportMetadata settings, final String reportSettingsId, boolean allowOverwrite) {
        reportsSettingsPersistenceService.save(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                view.onReportSettingsSaveSucceed(reportSettingsId);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof CustomizationOverwriteAttemptException) {
                    view.onReportSettingsSaveFailed(i18n.tr("Please choose a different name: a report settings preset named \"{0}\" already exists",
                            reportSettingsId));
                } else if (caught instanceof UserRuntimeException) {
                    view.onReportSettingsSaveFailed(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, reportSettingsId, settings, allowOverwrite);

    }

    @Override
    public void deleteSettings(String settings) {

        reportsSettingsPersistenceService.delete(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populateAvailableReportSettings();
            }

        }, settings, (ReportMetadata) EntityFactory.getEntityPrototype(retrieveReportSettings(place).getInstanceValueClass()));
    }

    @Override
    public void populateAvailableReportSettings() {
        reportsSettingsPersistenceService.list(new DefaultAsyncCallback<Vector<String>>() {

            @Override
            public void onSuccess(Vector<String> result) {
                getReportSettingsManagementVizorController().setAvailableReportSettingsIds(result);
                getReportSettingsManagementVizorController().show();
            }

        }, (ReportMetadata) EntityFactory.getEntityPrototype(retrieveReportSettings(place).getInstanceValueClass()));

    }

    @Override
    public void onStop() {
        super.onStop();
        view.storeState(getPlace());
    }

    protected ReportMetadata retrieveReportSettings(AppPlace place) {
        if (place instanceof ReportsAppPlace) {
            return ((ReportsAppPlace) place).getReportMetadata();
        } else {
            return null;
        }
    }

    @Override
    public void populate() {
    }

    @Override
    public void refresh() {

    }

}
