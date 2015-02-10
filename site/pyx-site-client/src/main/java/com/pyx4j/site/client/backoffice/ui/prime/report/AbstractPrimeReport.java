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
 */
package com.pyx4j.site.client.backoffice.ui.prime.report;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.client.backoffice.ui.prime.AbstractPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.form.PrimeEntityForm;
import com.pyx4j.site.client.backoffice.ui.prime.report.IPrimeReportView.IPrimeReportPresenter;
import com.pyx4j.site.shared.domain.reports.ExportableReport;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class AbstractPrimeReport<R extends ReportTemplate> extends AbstractPrimePaneView<IPrimeReportPresenter<R>> implements IPrimeReportView<R> {

    private static final I18n i18n = I18n.get(AbstractPrimeReport.class);

    private FlowPanel viewPanel;

    private ReportControlPanel<R> reportControlPanel;

    private Button exportButton;

    private FlowPanel errorPanel;

    private IReportWidget reportWidget;

    private CForm<R> settingsForm;

    public AbstractPrimeReport() {
    }

    public void setReportWidget(IReportWidget reportWidget, PrimeEntityForm<R> settingsForm, CForm<R> controlPanelForm) {
        setSize("100%", "100%");

        this.reportWidget = reportWidget;

        this.settingsForm = settingsForm;
        if (this.settingsForm != null) {
            this.settingsForm.init();
        }

        this.viewPanel = new FlowPanel();
        this.viewPanel.setStyleName(PaneTheme.StyleName.ReportView.name());
        this.viewPanel.setWidth("100%");
        this.viewPanel.setHeight("100%");

        reportControlPanel = new ReportControlPanel<>(this, controlPanelForm);

        viewPanel.add(reportControlPanel);

        errorPanel = new FlowPanel();
        errorPanel.setStyleName(PaneTheme.StyleName.ReportProgressErrorPanel.name());
        errorPanel.setVisible(false);
        viewPanel.add(errorPanel);

        viewPanel.add(reportWidget);

// removed currently according VISTA-5872
//
//        addHeaderToolbarItem(new Button(i18n.tr("Customize..."), new Command() {
//            @Override
//            public void execute() {
//                getPresenter().loadAvailableTemplates();
//            }
//        }));

        addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                print();
            }
        }));

        addHeaderToolbarItem(exportButton = new Button(i18n.tr("Export"), new Command() {
            @Override
            public void execute() {
                getPresenter().export();
            }
        }));

        setContentPane(new ScrollPanel(viewPanel));
    }

    @Override
    public void setReportMetadata(R reportMetadata) {
        hideVisor();

        this.errorPanel.setVisible(false);

        if (reportMetadata != null) {
            exportButton.setVisible(reportMetadata instanceof ExportableReport);

            populateSettingsForm(reportMetadata);

            reportWidget.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            reportWidget.setData(null);
            resetCaption();
        } else {
            resetCaption();
        }

    }

    @Override
    public R getReportSettings() {
        return reportControlPanel.getReportSettings();
    }

    @Override
    public void setReportData(Object data) {
        errorPanel.setVisible(false);
        reportControlPanel.onReportGenerationStopped();

        if (data != null) {
            reportWidget.asWidget().setVisible(true);
            reportWidget.setData(data);
        }
    }

    @Override
    public void setError(String errorMessage) {
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
        reportWidget.setData(null);
        errorPanel.setVisible(false);
        reportControlPanel.onReportGenerationStarted(deferredProgressCorelationId, deferredProgressListener);
    }

    @Override
    public void onReportMetadataSaveFailed(String reason) {
        MessageDialog.error(i18n.tr("Save Failed"), reason);
        reportControlPanel.getReportSettings().reportTemplateName().setValue(null);
        resetCaption();
    }

    @Override
    public void onReportMetadataSaveSucceed() {
        MessageDialog.info(i18n.tr("Report settings were saved successfully!"));
        resetCaption();
    }

    private void populateSettingsForm(R reportSettings) {
        reportControlPanel.populateReportSettings(reportSettings);
    }

    private void saveSettings() {
        if (!CommonsStringUtils.isStringSet(reportControlPanel.getReportSettings().reportTemplateName().getValue())) {
            saveSettingsAs();
        } else {
            getPresenter().saveReportMetadata();
        }
    }

    private void saveSettingsAs() {
        new OkCancelDialog(i18n.tr("Save current report settings as:")) {
            private StringBox reportMetadataIdTextBox;

            {
                setBody(reportMetadataIdTextBox = new StringBox());
            }

            @Override
            public boolean onClickOk() {
                if (!reportMetadataIdTextBox.getValue().isEmpty()) {
                    reportControlPanel.getReportSettings().reportTemplateName().setValue(reportMetadataIdTextBox.getValue());
                    getPresenter().saveAsReportMetadata();
                    return true;
                } else {
                    return false;
                }
            }
        }.show();
    }

    private void resetCaption() {
        R reportSettings = reportControlPanel.getReportSettings();
        if (reportSettings == null) {
            // TODO: maybe just throw an exception?
            setCaption(i18n.tr("Non Defined Report"));
        } else {
            String reportStringView = reportSettings.isNull() ? i18n.tr("Untitled") : reportSettings.getStringView();
            setCaption(SimpleMessageFormat.format("{0} - {1}", reportSettings.getEntityMeta().getCaption(), reportStringView));
        }
    }

    void runReportGeneration() {
        if (getPresenter() != null) {
            if (reportControlPanel.isValid()) {
                getPresenter().runReportGeneration();
            }
        }
    }

    void abortReportGeneration() {
        if (getPresenter() != null) {
            getPresenter().abortReportGeneration();
        }
    }

    public void print() {
        StringBuilder printableHtml = new StringBuilder();
        printableHtml.append("<html>");
        printableHtml.append("<head>");
        printableHtml.append("<style type=\"text/css\">");
        printableHtml.append(ReportPrintTheme.getPrintTheme());
        printableHtml.append("</style>");
        printableHtml.append("</head>");
        printableHtml.append("<body>");
        Element reportElement = (Element) reportWidget.asWidget().getElement().cloneNode(true);
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

    public static class ReportPrintPalette extends Palette {

    }

    public static class ReportPrintTheme extends Theme {

        private static String printTheme;

        public enum Styles implements IStyleName {

            ReportNonPrintable, ReportPrintableOnly

        }

        static {

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

        public static String getPrintTheme() {
            if (printTheme == null) {
                StringBuilder stylesString = new StringBuilder();
                ReportPrintTheme theme = new ReportPrintTheme();
                Palette palette = new ReportPrintPalette();
                for (Style style : theme.getAllStyles()) {
                    stylesString.append(style.getCss(theme, palette));
                }
                printTheme = stylesString.toString();
            }
            return printTheme;
        }
    }

    @Override
    public void reset() {
    }

}
