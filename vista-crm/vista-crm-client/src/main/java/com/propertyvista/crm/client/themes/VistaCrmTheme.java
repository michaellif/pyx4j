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
import com.pyx4j.widgets.client.dashboard.CSSNames;
import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.CrmView;
import com.propertyvista.crm.client.ui.NavigViewImpl;
import com.propertyvista.crm.client.ui.SearchBox;
import com.propertyvista.crm.client.ui.SearchBox.StyleSuffix;
import com.propertyvista.crm.client.ui.ShortCutsViewImpl;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public abstract class VistaCrmTheme extends VistaTheme {

    public static enum StyleSuffixEx implements IStyleSuffix {
        SaveButton, CancelButton;
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

        initGwtButtonStylesEx();
        initHeadersStyle();
        initDashboardReport();
        initEntityDataTableStyles();
    }

    @Override
    protected void initThemeColors() {
        float hue = (float) 213 / 360;
        float saturation = (float) 0.9;
        float brightness = (float) 0.7;
        putThemeColor(ThemeColor.OBJECT_TONE1, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.08));
        putThemeColor(ThemeColor.OBJECT_TONE2, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.12));
        putThemeColor(ThemeColor.OBJECT_TONE3, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.16));
        putThemeColor(ThemeColor.OBJECT_TONE4, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.2));
        putThemeColor(ThemeColor.OBJECT_TONE5, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.99));
        putThemeColor(ThemeColor.BORDER, 0xf0f0f0);
        putThemeColor(ThemeColor.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xeeeeee);
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
        style.addProperty("border-left", "ridge  #444");
//        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("cursor", "col-resize");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-VDragger");
        style.addProperty("border-top", "ridge  #444");
//        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("cursor", "row-resize");
        addStyle(style);

        /**
         * SearchBox style
         * TODO finish it up and move to appropriate module
         * 
         */
        style = new Style(Selector.valueOf(SearchBox.DEFAULT_STYLE_NAME));
        style.addProperty("background", "#ffffff");
        style.addProperty("overflow", "hidden");
        style.addProperty("border", "none");
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("min-width", "8em !important");
        style.addProperty("white-space", "normal !important");
        style.addProperty("padding-left", "0.7em !important");
        addStyle(style);
        //textbox
        style = new Style(Selector.valueOf(SearchBox.DEFAULT_STYLE_NAME, StyleSuffix.Text));
        style.addProperty("border-radius", "15px");
        style.addProperty("-moz-border-radius", "15px");
        style.addProperty("border", "none");
        //  style.addProperty("min-width", "4em !important");

        addStyle(style);
        //trigger
        style = new Style(Selector.valueOf(SearchBox.DEFAULT_STYLE_NAME, StyleSuffix.Trigger));
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

        style = new Style(Selector.valueOf(NavigViewImpl.DEFAULT_STYLE_PREFIX, NavigViewImpl.StyleSuffix.NoBottomMargin));
        style.addProperty("margin-bottom", "0 !important");
        addStyle(style);

        /**
         * horizontal alignment for blocks
         */
        style = new Style(".pyx4j-horizontal-align-center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
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

        Style style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Content));
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // HeaderStyle:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE95);
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // FooterStyle:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.Footer));
        style.addProperty("padding-top", "0.5em");
        style.addProperty("background", "url('" + CrmImages.INSTANCE.logo().getURL() + "') no-repeat scroll left center transparent");

        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("text-align", "center");
        addStyle(style);

        // DisplayStyle:
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#757575");//TODO define constant for the font in accordance with the spec
        addStyle(style);

        // LeftMenuContainer:
        style = new Style(Selector.valueOf(prefix, CrmView.StyleSuffix.NavigContainer));
        //style.addProperty("min-height", "400px");
        addStyle(style);

        // ActionStyle:
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
        //anchors within the class
        style = new Style(Selector.valueOf(prefix + " a:link, a:visited, a:active"));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);
        /**
         * components within the class
         */
        //stack header
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader"));
        style.addProperty("font-size", "1.3em");
        style.addProperty("padding-top", "0.2em");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("cursor", "pointer");
        style.addProperty("color", "DimGray");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE35);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader", null, NavigViewImpl.StyleDependent.selected));
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE80);
        addStyle(style);

        //stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "1em");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE25);
        addStyle(style);

        //Item style defines anchor specific styling
        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Item));
        style.addProperty("margin-bottom", "0.3em");
        addStyle(style);
    }

    /**
     * TODO When the layout is finalized it might make sense to combine
     * Navigation and ShortCuts styling due to their similarity
     */
    protected void intitShortCutStyles() {
        String prefix = ShortCutsViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        //anchors within the class
        style = new Style(prefix + " a:link, a:visited, a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix + " a:hover"));
        style.addProperty("text-decoration", "underline");
        addStyle(style);
        /**
         * components within the class
         */
        //stack header
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelHeader"));
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "1em");
//        style.addProperty("cursor", "pointer");
        style.addProperty("color", ThemeColor.OBJECT_TONE90);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE15);
        //NOTE: must correspond with the header size defined by stackpanel
        style.addProperty("line-height", "2.2em");
        addStyle(style);

        //stack content
        style = new Style(Selector.valueOf(prefix + " .gwt-StackLayoutPanelContent"));
        style.addProperty("font-size", "1.1em");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE15);
        addStyle(style);

        //Item style defines anchor specific styling
        style = new Style(Selector.valueOf(prefix, NavigViewImpl.StyleSuffix.Item));
        style.addProperty("margin-bottom", "0.3em");
        addStyle(style);

        //Search line style
        style = new Style(Selector.valueOf(prefix, ShortCutsViewImpl.StyleSuffix.SearchBar));
        addStyle(style);
    }

    protected void initGwtButtonStylesEx() {
        String gwtButtonEx = Selector.valueOf("gwt-Button", StyleSuffixEx.SaveButton);
        Style style = new Style(gwtButtonEx);
        style.addProperty("color", ThemeColor.OBJECT_TONE10);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE80);
        addStyle(style);

        style = new Style(gwtButtonEx + ":hover");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE85);
        addStyle(style);

        //
        // AnchorButton: 
        gwtButtonEx = Selector.valueOf(AnchorButton.DEFAULT_STYLE_PREFIX);
        style = new Style(gwtButtonEx);
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style(gwtButtonEx + ":hover");
        style.addProperty("text-decoration", "underline");
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

        prefix = CrmHeader1Decorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", "#5E5E5E");
        style.addProperty("margin", "0.2em 0 1em 0");
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
        style.addProperty("margin", "0.2em 0 1em 0");
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #D8D8D8");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CrmHeader2Decorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.5em 1em 0.5em 1.5em");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bold");
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
        style.addProperty("border", "1px solid #aaa");
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
        style.addProperty("font", "caption");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#444");
        style.addProperty("height", "20px");
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
//        style.addProperty("cursor", "default");
        addStyle(style);
    }

    protected void initEntityDataTableStyles() {
        String prefix = DataTable.BASE_NAME;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin", "2px 0 2px 0");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Header));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE35);
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row));
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.even));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE15);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.odd));
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.nodetails));
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.Row, DataTable.StyleDependent.selected));
        style.addProperty("background-color", ThemeColor.SELECTION);
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, DataTable.StyleSuffix.ActionsBar));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE30);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.BORDER);
        addStyle(style);
    }
}