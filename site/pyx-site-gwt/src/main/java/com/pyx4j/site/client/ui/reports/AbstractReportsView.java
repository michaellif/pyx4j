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
package com.pyx4j.site.client.ui.reports;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.site.shared.domain.reports.HasAdvancedSettings;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class AbstractReportsView extends ViewImplBase implements IReportsView {

    public enum Styles {

        SettingsFormPanel, ReportPanel;
    }

    private static final I18n i18n = I18n.get(AbstractReportsView.class);

    private IReportsView.Presenter presenter;

    private final FlowPanel viewPanel;

    private final Map<Class<? extends ReportMetadata>, ReportFactory<?>> reportFactoryMap;

    private CEntityForm<ReportMetadata> settingsForm;

    private final SimplePanel settingsFormPanel;

    private final ScrollPanel reportPanel;

    private final ReportSettingsFormControlBar reportSettingsFormControlBar;

    private Report report;

    private String settingsId;

    private final ReportSettingsManagementPanel reportSettingsManagementPanel;

    public AbstractReportsView(Map<Class<? extends ReportMetadata>, ReportFactory<?>> reportFactoryMap) {
        setSize("100%", "100%");
        this.reportFactoryMap = reportFactoryMap;
        this.settingsForm = null;
        this.presenter = null;

        reportSettingsManagementPanel = new ReportSettingsManagementPanel() {

            @Override
            public void onLoadRequest(String selectedReportSettingsId) {
                presenter.loadSettings(selectedReportSettingsId);
            }

            @Override
            public void onDeleteRequest(String selectedReportSettingsId) {
                presenter.deleteSettings(selectedReportSettingsId);
            }
        };

        viewPanel = new FlowPanel();
        viewPanel.setWidth("100%");
        viewPanel.setHeight("100%");

        settingsFormPanel = new SimplePanel();
        settingsFormPanel.setStylePrimaryName(Styles.SettingsFormPanel.name());
        viewPanel.add(settingsFormPanel);

        reportSettingsFormControlBar = new ReportSettingsFormControlBar() {

            @Override
            public void onApply() {
                if (presenter != null & settingsForm != null) {
                    if (settingsForm.isValid()) {
                        presenter.apply(settingsForm.getValue());
                    } else {
                        settingsForm.setUnconditionalValidationErrorRendering(true);
                    }
                }
            }

            @Override
            public void onSettingsModeToggled(boolean isAdvanced) {
                setSettingsMode(isAdvanced);
            }

        };
        viewPanel.add(reportSettingsFormControlBar);

        reportPanel = new ScrollPanel();
        reportPanel.setSize("100%", "100%");
        reportPanel.setStylePrimaryName(Styles.ReportPanel.name());
        viewPanel.add(reportPanel);

        addHeaderToolbarItem(new Button(i18n.tr("Refresh")));

        addHeaderToolbarItem(new Button(i18n.tr("Load..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onLoadSettingsClicked();
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Save As..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSaveSettingsAsClicked();
            }
        }));
        addHeaderToolbarItem(new Button(i18n.tr("Save"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onSaveSettingsClicked();
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Print")));
        addHeaderToolbarItem(new Button(i18n.tr("Export")));

        resetCaption();
        setContentPane(new ScrollPanel(viewPanel));
    }

    @Override
    public void setPresenter(IReportsView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReportSettings(ReportMetadata reportSettings, String settingsId) {
        hideVisor();
        this.settingsId = settingsId;
        this.settingsForm = null;
        this.settingsFormPanel.setWidget(null);

        if (reportSettings == null) {
            reportPanel.setWidget(null);
        } else {

            populateSettingsForm(reportSettings);

            ReportFactory<?> factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
            report = factory.getReport();
            reportPanel.setWidget(report);
        }
        resetCaption();
    }

    @Override
    public void setReportData(Object data) {
        if (report != null) {
            report.setData(data);
        }
    }

    @Override
    public void setAvailableReportSettings(List<String> reportSettingsIds) {
        reportSettingsManagementPanel.setAvailableReportSettingsIds(reportSettingsIds);
    }

    @Override
    public void onReportSettingsSaveFailed(String reason) {
        MessageDialog.error(i18n.tr("Save Failed"), reason);
    }

    @Override
    public void onReportSettingsSaveSucceed(String reportSettingsId) {
        MessageDialog.info(i18n.tr("Report settings were saved successfuly!"));
        settingsId = reportSettingsId;
        resetCaption();
    }

    private void setSettingsMode(boolean isAdvanced) {
        if (settingsForm != null) {
            ((HasAdvancedSettings) settingsForm.getValue()).isInAdvancedMode().setValue(isAdvanced);
            populateSettingsForm(settingsForm.getValue());
        }
    }

    private void populateSettingsForm(ReportMetadata reportSettings) {
        ReportFactory<?> factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
        if (factory == null) {
            throw new Error("factory not found for report: " + reportSettings.getInstanceValueClass().getName());
        } else if (factory instanceof HasAdvancedModeReportFactory) {
            boolean isAdvancedMode = ((HasAdvancedSettings) reportSettings).isInAdvancedMode().isBooleanTrue();
            settingsForm = isAdvancedMode ? ((HasAdvancedModeReportFactory) factory).getAdvancedReportSettingsForm() : factory.getReportSettingsForm();
            reportSettingsFormControlBar.enableSettingsModeToggle(isAdvancedMode);
        } else {
            settingsForm = (CEntityForm<ReportMetadata>) factory.getReportSettingsForm();
            reportSettingsFormControlBar.disableModeToggle();
        }
        settingsFormPanel.setWidget(settingsForm);
        settingsForm.populate(reportSettings);
    }

    private void onLoadSettingsClicked() {
        reportSettingsManagementPanel.setAvailableReportSettingsIds(null);

        showVisor(reportSettingsManagementPanel, i18n.tr("Load report configuration preset"));

        presenter.populateAvailableReportSettings();

    }

    private void onSaveSettingsAsClicked() {
        new OkCancelDialog(i18n.tr("Save current report settings as:")) {
            private TextBox settingsId;

            {
                setBody(settingsId = new TextBox());
            }

            @Override
            public boolean onClickOk() {
                if (!settingsId.getText().isEmpty()) {
                    presenter.saveSettings(settingsForm.getValue(), settingsId.getText(), false);
                    return true;
                } else {
                    return false;
                }
            }
        }.show();
    }

    private void onSaveSettingsClicked() {
        if (settingsId == null) {
            onSaveSettingsAsClicked();
        } else {
            presenter.saveSettings(settingsForm.getValue(), settingsId, true);
        }
    }

    private void resetCaption() {
        ReportMetadata reportSettings = settingsForm != null ? settingsForm.getValue() : null;
        if (reportSettings == null) {
            setCaption(i18n.tr("Reports"));
        } else {
            setCaption(SimpleMessageFormat.format("{0} - {1}", reportSettings.getEntityMeta().getCaption(), settingsId == null ? i18n.tr("Untitled")
                    : settingsId));
        }
    }
}
