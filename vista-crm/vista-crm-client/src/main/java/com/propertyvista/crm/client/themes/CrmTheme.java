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
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.entity.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.DefaultCCOmponentsTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.dashboard.CSSNames;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.richtext.DefaultRichTextEditorTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.common.client.theme.DraggerMixin;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.SearchBox;
import com.propertyvista.crm.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;
import com.propertyvista.crm.client.ui.TopRightActionsViewImpl;
import com.propertyvista.crm.client.ui.board.BoardBase;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.gadgets.addgadgetdialog.GadgetDirectoryDialog;

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

    public static enum TransactionHistoryStyleName implements IStyleName {
        TransactionsHistoryColumnTitle, TransactionsHistoryMoneyColumnTitle, TransactionRecordEven, TransactionRecordOdd, TransactionRecordMoneyCell;
    }

    public CrmTheme() {
        initStyles();
    }

    protected void initStyles() {
        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme());
        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.foreground;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.foreground;
            }
        });
        addTheme(new DefaultRichTextEditorTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultSiteCrudPanelsTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCCOmponentsTheme());

        addTheme(new DefaultTabTheme());

        addTheme(new CrmSitePanelTheme());
        addTheme(new DraggerMixin());

        addTheme(new BillingTheme());

        initGeneralStyles();
        initBodyStyles();
        initCellListStyle();

        intitNavigationStyles();
        intitShortCutStyles();

        initSearchBoxStyles();
        initButtonStylesEx();

        initDashboardView();
        initDashboardReport();

        initTabPanelStyles();
        initDialogBoxStyles();
        initMenuBarStyles();

        initGadgetDirectoryStyles();
        initTransactionHistoryStyles();
        initArrearsViewStyles();

        initSuggestBoxStyle();

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

    protected void intitNavigationStyles() {
        String prefix = NavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(prefix);

        /*
         * anchors within the class:
         */
        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:link, " + prefix + " a:visited, " + prefix + " a:active"));
        style.addProperty("text-decoration", "none");
        addStyle(style);

        /*
         * components within the class:
         */
        // stack header
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader"));
        style.addProperty("font-size", "1.3em");
//        style.addProperty("padding-top", "0.2em");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("cursor", "pointer");
        style.addProperty("color", ThemeColors.object1, 0.1);
        style.addGradient(ThemeColors.object1, 1, ThemeColors.object1, 0.6);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader", null, NavigViewImpl.StyleDependent.selected));
        style.addProperty("font-weight", "bold");
        style.addShadow(ThemeColors.foreground, "1px 1px 0");
        style.addGradient(ThemeColors.object1, 1, ThemeColors.object1, 0.8);
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "1em");
        style.addProperty("background-color", ThemeColors.foreground, 0.02);
        addStyle(style);

        // Item style defines anchor specific styling
        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Item));
        style.addProperty("margin-bottom", "0.3em");
        addStyle(style);

        // ???
        style = new Style(Selector.valueOf(NavigViewImpl.DEFAULT_STYLE_PREFIX, NavigViewImpl.StyleSuffix.NoBottomMargin));
        style.addProperty("margin-bottom", "0 !important");
        addStyle(style);
    }

    /*
     * TODO When the layout is finalised it might make sense to combine
     * Navigation and ShortCuts styling due to their similarity
     */
    protected void intitShortCutStyles() {
        String prefix = ShortCutsViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));

        /*
         * anchors within the class:
         */
        style = new Style(prefix + " a:link, " + prefix + " a:visited, " + prefix + " a:active");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        /*
         * components within the class:
         */
        //stack header
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader"));
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "1em");
//        style.addProperty("cursor", "pointer");
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("border-top", "solid 4px");
        style.addProperty("border-top-color", ThemeColors.object1);
        // NOTE: must correspond with the header size defined by stackpanel
        style.addProperty("line-height", "2.2em");
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        addStyle(style);

        // Item style defines anchor specific styling
        style = new Style(Selector.valueOf(prefix, ShortCutsViewImpl.StyleSuffix.Item));
        style.addProperty("margin-left", "0.3em");
        style.addProperty("margin-right", "0.2em");
        style.addProperty("margin-bottom", "0.3em");
        addStyle(style);

        // Search line style
        style = new Style(Selector.valueOf(prefix, ShortCutsViewImpl.StyleSuffix.SearchBar));
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
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "black");
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".gwt-ToggleButton-up");
        style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.3);
        style.addProperty("border-style", "outset");
        addStyle(style);

        String buttonEx = Selector.valueOf("gwt-ToggleButton-up-hovering");
        style = new Style(buttonEx);
        style.addGradient(ThemeColors.foreground, 0.0, ThemeColors.foreground, 0.2);
        style.addProperty("border-style", "outset");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down");
        style = new Style(buttonEx);
        style.addGradient(ThemeColors.foreground, 0.3, ThemeColors.foreground, 0.1);
        style.addProperty("border-style", "inset");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down-hovering");
        style = new Style(buttonEx);
        style.addGradient(ThemeColors.foreground, 0.2, ThemeColors.foreground, 0.1);
        style.addProperty("cursor", "pointer");
        style.addProperty("border-style", "inset");
        addStyle(style);

        // Push Button
        buttonEx = ".gwt-PushButton";
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.background);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColors.background);
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(buttonEx + "-up-hovering");
        style.addProperty("border-color", ThemeColors.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(buttonEx + "-down");
        style.addProperty("border-color", ThemeColors.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(buttonEx + "-down-hovering");
        style.addProperty("border-color", ThemeColors.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);
        //
        // default AnchorButton: 
        buttonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.object1, 0.95);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        //
        // Back2CRM link:
        buttonEx = Selector.valueOf(TopRightActionsViewImpl.BACK_TO_CRM);
        style = new Style(buttonEx);
        style.addProperty("font-weight", "bolder");
        style.addProperty("padding-bottom", "0.2em");
        style.addProperty("border-bottom", "2px dotted");
        style.addProperty("border-bottom-color", ThemeColors.object1, 0.15);
        addStyle(style);
    }

    protected void initDashboardView() {
        String prefix = BoardBase.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, BoardBase.StyleSuffix.filtersDescription));
        style.addProperty("color", ThemeColors.foreground, 0.95);
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.1em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, BoardBase.StyleSuffix.filtersPanel));
        style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.3);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("border-bottom", "1px solid #ccc");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, BoardBase.StyleSuffix.actionsPanel));
        style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.3);
        style.addProperty("color", ThemeColors.foreground);
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
        style.addProperty("background-color", ThemeColors.background);
        style.addProperty("border", "1px solid #ccc");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder, CSSNames.StyleDependent.maximized));
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderSetup));
        style.addProperty("background-color", ThemeColors.contrast1, 0.1);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption));
        style.addProperty("background-color", ThemeColors.foreground);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("height", "20px");
        style.addProperty("padding-left", "1em");
        style.addProperty("cursor", "move");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption) + ":hover");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderHeading));
        style.addProperty("padding-top", "2px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderMenu));
        style.addProperty("background-color", ThemeColors.foreground);
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

    private void initGadgetDirectoryStyles() {
        String prefix = GadgetDirectoryDialog.STYLE;

        // GADGETS LIST
        Style style = new Style("." + prefix + " .cellListOddItem");
        style.addGradient(ThemeColors.object1, 0.1, ThemeColors.object1, 0.4);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "outset");
        style.addProperty("margin", "0.2em");
        style.addProperty("padding", "0.5em 1em");
        addStyle(style);

        style = new Style("." + prefix + " .cellListEvenItem");
        style.addGradient(ThemeColors.object1, 0.1, ThemeColors.object1, 0.4);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "outset");
        style.addProperty("margin", "0.2em");
        style.addProperty("padding", "0.5em 1em");
        addStyle(style);

        // CATEGORIES TREE
        style = new Style("." + prefix + " .cellTreeItem");
        style.addProperty("color", ThemeColors.foreground);
        addStyle(style);

        style = new Style("." + prefix + " .cellTreeSelectedItem");
        style.addGradient(ThemeColors.object1, 0.1, ThemeColors.object1, 0.4);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "outset");
        addStyle(style);

        style = new Style("." + prefix + " .cellTreeKeyboardSelectedItem");
        style.addProperty("background-color", ThemeColors.object2);
        addStyle(style);

        style = new Style("." + prefix + " .cellTreeItemValue");
        style.addProperty("text-align", "left");
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    protected void initTransactionHistoryStyles() {
        {
            Style style = new Style("." + TransactionHistoryStyleName.TransactionsHistoryColumnTitle.name());
            style.addProperty("font-weight", "bold");
            style.addProperty("text-align", "left");
            addStyle(style);
        }
        {
            Style style = new Style("." + TransactionHistoryStyleName.TransactionsHistoryMoneyColumnTitle.name());
            style.addProperty("font-weight", "bold");
            style.addProperty("text-align", "right");
            addStyle(style);
        }
        {
            Style style = new Style("." + TransactionHistoryStyleName.TransactionRecordEven.name());
            style.addProperty("padding", "1em 2px");
            addStyle(style);

        }
        {
            Style style = new Style("." + TransactionHistoryStyleName.TransactionRecordOdd.name());
            style.addProperty("padding", "1em 2px");
            addStyle(style);
        }
        {
            Style style = new Style("." + TransactionHistoryStyleName.TransactionRecordMoneyCell.name());
            style.addProperty("text-align", "right");
            style.addProperty("font-family", "monospace");
            addStyle(style);
        }

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

}