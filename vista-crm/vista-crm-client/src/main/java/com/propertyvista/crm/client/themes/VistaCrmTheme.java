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

import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.site.client.ui.crud.ListerBase;
import com.pyx4j.widgets.client.dashboard.CSSNames;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.CrmView;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.SearchBox;
import com.propertyvista.crm.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;
import com.propertyvista.crm.client.ui.TopRightActionsViewImpl;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.crm.client.ui.decorations.CrmHeader0Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public abstract class VistaCrmTheme extends VistaTheme {

    public static double defaultHeaderHeight = 3;

    public static double defaultFooterHeight = 4;

    public static double defaultTabHeight = 2.7;

    public static enum StyleSuffixEx implements IStyleSuffix {
        SaveButton, CancelButton, EditButton;
    }

    public VistaCrmTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initGeneralStyles();
        initBodyStyles();

        initSiteViewStyles();
        intitNavigationStyles();
        intitShortCutStyles();

        initSearchBoxStyles();
        initButtonStylesEx();
        initHeadersStyle();
        initVistaTabStyles();
        initVistaDecoratorsPanelStyles();
        initDashboardView();
        initDashboardReport();
        initListerStyles();
        initEntityDataTableStyles();
    }

    @Override
    protected void initGeneralStyles() {
        super.initGeneralStyles();

        Style style = new Style("a");
        style.addProperty("color", "#333");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-HDragger");
        style.addProperty("background", "#ccc");
        style.addProperty("cursor", "col-resize");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-VDragger");
        style.addProperty("background", "#ccc");
        style.addProperty("cursor", "row-resize");
        addStyle(style);

        /*
         * horizontal alignment for blocks
         */
        style = new Style(".pyx4j-horizontal-align-center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
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

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style(".body-navig");
        style.addProperty("background", "url('" + CrmImages.INSTANCE.bodyBackground().getURL() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = CrmView.DEFAULT_STYLE_PREFIX;

        // All viewable area:
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Content));
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // Header:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE95);
        style.addProperty("color", "white");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // Footer:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Footer));
        style.addProperty("background", "url('" + CrmImages.INSTANCE.logo().getURL() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", "#ccc");
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.NavigContainer));
        style.addProperty("min-width", "100px");
        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Action));
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Action) + " td");
        style.addProperty("vertical-align", "middle !important");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        // anchors within the ActionBar:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Action) + " a:link, a:visited, a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.OBJECT_TONE20);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Action) + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }

    protected void intitNavigationStyles() {
        String prefix = NavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(prefix);

        /*
         * anchors within the class:
         */
        style = new Style(Selector.valueOf(prefix + " a:link, a:visited, a:active"));
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
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
        style.addProperty("color", "DimGray");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE40);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader", null, NavigViewImpl.StyleDependent.selected));
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE75);
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "1em");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE30);
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
        style = new Style(prefix + " a:link, a:visited, a:active");
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
        style.addProperty("color", ThemeColor.OBJECT_TONE90);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE20);
        // NOTE: must correspond with the header size defined by stackpanel
        style.addProperty("line-height", "2.2em");
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE20);
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
        style.addProperty("margin-right", "0.2em !important");
        addStyle(style);
    }

    protected void initButtonStylesEx() {
        String buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.SaveButton);
        Style style = new Style(buttonEx);
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE80);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
//        style.addProperty("background-color", ThemeColor.OBJECT_TONE85);
        addStyle(style);

        style = new Style(buttonEx + "[disabled]:hover");
        style.addProperty("border", "1px outset #555");

        //
        // default AnchorButton: 
        buttonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
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
        style.addProperty("color", ThemeColor.OBJECT_TONE15);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        //
        // Back2CRM link:
        buttonEx = Selector.valueOf(TopRightActionsViewImpl.BACK_TO_CRM);
        style = new Style(buttonEx);
        style.addProperty("color", "red");
        style.addProperty("background-color", "red");
        style.addProperty("border", "1px solid yellow");
        style.addProperty("font-weight", "bolder");
        style.addProperty("text-shadow", "2px -1px 0 #333");
//        style.addProperty("text-transform", "uppercase");
        style.addProperty("padding", "0.3em");
        addStyle(style);
    }

    protected void initHeadersStyle() {

        String prefix = CrmHeaderDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE60);
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmHeaderDecorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        prefix = CrmHeader0Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE20);
        style.addProperty("color", ThemeColor.OBJECT_TONE85);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmHeader2Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.5em 1em 0.6em 1.5em");
        style.addProperty("font-size", "1.2em");
//        style.addProperty("font-weight", "bold");
        addStyle(style);

        prefix = CrmHeader1Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #727171");
        style.addProperty("clear", "both");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmHeader1Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.6em 1em 0.1em 1em");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        prefix = CrmHeader2Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #D8D8D8");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmHeader2Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.5em 1em 0.5em 1.5em");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    protected void initVistaTabStyles() {
        String prefix = VistaTabLayoutPanel.TAB_DIASBLED_STYLE;

        Style style = new Style(Selector.valueOf(prefix));
//        style.addProperty("background-color", ThemeColor.DISABLED_TEXT_BACKGROUND);
        style.addProperty("color", ThemeColor.OBJECT_TONE55);
        style.addProperty("cursor", "default");
        addStyle(style);
    }

    protected void initVistaDecoratorsPanelStyles() {
        String prefix = VistaDecoratorsFlowPanel.DEFAULT_STYLE_NAME;

        Style style = new Style(prefix);

        /*
         * anchors within the class:
         */
        style = new Style(Selector.valueOf(prefix + " a:link, a:visited, a:active"));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.OBJECT_TONE85);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    protected void initDashboardView() {
        String prefix = DashboardViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DashboardViewImpl.StyleSuffix.actionsPanel));
        style.addProperty("margin-top", "0.5em");
        style.addProperty("margin-bottom", "0.5em");
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
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("border", "1px solid #ccc");
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.Holder, CSSNames.StyleDependent.maximized));
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderSetup));
        style.addProperty("background-color", ThemeColor.MANDATORY_TEXT_BACKGROUND);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CSSNames.StyleSuffix.HolderCaption));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE4);
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
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
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
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
        style.addProperty("margin-top", "0.5em");
        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.filtersPanel));
        style.addProperty("padding-top", "0.5em");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.listPanel));
        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE35);
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE60);
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + " a:link, a:visited, a:active");
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + ":hover");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE80);
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnMenu));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE10);
        style.addProperty("color", ThemeColor.OBJECT_TONE90);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.OBJECT_TONE90);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.selected));
        style.addProperty("background-color", ThemeColor.SELECTION);
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE30);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }
}