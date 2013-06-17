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

import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.client.deferred.DeferredProgressPanel;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.AbstractPane;
import com.pyx4j.site.shared.domain.reports.ExportableReport;
import com.pyx4j.site.shared.domain.reports.HasAdvancedSettings;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class AbstractReport extends AbstractPane implements IReportsView {

    public enum Styles {

        ReportView, SettingsFormPanel, ReportPanel, ReportProgressControlPanel, ReportProgressErrorPanel;
    }

    private static class ReportPrintPalette extends Palette {

    }

    public static class ReportPrintTheme extends Theme {

        public enum Styles implements IStyleName {

            ReportNonPrintable, ReportPrintableOnly

        }

        public ReportPrintTheme() {
            Style style = new Style("*");
            style.addProperty("color", "black");
            style.addProperty("font-size", "12px");
            addStyle(style);

            style = new Style("a, a:link, a:visited, a:hover, a:active");
            style.addProperty("color", "black");
            style.addProperty("text-decoration", "none");
            addStyle(style);

            style = new Style("." + Styles.ReportNonPrintable.name());
            style.addProperty("display", "none");
            addStyle(style);

        }

        @Override
        public final ThemeId getId() {
            return new ClassBasedThemeId(getClass());
        }
    }

    private static String PRINT_THEME;

    static {
        StringBuilder stylesString = new StringBuilder();
        ReportPrintTheme theme = new ReportPrintTheme();
        Palette palette = new ReportPrintPalette();
        for (Style style : theme.getAllStyles()) {
            stylesString.append(style.toString(theme, palette));
        }
        PRINT_THEME = stylesString.toString();
    }

    private static final I18n i18n = I18n.get(AbstractReport.class);

    private IReportsView.Presenter presenter;

    private final FlowPanel viewPanel;

    private final Map<Class<? extends ReportMetadata>, ReportFactory<?>> reportFactoryMap;

    private CEntityForm<ReportMetadata> settingsForm;

    private final SimplePanel settingsFormPanel;

    private final ScrollPanel reportPanel;

    private final ReportSettingsFormControlBar reportSettingsFormControlBar;

    private Report report;

    private String settingsId;

    private Button exportButton;

    private final FlowPanel reportProgressControlPanel;

    private Button abortReportGenerationButton;

    private final SimplePanel reportProgressHolderPanel;

    private DeferredProgressPanel progressPanel;

    private final FlowPanel errorPanel;

    public AbstractReport(Map<Class<? extends ReportMetadata>, ReportFactory<?>> reportFactoryMap) {
        setSize("100%", "100%");
        this.reportFactoryMap = reportFactoryMap;
        this.settingsForm = null;
        this.presenter = null;

        viewPanel = new FlowPanel();
        viewPanel.setStyleName(Styles.ReportView.name());
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

        reportProgressControlPanel = new FlowPanel();
        reportProgressControlPanel.setStyleName(AbstractReport.Styles.ReportProgressControlPanel.name());
        reportProgressHolderPanel = new SimplePanel();
        reportProgressControlPanel.add(reportProgressHolderPanel);

        reportProgressControlPanel.add(abortReportGenerationButton = new Button(i18n.tr("Abort"), new Command() {
            @Override
            public void execute() {
                progressPanel.cancelProgress();
                unlockReportSettings();
            }
        }));
        reportProgressControlPanel.setVisible(false);
        viewPanel.add(reportProgressControlPanel);

        errorPanel = new FlowPanel();
        errorPanel.setStyleName(AbstractReport.Styles.ReportProgressErrorPanel.name());
        errorPanel.setVisible(false);
        viewPanel.add(errorPanel);

        reportPanel = new ScrollPanel();
        reportPanel.setSize("100%", "100%");
        reportPanel.setStylePrimaryName(Styles.ReportPanel.name());
        viewPanel.add(reportPanel);

        addHeaderToolbarItem(new Button(i18n.tr("Refresh")));

        addHeaderToolbarItem(new Button(i18n.tr("Load..."), new Command() {
            @Override
            public void execute() {
                presenter.populateAvailableReportSettings();
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Save As..."), new Command() {
            @Override
            public void execute() {
                onSaveSettingsAsClicked();
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                onSaveSettingsClicked();
            }
        }));

        addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                print();
            }
        }));

        addHeaderToolbarItem(exportButton = new Button(i18n.tr("Export"), new Command() {
            @Override
            public void execute() {
                presenter.export(settingsForm.getValue());
            }
        }));

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

            exportButton.setVisible(reportSettings instanceof ExportableReport);
        }

        resetCaption();
        errorPanel.setVisible(false);
    }

    @Override
    public void setReportData(Object data) {
        unlockReportSettings();
        errorPanel.setVisible(false);

        if (report != null) {
            report.setData(data);
        }
    }

    @Override
    public void setError(String errorMessage) {
        unlockReportSettings();
        errorPanel.clear();
        if (errorMessage != null) {
            errorPanel.setVisible(true);
            errorPanel.add(new Label(errorMessage));
        } else {
            errorPanel.setVisible(false);
        }
    }

    @Override
    public void startReportGenerationProgress(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener) {
        settingsForm.setEnabled(false);
        reportSettingsFormControlBar.setEnabled(false);
        reportPanel.setVisible(false);
        errorPanel.setVisible(false);

        progressPanel = new DeferredProgressPanel("600px", "30px", false, deferredProgressListener);
        progressPanel.startProgress(deferredProgressCorelationId);
        reportProgressHolderPanel.setWidget(progressPanel);
        reportProgressControlPanel.setVisible(true);
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

    public void print() {
        StringBuilder printableHtml = new StringBuilder();
        printableHtml.append("<html>");
        printableHtml.append("<head>");
        printableHtml.append("<style type=\"text/css\">");
        printableHtml.append(PRINT_THEME);
        printableHtml.append("</style>");
        printableHtml.append("</head>");
        printableHtml.append("<body>");
        printableHtml.append(DOM.clone(reportPanel.getElement(), true).getInnerHTML());
        printableHtml.append("</body>");
        printableHtml.append("</html>");

        Print.preview(printableHtml.toString());
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
        settingsForm.setEnabled(true);
        reportSettingsFormControlBar.setEnabled(true);
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

    private void unlockReportSettings() {
        settingsForm.setEnabled(true);
        reportSettingsFormControlBar.setEnabled(true);
        reportPanel.setVisible(true);

        reportProgressHolderPanel.setWidget(null);
        reportProgressControlPanel.setVisible(false);
        progressPanel = null;
    }

}
