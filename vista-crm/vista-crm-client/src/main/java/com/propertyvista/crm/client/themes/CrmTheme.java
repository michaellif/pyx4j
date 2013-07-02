/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.themes;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.site.client.ui.reports.AbstractReport;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.dashboard.CSSNames;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.richtext.DefaultRichTextEditorTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.DraggerMixin;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.HeaderViewImpl;
import com.propertyvista.crm.client.ui.SearchBox;
import com.propertyvista.crm.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.crm.client.ui.components.KeywordsBox;
import com.propertyvista.crm.client.ui.components.LegalTermsContentViewer;
import com.propertyvista.crm.client.ui.components.PmcSignatureForm;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AddGadgetDialog;
import com.propertyvista.crm.client.ui.gadgets.forms.ArrearsGadgetSummaryForm;
import com.propertyvista.crm.client.ui.reports.components.CommonReportStyles;
import com.propertyvista.crm.client.ui.wizard.creditcheck.components.CreditCheckReportTypeSelector;

public class CrmTheme extends VistaTheme {

    public static double defaultHeaderHeight = 3;

    public static double defaultFooterHeight = 3;

    public static double defaultActionBarHeight = 2.9;

    public static enum TitleBarStyleName implements IStyleName {
        TitleBarBreadcrumb
    }

    public static enum ArrearsStyleName implements IStyleName {
        ArrearsColumnTitle, ArrearsMoneyColumnTitle, ArrearsCategoryEven, ArrearsCategoryOdd, ArrearsCategoryAll, ArrearsMoneyCell;
    }

    public CrmTheme() {
        initStyles();
    }

    protected void initStyles() {
        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultRichTextEditorTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultPaneTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new DefaultTabTheme());

        addTheme(new SiteViewTheme());
        addTheme(new DraggerMixin());

        addTheme(new BillingTheme());
        addTheme(new TransactionHistoryViewerTheme());

        initGeneralStyles();
        initBodyStyles();
        initCellListStyle();
        initMessageStyles();

        initSearchBoxStyles();
        initButtonStylesEx();

        initDashboardView();
        initDashboardReport();

        initTabPanelStyles();
        initDialogBoxStyles();
        initMenuBarStyles();

        initAddGadgetDialogStyles();
        initArrearsViewStyles();

        initSuggestBoxStyle();
        initKeywordBoxStyles();

        initWizardPanelStyles();
        initCreditCheckReportTypeSelectorStyles();
        initPmcSignatureFormStyles();
        initLegalTermsContentViewerStyles();

        initReportsStyles();
        initGadgetStyles();
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();

        Style style = new Style("a");
        style.addProperty("color", "#333");
        addStyle(style);
    }

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style("body");
        style.addProperty("overflow", "hidden");
        addStyle(style);
    }

    protected void initSearchBoxStyles() {
        String prefix = SearchBox.DEFAULT_STYLE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background", "#ffffff");
        style.addProperty("overflow", "hidden");
        style.addProperty("border", "none");
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("min-width", "8em !important");
        style.addProperty("white-space", "normal !important");
        style.addProperty("padding-left", "0.7em !important");
        addStyle(style);

        // SearchBox
        style = new Style(Selector.valueOf(prefix, StyleSuffix.Text));
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("border", "none");
        //  style.addProperty("min-width", "4em !important");
        addStyle(style);

        //trigger
        style = new Style(Selector.valueOf(prefix, StyleSuffix.Trigger));
/*
 * style.addProperty("background", "url(" + CrmImages.INSTANCE.search().getURL() +
 * ") no-repeat");
 * style.addProperty("background-position", "center");
 */
        style.addProperty("width", "16px");
        style.addProperty("heigh", "16px");
        style.addProperty("float", "right !important");
        style.addProperty("border", "none");
        style.addProperty("margin-right", "6px !important");
        style.addProperty("margin-top", "1px !important");

        addStyle(style);
    }

    protected void initButtonStylesEx() {

        //
        // Toggle Button
        Style style = new Style(".gwt-ToggleButton");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "black");
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".gwt-ToggleButton-up");
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "outset");
        addStyle(style);

        String buttonEx = Selector.valueOf("gwt-ToggleButton-up-hovering");
        style = new Style(buttonEx);
        style.addGradient(ThemeColor.foreground, 0.0, ThemeColor.foreground, 0.2);
        style.addProperty("border-style", "outset");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down");
        style = new Style(buttonEx);
        style.addGradient(ThemeColor.foreground, 0.3, ThemeColor.foreground, 0.1);
        style.addProperty("border-style", "inset");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down-hovering");
        style = new Style(buttonEx);
        style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.1);
        style.addProperty("cursor", "pointer");
        style.addProperty("border-style", "inset");
        addStyle(style);

        // Push Button
        buttonEx = ".gwt-PushButton";
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColor.background);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.background);
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(buttonEx + "-up-hovering");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(buttonEx + "-down");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(buttonEx + "-down-hovering");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        //
        // Back2CRM link:
        buttonEx = Selector.valueOf(HeaderViewImpl.BACK_TO_CRM);
        style = new Style(buttonEx);
        style.addProperty("font-weight", "bolder");
        style.addProperty("padding-bottom", "0.2em");
        style.addProperty("border-bottom", "2px dotted");
        style.addProperty("border-bottom-color", ThemeColor.object1, 0.15);
        addStyle(style);
    }

    protected void initDashboardView() {
        String prefix = AbstractDashboard.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AbstractDashboard.StyleSuffix.filtersDescription));
        style.addProperty("color", ThemeColor.foreground, 0.95);
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.1em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AbstractDashboard.StyleSuffix.filtersPanel));
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.3);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("border-bottom", "1px solid #ccc");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AbstractDashboard.StyleSuffix.actionsPanel));
        style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.3);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin-bottom", "0.2em");
        addStyle(style);
    }

    protected void initDashboardReport() {
        String prefix = CSSNames.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
//        style.addProperty("border", "1px solid #aaa");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Column));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.ColumnSpacer));
        style.addProperty("height", "4em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder));
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("border", "1px solid #ccc");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder, CSSNames.StyleDependent.maximized));
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderSetup));
        style.addProperty("background-color", ThemeColor.contrast1, 0.1);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption));
        style.addProperty("background-color", ThemeColor.foreground);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("height", "20px");
        style.addProperty("padding-left", "1em");
        style.addProperty("cursor", "move");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption, CSSNames.StyleDependent.readonly));
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption) + ":hover");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderHeading));
        style.addProperty("padding-top", "2px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderMenu));
        style.addProperty("background-color", ThemeColor.foreground);
        style.addProperty("border", "1px solid #aaa");
        style.addProperty("font", "menu");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.DndPositioner));
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.DndReportPositioner));
        style.addProperty("border", "1px dotted #555");
        style.addProperty("margin", "5px");
        addStyle(style);

        // overriding gwt-dnd styles:
        style = new Style(".dragdrop-handle");
