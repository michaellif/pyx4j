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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CommonsStringUtils;
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

public abstract class AbstractReport<R extends ReportMetadata> extends AbstractPrimePane implements IReportsView<R> {

    public enum Styles {

        ReportView, SettingsFormPanel, ReportPanel, ReportProgressControlPanel, ReportProgressErrorPanel;

    }

    private enum MementoKeys {

        ReportMetadata, HasReportData, HorizontalScrollPosition, VerticalScrollPosition, ReportWidget

    }

    public static class ReportPrintPalette extends Palette {

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

    private IReportsView.Presenter<R> presenter;

    private FlowPanel viewPanel;

    private SimplePanel settingsFormPanel;

    private SimplePanel reportPanel;

    private ReportSettingsFormControlBar reportSettingsFormControlBar;

    private Button exportButton;

    private FlowPanel reportProgressControlPanel;

    private Button abortReportGenerationButton;

    private SimplePanel reportProgressHolderPanel;

    private DeferredProgressPanel progressPanel;

    private FlowPanel errorPanel;

    private ReportWidget reportWidget;

    private CEntityForm<R> activeSettingsForm;

    private CEntityForm<R> simpleSettingsForm;

    private CEntityForm<R> advancedSettingsForm;

    /**
     * @param advancedSettingsForm
     *            this is optional, and in this case ReportMetadata has to implement HasAdvancedSettings
     */
    public void setReportWidget(ReportWidget reportWidget, CEntityForm<R> simpleSettingsForm, CEntityForm<R> advancedSettingsForm) {
        setSize("100%", "100%");

        this.reportWidget = reportWidget;
        this.simpleSettingsForm = simpleSettingsForm;
        this.simpleSettingsForm.initContent();
        this.advancedSettingsForm = advancedSettingsForm;
        if (this.advancedSettingsForm != null) {
            this.advancedSettingsForm.initContent();
        }

        this.activeSettingsForm = null;
        this.presenter = null;

        this.viewPanel = new FlowPanel();
        this.viewPanel.setStyleName(Styles.ReportView.name());
        this.viewPanel.setWidth("100%");
        this.viewPanel.setHeight("100%");

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
                if (presenter != null & activeSettingsForm != null) {
                    AbstractReport.this.apply();
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
                showReportWidget();
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
        reportPanel.setStylePrimaryName(Styles.ReportPanel.name());
        reportPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportPanel.getElement().getStyle().setLeft(0, Unit.PX);
        reportPanel.getElement().getStyle().setTop(18, Unit.EM);
        reportPanel.getElement().getStyle().setRight(0, Unit.PX);
        reportPanel.getElement().getStyle().setBottom(0, Unit.PX);
        viewPanel.add(reportPanel);

        addHeaderToolbarItem(new Button(i18n.tr("Load..."), new Command() {
            @Override
            public void execute() {
                presenter.populateAvailableReportMetadata();
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
                presenter.export();
            }
        }));

        setContentPane(viewPanel);
    }

    @Override
    public void setPresenter(IReportsView.Presenter<R> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReportMetadata(R reportMetadata) {
        hideVisor();

        this.activeSettingsForm = null;
        this.settingsFormPanel.setWidget(null);
        this.reportSettingsFormControlBar.setEnabled(false);
        this.reportPanel.setWidget(null);
        this.errorPanel.setVisible(false);

        if (reportMetadata != null) {
            exportButton.setVisible(reportMetadata instanceof ExportableReport);

            populateSettingsForm(reportMetadata);

            reportWidget.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            showReportWidget();
            reportPanel.setWidget(reportWidget);
            reportWidget.setData(null, new Command() {
                @Override
                public void execute() {
                    unlockReportSettings();
                    resetCaption();
                }
            });
        } else {
            resetCaption();
        }

    }

    @Override
    public R getReportMetadata() {
        return activeSettingsForm.getValue();
    }

    @Override
    public void setReportData(Object data) {
        errorPanel.setVisible(false);

        lockReportSettings();
        showReportWidget();
        if (data != null) {
            reportWidget.asWidget().setVisible(true);
            reportWidget.setData(data, new Command() {
                @Override
                public void execute() {
                    unlockReportSettings();
                }
            });
        }
    }

    @Override
    public void setError(String errorMessage) {
        unlockReportSettings();
        errorPanel.clear();
        setReportData(null);
        if (errorMessage != null) {
            errorPanel.setVisible(true);
            errorPanel.add(new Label(errorMessage));
        } else {
            errorPanel.setVisible(false);
        }
    }

    @Override
    public void startReportGenerationProgress(String deferredProgressCorelationId, DeferredProgressListener deferredProgressListener) {
        activeSettingsForm.setEnabled(false);
        reportSettingsFormControlBar.setEnabled(false);
        reportPanel.setVisible(false);
        errorPanel.setVisible(false);

        progressPanel = new DeferredProgressPanel("600px", "30px", false, deferredProgressListener);
        progressPanel.startProgress(deferredProgressCorelationId);
        reportProgressHolderPanel.setWidget(progressPanel);
        reportProgressControlPanel.setVisible(true);
    }

    @Override
    public void onReportMetadataSaveFailed(String reason) {
        MessageDialog.error(i18n.tr("Save Failed"), reason);
        activeSettingsForm.getValue().reportMetadataId().setValue(null);
        resetCaption();
    }

    @Override
    public void onReportMetadataSaveSucceed(String reportSettingsId) {
        MessageDialog.info(i18n.tr("Report settings were saved successfuly!"));
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
        Element reportElement = (Element) reportPanel.getElement().getFirstChildElement().cloneNode(true);
        reportElement.getStyle().setPosition(Position.STATIC);
        reportElement.getStyle().setDisplay(Display.BLOCK);
        reportElement.getStyle().clearOverflow();
        reportElement.getStyle().clearLeft();
        reportElement.getStyle().clearTop();
        reportElement.getStyle().clearRight();
        reportElement.getStyle().clearBottom();

        printableHtml.append(reportElement.getString());
        printableHtml.append("</body>");
        printableHtml.append("</html>");

        Print.preview(printableHtml.toString());
    }

    @Override
    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);
        getMemento().putObject(MementoKeys.ReportMetadata.name(), activeSettingsForm.getValue());
        getMemento().putObject(MementoKeys.ReportWidget.name(), reportWidget.getMemento());
    }

