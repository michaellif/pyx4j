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
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.dashboard.CSSNames;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.common.client.theme.DraggerMixin;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.SearchBox;
import com.propertyvista.crm.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;
import com.propertyvista.crm.client.ui.TopRightActionsViewImpl;
import com.propertyvista.crm.client.ui.board.BoardBase;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.decorations.CrmActionsBarDecorator;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;

public class VistaCrmTheme extends VistaTheme {

    public static double defaultHeaderHeight = 3;

    public static double defaultFooterHeight = 3;

    public static double defaultActionBarHeight = 2.9;

    public static double defaultTabHeight = 2.6;

    public static enum StyleSuffixEx implements IStyleName {
        SaveButton, CancelButton, EditButton, ActionButton;
    }

    public VistaCrmTheme() {
        initStyles();
    }

    protected void initStyles() {
        addTheme(new HorizontalAlignCenterMixin());

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

        addTheme(new DraggerMixin());

        addTheme(new CrmSitePanelTheme());

        initGeneralStyles();
        initBodyStyles();

        intitNavigationStyles();
        intitShortCutStyles();

        initSearchBoxStyles();
        initButtonStylesEx();
        initHeadersStyle();

        initSectionSeparatorStyle();
        initVistaTabStyles();
        initVistaDecoratorsPanelStyles();
        initDashboardView();
        initDashboardReport();
        initListerStyles();
        initEntityDataTableStyles();
        initTabPanelStyles();
        initDialogBoxStyles();
        initDialogPanelStyles();
        initMenuBarStyles();
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();

        Style style = new Style("a");
        style.addProperty("color", "#333");
        addStyle(style);

        // editor forms:
        style = new Style(Selector.valueOf(VistaDecoratorsFlowPanel.DEFAULT_STYLE_NAME));
//        style.addProperty("color", ThemeColor.TEXT);
//        style.addProperty("WebkitBoxSizing", "border-box");
//        style.addProperty("MozBoxSizing", "border-box");
//        style.addProperty("boxSizing", "border-box");
//        style.addProperty("padding", "0.7em");
//        style.addProperty("margin", "0.7em");
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
        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Item));
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
        // Save Button: 
        String buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.SaveButton);
        Style style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.9);
        addStyle(style);

        //
        // Edit Button: 
        buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.EditButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.9);
        addStyle(style);

        //
        // Action Button: 
        buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.ActionButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.9);
        addStyle(style);

        //
        // Toggle Button
        buttonEx = Selector.valueOf("gwt-ToggleButton");
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        style.addProperty("background-color", ThemeColors.foreground, 0.8);
        style.addProperty("border-width", "0px");
        style.addProperty("border-style", "solid");
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-up");
        style = new Style(buttonEx);
        style.addProperty("background-color", ThemeColors.object1, 0.4);
        style.addProperty("border-left-color", ThemeColors.object1, 0.4);
        style.addProperty("border-top-color", ThemeColors.object1, 0.4);
        style.addProperty("border-right-color", ThemeColors.object1, 0.95);
        style.addProperty("border-bottom-color", ThemeColors.object1, 0.95);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-up-hovering");
        style = new Style(buttonEx);
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColors.object1, 0.4);
        style.addProperty("border-left-color", ThemeColors.object1, 0.1);
        style.addProperty("border-top-color", ThemeColors.object1, 0.1);
        style.addProperty("border-right-color", ThemeColors.object1, 0.95);
        style.addProperty("border-bottom-color", ThemeColors.object1, 0.95);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down");
        style = new Style(buttonEx);
        style.addProperty("background-color", ThemeColors.object1, 0.8);
        style.addProperty("border-left-color", ThemeColors.object1, 0.95);
        style.addProperty("border-top-color", ThemeColors.object1, 0.95);
        style.addProperty("border-right-color", ThemeColors.object1, 0.1);
        style.addProperty("border-bottom-color", ThemeColors.object1, 0.1);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-ToggleButton-down-hovering");
        style = new Style(buttonEx);
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColors.object1, 0.8);
        style.addProperty("border-left-color", ThemeColors.object1, 0.95);
        style.addProperty("border-top-color", ThemeColors.object1, 0.95);
        style.addProperty("border-right-color", ThemeColors.object1, 0.1);
        style.addProperty("border-bottom-color", ThemeColors.object1, 0.1);
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
        // Edit AnchorButton: 
        buttonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX, StyleSuffixEx.EditButton);
        style = new Style(buttonEx);
        style.addProperty("color", "white");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        //
        // Action AnchorButton: 
        buttonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX, StyleSuffixEx.ActionButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.object1, 0.85);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
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

    protected void initHeadersStyle() {

        String prefix = CrmTitleBar.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColors.object1, 1);
        style.addProperty("color", ThemeColors.object1, 0.1);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmTitleBar.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmTitleBar.StyleSuffix.Breadcrumb));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.1em");
        addStyle(style);

        prefix = CrmActionsBarDecorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmActionsBarDecorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

    }

    protected void initSectionSeparatorStyle() {
        String prefix = CrmSectionSeparator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #D8D8D8");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmSectionSeparator.StyleSuffix.Caption));
        style.addProperty("padding", "0.5em 1em 0.5em 1.5em");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.object1, 0.4);
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        addStyle(style);

    }

    protected void initVistaTabStyles() {
        String prefix = VistaTabLayoutPanel.TAB_DIASBLED_STYLE;

        Style style = new Style(Selector.valueOf(prefix));
//        style.addProperty("background-color", ThemeColor.DISABLED_TEXT_BACKGROUND);
        style.addProperty("color", ThemeColors.foreground, 0.55);
        style.addProperty("cursor", "default");
        addStyle(style);
    }

    protected void initVistaDecoratorsPanelStyles() {
        String prefix = VistaDecoratorsFlowPanel.DEFAULT_STYLE_NAME;

        Style style = new Style(Selector.valueOf(prefix));

        /*
         * anchors within the class:
         */
        style = new Style(Selector.valueOf(prefix + " a:link, " + prefix + " a:visited, " + prefix + " a:active"));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.object1, 0.85);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        // internal scroll panel:
        style = new Style(Selector.valueOf(CrmScrollPanel.DEFAULT_STYLE_PREFIX));
        style.addProperty("padding", "1em");
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

    protected void initListerStyles() {
        String prefix = ListerBase.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.actionsPanel));
        style.addProperty("background-color", ThemeColors.foreground, 0.20);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("height", defaultActionBarHeight + "em");
//        style.addProperty("margin-top", "0.5em");
//        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.filtersPanel));
        style.addProperty("background-color", ThemeColors.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.listPanel));
        addStyle(style);

        String buttonEx = Selector.valueOf("gwt-Button", ListerBase.StyleSuffix.newItemButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-Button", ListerBase.StyleSuffix.actionButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.foreground, 0.15);
        style.addProperty("background-color", ThemeColors.foreground, 1.1);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 1.3);

        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColors.foreground, 1);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector));
        style.addProperty("background-color", ThemeColors.foreground, 0.6);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        String selectorPrefix = Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector);
        style = new Style(selectorPrefix + " a:link, " + selectorPrefix + " a:visited, " + selectorPrefix + " a:active");
        style.addProperty("color", ThemeColors.foreground, 0.1);
        addStyle(style);

        style = new Style(selectorPrefix + ":hover");
        style.addProperty("background-color", ThemeColors.foreground, 0.8);
        style.addProperty("color", ThemeColors.foreground, 0.1);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnMenu));
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("color", ThemeColors.foreground, 0.9);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.9);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColors.foreground, 0.15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.selected));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColors.foreground, 0.2);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.foreground, 0.3);
        addStyle(style);
    }
}