//        style.addProperty("cursor", "pointer");
        addStyle(style);
    }

    private void initAddGadgetDialogStyles() {
        String prefix = AddGadgetDialog.ADD_GADGET_DIALOG_STYLE;

        Style style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetDescriptionsList);
        style.addProperty("margin-top", "0.5em");
        style.addProperty("margin-left", "0.5em");
        style.addProperty("margin-right", "0.5em");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetDescriptionBox);
        style.addProperty("margin-bottom", "0.2em");
        style.addProperty("margin-bottom", "0.2em");
        addStyle(style);

        style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetDescriptionDecorator);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin-top", "0.2em");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetDescriptionDecorator + ":hover");
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.8);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetNameLabel);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style("." + prefix + AddGadgetDialog.StyleSuffix.GadgetDescriptionText);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("text-align", "justify");
        addStyle(style);
    }

    protected void initArrearsViewStyles() {
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsColumnTitle.name());
            style.addProperty("font-weight", "bold");
            style.addProperty("text-align", "left");
            addStyle(style);
        }
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsMoneyColumnTitle.name());
            style.addProperty("font-weight", "bold");
            style.addProperty("text-align", "right");
            addStyle(style);
        }
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsCategoryAll.name());
            style.addProperty("font-weight", "bold");
            style.addProperty("border-weight", "bold");
            style.addProperty("padding", "1em 2px");
            addStyle(style);

        }
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsCategoryEven.name());
            style.addProperty("padding", "1em 2px");
            addStyle(style);

        }
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsCategoryOdd.name());
            style.addProperty("padding", "1em 2px");
            addStyle(style);
        }
        {
            Style style = new Style("." + ArrearsStyleName.ArrearsMoneyCell);
            style.addProperty("text-align", "right");
            style.addProperty("font-family", "monospace");
            addStyle(style);
        }

    }

    private void initReportsStyles() {
        Style style = new Style("." + AbstractReport.Styles.SettingsFormPanel.name());
        style.addProperty("background-color", ThemeColor.object1, 0.3);
        addStyle(style);

        style = new Style("." + AbstractReport.Styles.ReportView.name(), " .FooterToolbar");
        style.addProperty("height", "2em");
        addStyle(style);

        style = new Style("." + AbstractReport.Styles.ReportProgressControlPanel.name());
        style.addProperty("text-align", "center");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style("." + AbstractReport.Styles.ReportProgressErrorPanel.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "red");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style("." + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name());
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RCellNumber.name());
        style.addProperty("text-align", "right");
        style.addProperty("padding-right", "5px");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RRowTotal.name());
        style.addProperty("background-color", ThemeColor.foreground, 0.5);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RReportTableFixedHeader.name());
        style.addProperty("display", "block");
        style.addProperty("overflow-y", "scroll");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RReportTableFixedHeader.name() + " th");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RReportTableScrollableBody.name());
        style.addProperty("position", "absolute");
        style.addProperty("top", "0px"); // this value has to be computed and set after the table header is drawn;
        style.addProperty("bottom", "0px");
        style.addProperty("left", "0px");
        style.addProperty("width", "100%");
        style.addProperty("display", "block");
        style.addProperty("overflow-y", "scroll");
        addStyle(style);

        style = new Style("." + CommonReportStyles.RReportTableScrollableBody.name() + " td");
        addStyle(style);

    }

    private void initKeywordBoxStyles() {
        Style style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX);
        style.addProperty("display", "inline");
        style.addProperty("border-style", "inset");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "3px");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.ActiveKeywords);
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.Keyword);
        style.addProperty("display", "inline-block");
        style.addProperty("padding-top", "1px");
        style.addProperty("padding-bottom", "1px");
        style.addProperty("padding-left", "5px");
        style.addProperty("padding-right", "5px");
        style.addProperty("margin-right", "5px");
