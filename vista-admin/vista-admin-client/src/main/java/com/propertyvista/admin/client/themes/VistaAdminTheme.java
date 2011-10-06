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
package com.propertyvista.admin.client.themes;

import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.site.client.ui.crud.ListerBase;
import com.pyx4j.widgets.client.style.IStyleName;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColors;

import com.propertyvista.admin.client.resources.AdminImages;
import com.propertyvista.admin.client.ui.AdminView;
import com.propertyvista.admin.client.ui.NavigViewImpl;
import com.propertyvista.admin.client.ui.SearchBox;
import com.propertyvista.admin.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.admin.client.ui.ShortCutsViewImpl;
import com.propertyvista.admin.client.ui.components.AnchorButton;
import com.propertyvista.admin.client.ui.decorations.AdminActionsBarDecorator;
import com.propertyvista.admin.client.ui.decorations.AdminHeader1Decorator;
import com.propertyvista.admin.client.ui.decorations.AdminHeader2Decorator;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;
import com.propertyvista.admin.client.ui.decorations.AdminScrollPanel;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;

public class VistaAdminTheme extends VistaTheme {

    public static double defaultHeaderHeight = 3;

    public static double defaultFooterHeight = 3;

    public static double defaultActionBarHeight = 2.9;

    public static double defaultTabHeight = 2.6;

    public static enum StyleSuffixEx implements IStyleName {
        SaveButton, CancelButton, EditButton, ActionButton;
    }

    public VistaAdminTheme() {
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
        style.addProperty("color", ThemeColors.TEXT);
        addStyle(style);
    }

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style(".body-navig");
        style.addProperty("background", "url('" + AdminImages.INSTANCE.bodyBackground().getSafeUri().asString() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = AdminView.DEFAULT_STYLE_PREFIX;

        // All viewable area:
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", ThemeColors.TEXT);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Content));
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // Header:
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE95);
        style.addProperty("color", "white");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // Footer:
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Footer));
        style.addProperty("background", "url('" + AdminImages.INSTANCE.logo().getSafeUri().asString() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", "#ccc");
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.NavigContainer));
        style.addProperty("min-width", "100px");
        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Action));
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Action) + " td");
        style.addProperty("vertical-align", "middle !important");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        // anchors within the ActionBar:
        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Action) + " a:link, a:visited, a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.OBJECT_TONE20);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminView.StyleSuffix.Action) + " a:hover");
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
        style.addProperty("background-color", ThemeColors.OBJECT_TONE35);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader", null, NavigViewImpl.StyleDependent.selected));
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "1em");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE25);
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
        style.addProperty("color", ThemeColors.OBJECT_TONE90);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
        // NOTE: must correspond with the header size defined by stackpanel
        style.addProperty("line-height", "2.2em");
        addStyle(style);

        // stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
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
 * style.addProperty("background", "url(" + AdminImages.INSTANCE.search().getURL() +
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
        //
        // Save Button: 
        String buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.SaveButton);
        Style style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
        addStyle(style);

        //
        // Edit Button: 
        buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.EditButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE85);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
        addStyle(style);

        //
        // Action Button: 
        buttonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.ActionButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE15);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
        addStyle(style);

        //
        // default AnchorButton: 
        buttonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
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
        style.addProperty("color", ThemeColors.OBJECT_TONE85);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bolder");
        addStyle(style);
    }

    protected void initHeadersStyle() {

        String prefix = AdminHeaderDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE60);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminHeaderDecorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        prefix = AdminActionsBarDecorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE40);
        style.addProperty("color", ThemeColors.OBJECT_TONE90);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminActionsBarDecorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        addStyle(style);

        prefix = AdminHeader1Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #727171");
        style.addProperty("clear", "both");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminHeader1Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.6em 1em 0.1em 1em");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        prefix = AdminHeader2Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #D8D8D8");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AdminHeader2Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.5em 1em 0.5em 1.5em");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("margin", "0.2em 0 0.5em 0");
        style.addProperty("width", "100%");
        addStyle(style);
    }

    protected void initVistaTabStyles() {
        String prefix = VistaTabLayoutPanel.TAB_DIASBLED_STYLE;

        Style style = new Style(Selector.valueOf(prefix));
//        style.addProperty("background-color", ThemeColor.DISABLED_TEXT_BACKGROUND);
        style.addProperty("color", ThemeColors.OBJECT_TONE55);
        style.addProperty("cursor", "default");
        addStyle(style);
    }

    protected void initVistaDecoratorsPanelStyles() {
        String prefix = VistaDecoratorsFlowPanel.DEFAULT_STYLE_NAME;

        Style style = new Style(Selector.valueOf(prefix));

        /*
         * anchors within the class:
         */
        style = new Style(Selector.valueOf(prefix + " a:link, a:visited, a:active"));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.OBJECT_TONE85);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        // internal scroll panel:
        style = new Style(Selector.valueOf(AdminScrollPanel.DEFAULT_STYLE_PREFIX));
        style.addProperty("padding", "1em");
        addStyle(style);
    }

    protected void initListerStyles() {
        String prefix = ListerBase.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.actionsPanel));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE40);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        style.addProperty("height", defaultActionBarHeight + "em");
//        style.addProperty("margin-top", "0.5em");
//        style.addProperty("margin-bottom", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.filtersPanel));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ListerBase.StyleSuffix.listPanel));
        addStyle(style);

        String buttonEx = Selector.valueOf("gwt-Button", ListerBase.StyleSuffix.newItemButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE85);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
        addStyle(style);

        buttonEx = Selector.valueOf("gwt-Button", ListerBase.StyleSuffix.actionButton);
        style = new Style(buttonEx);
        style.addProperty("color", ThemeColors.OBJECT_TONE15);
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        addStyle(style);

        style = new Style(buttonEx + ":hover");
        style.addProperty("border", "1px solid #555");
        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE35);
        style.addProperty("color", ThemeColors.OBJECT_TONE95);
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE60);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + " a:link, a:visited, a:active");
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnSelector) + ":hover");
        style.addProperty("background-color", ThemeColors.OBJECT_TONE80);
        style.addProperty("color", ThemeColors.OBJECT_TONE10);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ColumnMenu));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE10);
        style.addProperty("color", ThemeColors.OBJECT_TONE90);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.OBJECT_TONE90);
        style.addProperty("padding", "5px 7px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.selected));
        style.addProperty("background-color", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE30);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.BORDER);
        addStyle(style);
    }
}