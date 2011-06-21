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
package com.propertyvista.portal.client.themes;

import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

import com.propertyvista.common.client.ui.decorations.VistaHeaderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.ui.LogoViewImpl;
import com.propertyvista.portal.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.client.ui.PortalView;
import com.propertyvista.portal.client.ui.TopRightActionsViewImpl;
import com.propertyvista.portal.client.ui.decorations.BasicCardDecorator;
import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;
import com.propertyvista.portal.client.ui.searchapt.ApartmentDetailsViewImpl;
import com.propertyvista.portal.client.ui.searchapt.CardPanel;
import com.propertyvista.portal.client.ui.searchapt.FloorplanDetailsViewImpl;
import com.propertyvista.portal.client.ui.searchapt.PropertyMapViewImpl;
import com.propertyvista.portal.client.ui.searchapt.RefineApartmentSearchForm;
import com.propertyvista.portal.client.ui.searchapt.SearchApartmentForm;

public abstract class PortalTheme extends com.propertyvista.common.client.theme.VistaTheme {

    public PortalTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initBodyStyles();
        initListBoxStyle();
        initDecoratorsStyles();
        initSiteViewStyles();
        initLogoViewStyles();
        initTopActionStyles();
        initVistaMainNavigViewStyles();
        initPropertyListStyles();
        initSearchPanelStyles();
        initRefineSearchStyles();
        initApartmentDetailsStyles();
        initCriteriaWidgetDecoratorStyles();
        initBaseFolderItemViewerDecoratorStyles();
        initCardPanelDecoratorStyle();
        initPropertiesMapWidgetStyle();
        initFloorplanDetailsStyles();
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
        putThemeColor(ThemeColor.BORDER, 0xe7e7e7);
        putThemeColor(ThemeColor.SELECTION, ColorFactory.HSBVtoRGB(hue, saturation, brightness, (float) 0.4));
        putThemeColor(ThemeColor.SELECTION_TEXT, 0xffffff);
        putThemeColor(ThemeColor.TEXT, 0x000000);
        putThemeColor(ThemeColor.TEXT_BACKGROUND, 0xffffff);
        putThemeColor(ThemeColor.DISABLED_TEXT_BACKGROUND, 0xfafafa);
        putThemeColor(ThemeColor.MANDATORY_TEXT_BACKGROUND, 0xe5e5e5);
        putThemeColor(ThemeColor.READ_ONLY_TEXT_BACKGROUND, 0xeeeeee);
        putThemeColor(ThemeColor.SEPARATOR, 0xcccccc);
    }

    @Override
    protected void initListBoxStyle() {
        super.initListBoxStyle();
        Style style = new Style(Selector.valueOf(ListBox.DEFAULT_STYLE_PREFIX), " option");
        style.addProperty("background-color", "white");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = PortalView.DEFAULT_STYLE_PREFIX;

        int minWidth = 960;
        int maxWidth = 960;
        int leftColumnWidth = 0;
        int rightColumnWidth = 0;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        String headerstyle = Selector.valueOf(prefix, PortalView.StyleSuffix.Header);
        style = new Style(headerstyle);
        style.addProperty("background-color", ThemeColor.OBJECT_TONE35);
        style.addProperty("font-size", "14px");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        style.addProperty("height", "115px");
        addStyle(style);

        //TODO  think of a better way
        style = new Style(headerstyle + " a:link, a:visited, a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("font-size", "14px");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

        style = new Style(headerstyle + " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("height", "9.2em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        //   style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Content));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.SecondaryNavig));
        style.addProperty("margin", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Footer));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("clear", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Display));
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColor.OBJECT_TONE4);
        style.addProperty("margin-bottom", "0.3em");
        style.addProperty("width", "400px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaWidgetDecorator.DEFAULT_STYLE_PREFIX + StyleSuffix.Label));
        style.addProperty("padding-top", "2px");
        addStyle(style);

        style = new Style(Selector.valueOf("logo"));
        style.addProperty("font-size", "30px");
        style.addProperty("line-height", "1.2em");
        style.addProperty("text-align", "center");
        style.addProperty("vertical-align", "middle");
        style.addProperty("display", "block");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

    }

    private void initLogoViewStyles() {
        String prefix = LogoViewImpl.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("font-size", "2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "20px");
        style.addProperty("padding-top", "20px");
        addStyle(style);

    }

    private void initTopActionStyles() {
        String prefix = TopRightActionsViewImpl.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TopRightActionsViewImpl.StyleSuffix.PhoneLabel));
        style.addProperty("font-size", "2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-right", "20px");
        style.addProperty("padding-top", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, TopRightActionsViewImpl.StyleSuffix.GreetingLabel));
        style.addProperty("font-weight", "bold");
        addStyle(style);
    }

    private void initVistaMainNavigViewStyles() {
        String prefix = MainNavigViewImpl.DEFAULT_STYLE_PREFIX;
        String secondPrefix = MainNavigViewImpl.SECONDARY_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "100%");
        style.addProperty("overflow", "hidden");
        style.addProperty("padding-bottom", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix));
        style.addProperty("width", "100%");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Holder));
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE35);
        style.addProperty("list-style", "none");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.Holder));
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("background-color", ThemeColor.OBJECT_TONE95);
        style.addProperty("list-style", "none");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE55);
        style.addProperty("margin-right", "4px");
        style.addProperty("border", "none");
        style.addProperty("border-top-left-radius", "8px");
        style.addProperty("border-top-rigth-radius", "8px");
        style.addProperty("-moz-border-radius-topleft", "8px");
        style.addProperty("-moz-border-radius-topright", "8px");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.Tab));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder));
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.LabelHolder));
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE95);
        style.addProperty("color", "white");
        style.addProperty("border-top-left-radius", "8px");
        style.addProperty("border-top-rigth-radius", "8px");
        style.addProperty("-moz-border-radius-topleft", "8px");
        style.addProperty("-moz-border-radius-topright", "8px");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.pointer().getURL() + "') no-repeat scroll 50% 100% transparent");
        style.addProperty("color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "50px");
        style.addProperty("line-height", "50px");
        style.addProperty("color", "white");
        style.addProperty("font-size", "14px");
        style.addProperty("font-style", "normal");
        //      style.addProperty("text-shadow", "0 -1px 0 #E6E6E6");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

        style = new Style(Selector.valueOf(secondPrefix, MainNavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "60px");
        style.addProperty("line-height", "60px");
        style.addProperty("color", "white");
        style.addProperty("font-size", "14px");
        style.addProperty("font-style", "normal");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

    }

    private void initPropertyListStyles() {
        String prefix = PropertyMapViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Left));
        style.addProperty("margin-right", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Center));
        addStyle(style);

    }

    private void initDecoratorsStyles() {
        String prefix = VistaHeaderDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("border-bottom", "dotted 1px");
        style.addProperty("padding-top", "30px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("margin-bottom", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, VistaHeaderDecorator.StyleSuffix.Caption));
        style.addProperty("font-size", "20px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        prefix = VistaWidgetDecorator.DEFAULT_STYLE_PREFIX;
        style = new Style(Selector.valueOf(prefix));
        addStyle(style);

    }

    private void initCriteriaWidgetDecoratorStyles() {
        String prefix = CriteriaWidgetDecorator.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("padding", "10px");
        addStyle(style);
    }

    private void initSearchPanelStyles() {
        String prefix = SearchApartmentForm.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("padding", "10px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.SEPARATOR);

        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SearchApartmentForm.StyleSuffix.Row));
        style.addProperty("margin-bottom", "15px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SearchApartmentForm.StyleSuffix.RowHeader));
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SearchApartmentForm.StyleSuffix.Element));
        style.addProperty("padding-right", "10px");
        addStyle(style);

    }

    private void initRefineSearchStyles() {
        String prefix = RefineApartmentSearchForm.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin-right", "5px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.SEPARATOR);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, RefineApartmentSearchForm.StyleSuffix.SearchHeader));
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("margin-left", "10px");
        style.addProperty("margin-right", "10px");
        style.addProperty("border-bottom", "dotted 1px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, RefineApartmentSearchForm.StyleSuffix.ButtonPanel));
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("margin-left", "10px");
        style.addProperty("margin-right", "10px");
        style.addProperty("border-top", "dotted 1px");
        style.addProperty("text-align", "center");
        addStyle(style);
    }

    private void initApartmentDetailsStyles() {
        String prefix = ApartmentDetailsViewImpl.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.Left));
        style.addProperty("margin-right", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.Center));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.Button));
        style.addProperty("font-size", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.PageHeader));
        style.addProperty("margin-top", "10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("font-size", "20px");
        addStyle(style);

    }

    private void initFloorplanDetailsStyles() {
        String prefix = FloorplanDetailsViewImpl.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, FloorplanDetailsViewImpl.StyleSuffix.Left));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, FloorplanDetailsViewImpl.StyleSuffix.Center));
        style.addProperty("font-size", "14px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, FloorplanDetailsViewImpl.StyleSuffix.Button));
        style.addProperty("font-size", "20px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, FloorplanDetailsViewImpl.StyleSuffix.PageHeader));
        style.addProperty("margin-top", "10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("font-size", "20px");
        addStyle(style);

    }

    private void initBaseFolderItemViewerDecoratorStyles() {
        String prefix = BasicCardDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.SEPARATOR);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.Menu));
        addStyle(style);

        //TODO change colors
        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.MenuItem));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

        //TODO  think of a better way
        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.MenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

        //TODO need prefix+dependent Selector.valueOf implementation
        style = new Style("." + prefix + "-" + BasicCardDecorator.StyleDependent.hover);
        //TODO Not sure how to combine ThemeColor.OBJECT_TONE with !important declaration
        style.addProperty("border-color", "#8699B9!important");
        style.addProperty("border", "solid 1px");
        addStyle(style);

    }

    private void initCardPanelDecoratorStyle() {
        String prefix = CardPanel.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CardPanel.StyleSuffix.Image));
        style.addProperty("border-color", "#cccccc!important");
        style.addProperty("border", "solid 1px");
        style.addProperty("padding", "2px");

        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CardPanel.StyleSuffix.Image) + " img");
        style.addProperty("max-width", "100%!important");
        style.addProperty("width", "100%!important");
        style.addProperty("height", "100%!important");
        style.addProperty("-moz-background-size", "100% 100%!important");
        style.addProperty("-webkit-background-size", "100% 100%!important");
        style.addProperty("-khtml-background-size", "100% 100%!important");
        style.addProperty("background-size", "100% 100%!important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CardPanel.StyleSuffix.Header));
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-bottom", "5px");
        style.addProperty("font-size", "16px");
        style.addProperty("margin-left", "30px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, CardPanel.StyleSuffix.Content));
        style.addProperty("font-size", "12px");
        addStyle(style);

    }

    private void initPropertiesMapWidgetStyle() {
        String prefix = PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage));
        style.addProperty("border-color", "#cccccc!important");
        style.addProperty("border", "solid 1px");
        style.addProperty("padding", "2px");

        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage) + " img");
        style.addProperty("max-width", "100%!important");
        style.addProperty("width", "100%!important");
        style.addProperty("height", "100%!important");
        style.addProperty("-moz-background-size", "100% 100%!important");
        style.addProperty("-webkit-background-size", "100% 100%!important");
        style.addProperty("-khtml-background-size", "100% 100%!important");
        style.addProperty("background-size", "100% 100%!important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardHeader));
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-bottom", "5px");
        style.addProperty("font-size", "12px");
        style.addProperty("margin-left", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardContent));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", ThemeColor.OBJECT_TONE95);
        addStyle(style);

    }
}