//        style.addProperty("border-style", "outset");
        style.addProperty("border-width", "1px");
//        style.addProperty("border-radius", "5px");
        style.addGradient(ThemeColor.object1, 0.5, ThemeColor.object1, 1);
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordLabel);
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordUnselectButton);
        style.addProperty("display", "inline");
        style.addProperty("cursor", "pointer");
        style.addProperty("margin-left", "5px");
        style.addProperty("padding", "0px");
        style.addProperty("border-radius", "2px");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordUnselectButton + ":hover");
        style.addProperty("background-color", ThemeColor.object1, 1);
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordsAdder);
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordsAdderList);
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style("." + KeywordsBox.DEFAULT_STYLE_PREFIX + KeywordsBox.StyleSuffix.KeywordsAdderButton);
        style.addProperty("display", "inline-block");
        addStyle(style);
    }

    private void initWizardPanelStyles() {
        Style style = new Style("." + DefaultTabTheme.StyleName.WizardPanel, " .", DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH1Label);
        style.addProperty("text-align", "center");
        addStyle(style);
    }

    private void initLegalTermsContentViewerStyles() {
        Style style = new Style("." + LegalTermsContentViewer.Styles.LegalTermsContentViewerCaption);
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style("." + LegalTermsContentViewer.Styles.LegalTermsContentViewerHolder);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "inset");
        style.addProperty("border-color", ThemeColor.background);
        addStyle(style);

        style = new Style("." + LegalTermsContentViewer.Styles.LegalTermsContentViewerContent);
        style.addProperty("margin", "15px");
        addStyle(style);
    }

    private void initPmcSignatureFormStyles() {
        Style style = new Style("." + PmcSignatureForm.Styles.PmcSignatureFormPmcLegalName);
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style("." + PmcSignatureForm.Styles.PmcSignatureFormRepName);
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.5em");
        addStyle(style);
    }

    private void initCreditCheckReportTypeSelectorStyles() {
        Style style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypePanel.name());
        style.addProperty("height", "50em");
        style.addProperty("cursor", "pointer");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "inset");
        style.addProperty("border-style", "solid");
        style.addProperty("border-radius", "5px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypePanel.name() + ":hover");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "outset");
        style.addProperty("background-color", ThemeColor.object2);
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypePanel.name() + "-"
                + CreditCheckReportTypeSelector.StyleDependent.Selected.name());
        style.addProperty("border-style", "outset");
        style.addProperty("border-width", "2px");
        style.addProperty("background-color", ThemeColor.object1);
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypePanel.name() + "-"
                + CreditCheckReportTypeSelector.StyleDependent.Selected.name() + ":hover");
        style.addProperty("background-color", ThemeColor.object1);
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypeLabel.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-top", "20px");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckPoweredByLabel.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-bottom", "10px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckPoweredByLogo.name());
        style.addProperty("display", "block");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("margin-bottom", "20px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypePerApplicantFee.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "1.5em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-bottom", "5px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportTypeSetupFee.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "1em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);

        style = new Style("." + CreditCheckReportTypeSelector.Styles.CreditCheckReportDetailsLabel.name());
        style.addProperty("display", "table-cell");
        style.addProperty("vertical-align", "center");
        style.addProperty("height", "20em");
        style.addProperty("padding-left", "1em");
        style.addProperty("padding-right", "1em");
        addStyle(style);
    }

    private void initGadgetStyles() {
        Style style = new Style("." + ArrearsGadgetSummaryForm.Styles.ArrearsSummaryPanel.name() + " .ViewerPanel");
        style.addProperty("display", "inline");
        addStyle(style);
    }

}