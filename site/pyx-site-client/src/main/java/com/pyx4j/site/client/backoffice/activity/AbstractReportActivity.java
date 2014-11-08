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
package com.pyx4j.site.client.backoffice.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePane;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReport;
import com.pyx4j.site.client.backoffice.ui.prime.report.ReportSettingsManagementVizor;
import com.pyx4j.site.client.memento.MementoManager;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;
import com.pyx4j.site.rpc.customization.ICustomizationPersistenceService;
import com.pyx4j.site.rpc.reports.IReportsService;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

public abstract class AbstractReportActivity<R extends ReportTemplate> extends AbstractActivity implements IReport.Presenter<R> {

    public class ReportSettingsManagementVizorController extends AbstractVisorController {

        private final ReportSettingsManagementVizor visor;

        public ReportSettingsManagementVizorController(IPrimePane parentView, final IReport.Presenter<R> presenter) {
            super(parentView);
            visor = new ReportSettingsManagementVizor(this) {

                @Override
                public void onLoadRequest(String selectedReportSettingsId) {
                    presenter.loadReportMetadata(selectedReportSettingsId);
                }

                @Override
                public void onDeleteRequest(String selectedReportSettingsId) {
                    presenter.deleteReportMetadata(selectedReportSettingsId);
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

        ReportTemplate reportMetadata;

        Object data;
    }

    private static final I18n i18n = I18n.get(AbstractReportActivity.class);

    private static final Map<String, CachedReportData> reportDataCache = new HashMap<String, AbstractReportActivity.CachedReportData>();

    protected final ReportsAppPlace<R> place;

    private final IReport<R> view;

    private final IReportsService<R> reportsService;

    private final ICustomizationPersistenceService<ReportTemplate> reportsSettingsPersistenceService;

    private ReportSettingsManagementVizorController reportSettingsManagementVizorController;

    private final String downloadServletPath;

    private final DeferredProcessService deferredProccessService;

    private Timer progressTimer;

    private final Class<R> reportMetadataClass;

    public AbstractReportActivity(Class<R> reportMetadataClass, ReportsAppPlace<R> place, IReportsService<R> reportsService,
            ICustomizationPersistenceService<ReportTemplate> reportsSettingsPersistenceService, IReport<R> view, String dowloadServletPath) {
        this.reportMetadataClass = reportMetadataClass;
        this.reportsService = reportsService;
        this.reportsSettingsPersistenceService = reportsSettingsPersistenceService;
        this.deferredProccessService = GWT.<DeferredProcessService> create(DeferredProcessService.class);

        this.view = view;
        this.place = place;

        this.view.setPresenter(this);
        this.downloadServletPath = dowloadServletPath;
    }

    public IReport<R> getView() {
        return view;
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
        if (place.getReportMetadata() == null) {
            if (place.getReportMetadataId() != null) {
                loadReportMetadata(place.getReportMetadataId());
                return;
            } else {
                place.define(createDefaultReportMetadata());
            }
        }
        MementoManager.restoreState(getView(), place);
        onReportMetadataSet((R) place.getReportMetadata());
    }

    @Override
    public void runReportGeneration() {
        reportsService.generateReportAsync(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredProcessId) {
                view.startReportGenerationProgress(deferredProcessId, new DeferredProgressListener() {

                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
                            public void execute() {
                                reportsService.getReport(new DefaultAsyncCallback<Serializable>() {

                                    @Override
                                    public void onSuccess(Serializable result) {
                                        CachedReportData cachedReportData = new CachedReportData();
                                        cachedReportData.data = result;
                                        cachedReportData.reportMetadata = view.getReportSettings().duplicate();
                                        reportDataCache.put(GWTJava5Helper.getSimpleName(view.getReportSettings().getInstanceValueClass()), cachedReportData);
                                        view.setReportData(result);
                                    }

                                });
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
        }, view.getReportSettings());
    }

    @Override
    public void abortReportGeneration() {
//TODO implement
    }

    @Override
    public void export() {
        ReportDialog d = new ReportDialog(i18n.tr("Exporting Report"), "");
        d.setDownloadServletPath(downloadServletPath);

        ReportRequest request = new ReportRequest();

        final String METADATA_KEY = "METADATA";

        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put(METADATA_KEY, view.getReportSettings());

        request.setParameters(parameters);
        d.start(new ReportService<IEntity>() {

            @Override
            public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
                reportsService.export(callback, (R) reportRequest.getParameters().get(METADATA_KEY));
            }

            @Override
            public void cancelDownload(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
                reportsService.cancelExportedReport(callback, downloadUrl);
            }

        }, request);
    }

    @Override
    public void loadReportMetadata(final String reportMetadataId) {
        reportsSettingsPersistenceService.load(new DefaultAsyncCallback<ReportTemplate>() {

            @Override
            public void onSuccess(ReportTemplate reportMetadata) {
                place.define((R) reportMetadata);
                view.setReportMetadata((R) reportMetadata);
                onReportMetadataSet((R) reportMetadata);
            }

        }, reportMetadataId, EntityFactory.getEntityPrototype(reportMetadataClass));
    }

    @Override
    public void saveReportMetadata() {
        saveReportMetadata(true);
    }

    @Override
    public void saveAsReportMetadata() {
        saveReportMetadata(false);
    }

    private void saveReportMetadata(boolean allowOverwrite) {
        reportsSettingsPersistenceService.save(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                view.onReportMetadataSaveSucceed();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof CustomizationOverwriteAttemptException) {
                    view.onReportMetadataSaveFailed(i18n.tr("Please choose a different name: a report settings preset named \"{0}\" already exists", view
                            .getReportSettings().reportTemplateName().getValue()));
                } else if (caught instanceof UserRuntimeException) {
                    view.onReportMetadataSaveFailed(((UserRuntimeException) caught).getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, view.getReportSettings().reportTemplateName().getValue(), view.getReportSettings(), allowOverwrite);

    }

    @Override
    public void deleteReportMetadata(String settings) {

        reportsSettingsPersistenceService.delete(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                loadAvailableTemplates();
            }

        }, settings, EntityFactory.getEntityPrototype(reportMetadataClass));
    }

    @Override
    public void loadAvailableTemplates() {
        reportsSettingsPersistenceService.list(new DefaultAsyncCallback<Vector<String>>() {

            @Override
            public void onSuccess(Vector<String> result) {
                getReportSettingsManagementVizorController().setAvailableReportSettingsIds(result);
                getReportSettingsManagementVizorController().show();
            }

        }, EntityFactory.getEntityPrototype(reportMetadataClass));

    }

    public void onDiscard() {
        MementoManager.saveState(getView(), place);
        getView().reset();
    }

    @Override
    public void onCancel() {
        onDiscard();
    }

    @Override
    public void onStop() {
        onDiscard();
    }

    @Override
    public void populate() {
    }

    @Override
    public void refresh() {
    }

    protected void onReportMetadataSet(R reportMetadata) {

    }

    protected R createDefaultReportMetadata() {
        return EntityFactory.create(reportMetadataClass);
    }

}
