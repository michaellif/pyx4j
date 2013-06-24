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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.shared.domain.reports.ExportableReport;
import com.pyx4j.site.shared.domain.reports.HasAdvancedSettings;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class AbstractReport extends AbstractPrimePane implements IReportsView {

    public enum Styles {

        ReportView, SettingsFormPanel, ReportPanel, ReportProgressControlPanel, ReportProgressErrorPanel;

    }

    private enum MementoKeys {

        ReportMetadata, HasData, HorizontalScrollPosition, VerticalScrollPosition

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

    private final SimplePanel reportPanel;

    private final ReportSettingsFormControlBar reportSettingsFormControlBar;

    private Report report;

    private String settingsId;

    private Button exportButton;

    private final FlowPanel reportProgressControlPanel;

    private Button abortReportGenerationButton;

    private final SimplePanel reportProgressHolderPanel;

    private DeferredProgressPanel progressPanel;

    private final FlowPanel errorPanel;

    private boolean hasData;

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
        settingsFormPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
        settingsFormPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        settingsFormPanel.getElement().getStyle().setLeft(0, Unit.PX);
        settingsFormPanel.getElement().getStyle().setTop(0, Unit.PX);
        settingsFormPanel.getElement().getStyle().setRight(0, Unit.PX);
        settingsFormPanel.getElement().getStyle().setHeight(15, Unit.EM);

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
        reportSettingsFormControlBar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportSettingsFormControlBar.getElement().getStyle().setLeft(0, Unit.PX);
        reportSettingsFormControlBar.getElement().getStyle().setTop(15, Unit.EM);
        reportSettingsFormControlBar.getElement().getStyle().setRight(0, Unit.PX);
        reportSettingsFormControlBar.getElement().getStyle().setHeight(3, Unit.EM);
        viewPanel.add(reportSettingsFormControlBar);

        reportProgressControlPanel = new FlowPanel();
        reportProgressControlPanel.setStyleName(AbstractReport.Styles.ReportProgressControlPanel.name());
        reportProgressControlPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportProgressControlPanel.getElement().getStyle().setLeft(0, Unit.PX);
        reportProgressControlPanel.getElement().getStyle().setTop(18, Unit.EM);
        reportProgressControlPanel.getElement().getStyle().setRight(0, Unit.PX);
        reportProgressControlPanel.getElement().getStyle().setHeight(10, Unit.EM);

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
        errorPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        errorPanel.getElement().getStyle().setLeft(0, Unit.PX);
        errorPanel.getElement().getStyle().setTop(18, Unit.EM);
        errorPanel.getElement().getStyle().setRight(0, Unit.PX);
        errorPanel.getElement().getStyle().setHeight(10, Unit.EM);
        errorPanel.setVisible(false);
        viewPanel.add(errorPanel);

        reportPanel = new SimplePanel();
        // TODO add memento for scroll positions of the report
//        reportPanel.addScrollHandler(new ScrollHandler() {
//            @Override
//            public void onScroll(ScrollEvent event) {
//                getMemento().setCurrentPlace(presenter.getPlace());
//                getMemento().putInteger(((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.HorizontalScrollPosition.name(),
//                        reportPanel.getHorizontalScrollPosition());
//                getMemento().putInteger(((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.VerticalScrollPosition.name(),
//                        reportPanel.getVerticalScrollPosition());
//            }
//        });
        reportPanel.setStylePrimaryName(Styles.ReportPanel.name());
        reportPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportPanel.getElement().getStyle().setLeft(0, Unit.PX);
        reportPanel.getElement().getStyle().setTop(18, Unit.EM);
        reportPanel.getElement().getStyle().setRight(0, Unit.PX);
        reportPanel.getElement().getStyle().setBottom(0, Unit.PX);
        viewPanel.add(reportPanel);

        addHeaderToolbarItem(new Button(i18n.tr("Refresh"), new Command() {
            @Override
            public void execute() {
                presenter.refresh(settingsForm.getValue());
            }
        }));

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
        setContentPane(viewPanel);
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
        this.reportSettingsFormControlBar.setEnabled(false);
        this.reportPanel.setWidget(null);

        if (reportSettings != null) {
            populateSettingsForm(reportSettings);
            reportSettingsFormControlBar.setEnabled(true);

            ReportFactory<?> factory = reportFactoryMap.get(reportSettings.getInstanceValueClass());
            report = factory.getReport();
            report.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
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

        if (report != null & data != null) {
            report.setData(data);
            hasData = true;
        } else {
            hasData = false;
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

    @Override
    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);
        getMemento().putObject(((ReportsAppPlace) place).getReportMetadataName() + MementoKeys.ReportMetadata.name(), settingsForm.getValue());
        getMemento().putObject(((ReportsAppPlace) place).getReportMetadataName() + MementoKeys.HasData.name(), hasData);
        // TODO see reportPanel initialzation and ScollEventHandler
        //getMemento().putInteger(((ReportsAppPlace) place).getReportMetadataName() + MementoKeys.HorizontalScrollPosition.name(),
        //                reportPanel.getHorizontalScrollPosition());
        //getMemento().putInteger(((ReportsAppPlace) place).getReportMetadataName() + MementoKeys.VerticalScrollPosition.name(),
        //        reportPanel.getVerticalScrollPosition());
    }

    @Override
    public void restoreState() {
        if (getMemento().mayRestore()) {
            ReportMetadata reportMetadata = (ReportMetadata) getMemento().getObject(
                    ((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.ReportMetadata.name());
            setReportSettings(reportMetadata, null); // TODO deal with report metadata Id
            boolean hadData = Boolean.TRUE.equals(getMemento().getObject(
                    ((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.HasData.name()));
            if (hadData) {
                presenter.apply(reportMetadata);
                // TODO fix the MEMENTO of report
                //                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                //                    @Override
                //                    public void execute() {
                //                        Integer horizontalScrollPosition = getMemento().getInteger(
                //                                ((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.HorizontalScrollPosition.name());
                //                        if (horizontalScrollPosition != null) {
                //                            reportPanel.setHorizontalScrollPosition(horizontalScrollPosition);
                //                        }
                //                        Integer verticalScrollPosition = getMemento().getInteger(
                //                                ((ReportsAppPlace) presenter.getPlace()).getReportMetadataName() + MementoKeys.VerticalScrollPosition.name());
                //                        if (verticalScrollPosition != null) {
                //                            reportPanel.setVerticalScrollPosition(verticalScrollPosition);
                //                        }
                //                    }
                //                });
            }
        } else {
            setReportSettings(((ReportsAppPlace) getMemento().getCurrentPlace()).getReportMetadata(), null);
        }
    }

    @Override
    public List<Class<? extends ReportMetadata>> getSupportedReportMetadata() {
        return new ArrayList<Class<? extends ReportMetadata>>(reportFactoryMap.keySet());
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
        settingsForm.addValueChangeHandler(new ValueChangeHandler<ReportMetadata>() {
            @Override
            public void onValueChange(ValueChangeEvent<ReportMetadata> event) {
                hasData = false;
            }
        });
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
        if (settingsForm != null) {
            settingsForm.setEnabled(true);
        }
        reportSettingsFormControlBar.setEnabled(true);
        reportPanel.setVisible(true);

        reportProgressHolderPanel.setWidget(null);
        reportProgressControlPanel.setVisible(false);
        progressPanel = null;
    }

}