    @Override
    public void restoreState() {
        if (getMemento().mayRestore()) {
            R reportMetadata = (R) getMemento().getObject(MementoKeys.ReportMetadata.name());
            setReportMetadata(reportMetadata);

            Object reportMemento = getMemento().getObject(MementoKeys.ReportWidget.name());

            lockReportSettings();

            reportWidget.setMemento(reportMemento, new Command() {
                @Override
                public void execute() {
                    showReportWidget();
                    unlockReportSettings();
                }
            });
        } else {
            setReportMetadata((R) ((ReportsAppPlace<?>) getMemento().getCurrentPlace()).getReportMetadata());
        }
    }

    private void setSettingsMode(boolean isAdvanced) {
        if (activeSettingsForm != null) {
            ((HasAdvancedSettings) activeSettingsForm.getValue()).isInAdvancedMode().setValue(isAdvanced);
            populateSettingsForm(activeSettingsForm.getValue());
        }
    }

    private void populateSettingsForm(R reportSettings) {
        if (advancedSettingsForm != null) {
            boolean isAdvancedMode = ((HasAdvancedSettings) reportSettings).isInAdvancedMode().isBooleanTrue();
            activeSettingsForm = isAdvancedMode ? advancedSettingsForm : simpleSettingsForm;
            reportSettingsFormControlBar.enableSettingsModeToggle(isAdvancedMode);
        } else {
            activeSettingsForm = simpleSettingsForm;
            reportSettingsFormControlBar.disableModeToggle();
        }
        settingsFormPanel.setWidget(activeSettingsForm);
        activeSettingsForm.reset();
        activeSettingsForm.populate(reportSettings);
    }

    private void onSaveSettingsAsClicked() {
        new OkCancelDialog(i18n.tr("Save current report settings as:")) {
            private TextBox reportMetadataIdTextBox;

            {
                setBody(reportMetadataIdTextBox = new TextBox());
            }

            @Override
            public boolean onClickOk() {
                if (!reportMetadataIdTextBox.getText().isEmpty()) {
                    activeSettingsForm.getValue().reportMetadataId().setValue(reportMetadataIdTextBox.getText());
                    presenter.saveReportMetadata(false);
                    return true;
                } else {
                    return false;
                }
            }
        }.show();
    }

    private void onSaveSettingsClicked() {
        if (!CommonsStringUtils.isStringSet(activeSettingsForm.getValue().reportMetadataId().getValue())) {
            onSaveSettingsAsClicked();
        } else {
            presenter.saveReportMetadata(true);
        }
    }

    private void resetCaption() {
        ReportMetadata reportMetadata = activeSettingsForm != null ? activeSettingsForm.getValue() : null;
        if (reportMetadata == null) {
            // TODO: maybe just throw an exception?
            setCaption(i18n.tr("Non Defined Report"));
        } else {
            String reportStringView = reportMetadata.isNull() ? i18n.tr("Untitled") : reportMetadata.getStringView();
            setCaption(SimpleMessageFormat.format("{0} - {1}", reportMetadata.getEntityMeta().getCaption(), reportStringView));
        }
    }

    private void showReportWidget() {
        reportPanel.setVisible(true);

        reportProgressHolderPanel.setWidget(null);
        reportProgressControlPanel.setVisible(false);
        progressPanel = null;
    }

    private void unlockReportSettings() {
        if (activeSettingsForm != null) {
            activeSettingsForm.setEnabled(true);
        }
        reportSettingsFormControlBar.setEnabled(true);
    }

    private void lockReportSettings() {
        if (activeSettingsForm != null) {
            activeSettingsForm.setEnabled(false);
        }
        reportSettingsFormControlBar.setEnabled(false);
    }

    private void apply() {
        activeSettingsForm.setVisitedRecursive();
        if (activeSettingsForm.isValid()) {
            AbstractReport.this.reportWidget.asWidget().setVisible(false);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    presenter.apply(true);
                }
            });
        }
    }

}
