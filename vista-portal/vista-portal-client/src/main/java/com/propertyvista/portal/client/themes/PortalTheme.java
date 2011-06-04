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

import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.portal.client.decorators.BasicCardDecorator;
import com.propertyvista.portal.client.decorators.CriteriaWidgetDecorator;
import com.propertyvista.portal.client.ui.ApartmentDetailsViewImpl;
import com.propertyvista.portal.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.client.ui.PortalView;
import com.propertyvista.portal.client.ui.PropertyMapViewImpl;
import com.propertyvista.portal.client.ui.RefineApartmentSearchForm;
import com.propertyvista.portal.client.ui.ResidentsNavigViewImpl;
import com.propertyvista.portal.client.ui.SearchApartmentForm;

import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

public abstract class PortalTheme extends com.propertyvista.common.client.theme.VistaTheme {

    public PortalTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initBodyStyles();
        initListBoxStyle();

        initSiteViewStyles();
        initVistaMainNavigViewStyles();
        initSubNavigViewStyles();
        initPropertyListStyles();
        initSearchPanelStyles();
        initRefineSearchStyles();
        initApartmentDetailsStyles();
        initCriteriaWidgetDecoratorStyles();
        initBaseFolderItemViewerDecoratorStyles();
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

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Header));
        style.addProperty("height", "115px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
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
        style.addProperty("color", ThemeColor.OBJECT_TONE5);
        addStyle(style);

    }

    private void initVistaMainNavigViewStyles() {
        String prefix = MainNavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "100%");
        style.addProperty("height", "57px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Holder));
        style.addProperty("height", "57px");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("list-style", "none");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab));
        //     style.addProperty("background", "url('" + PortalImages.INSTANCE.step().getURL() + "') no-repeat scroll 0 0 transparent");
        style.addProperty("height", "57px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder));
        // style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointer().getURL() + "') no-repeat scroll 100% 0 transparent");
        style.addProperty("margin-right", "-14px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        //  style.addProperty("background", "url('" + PortalImages.INSTANCE.pointer().getURL() + "') no-repeat scroll 50% 100% transparent");
        style.addProperty("color", "white !important");
        style.addProperty("background-color", "#7B8388");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "57px");
        style.addProperty("line-height", "74px");
        style.addProperty("color", "#7B8388");
        style.addProperty("font-size", "15px");
        style.addProperty("font-style", "normal");
        //      style.addProperty("text-shadow", "0 -1px 0 #E6E6E6");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        //style.addProperty("background", "#654");
        addStyle(style);

    }

    private void initSubNavigViewStyles() {
        String prefix = ResidentsNavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "100%");
        style.addProperty("height", "40px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.Holder));
        style.addProperty("height", "40px");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("list-style", "none");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.Tab));
        //     style.addProperty("background", "url('" + PortalImages.INSTANCE.step().getURL() + "') no-repeat scroll 0 0 transparent");
        style.addProperty("height", "40px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.LabelHolder));
        // style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointer().getURL() + "') no-repeat scroll 100% 0 transparent");
        style.addProperty("margin-right", "-14px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.Label, ResidentsNavigViewImpl.StyleDependent.current));
        //  style.addProperty("background", "url('" + PortalImages.INSTANCE.pointer().getURL() + "') no-repeat scroll 50% 100% transparent");
        style.addProperty("text-decoration", "underline !important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "40px");
        style.addProperty("line-height", "54px");
        style.addProperty("color", "#7B8388");
        style.addProperty("font-size", "15px");
        style.addProperty("font-style", "normal");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ResidentsNavigViewImpl.StyleSuffix.Label, ResidentsNavigViewImpl.StyleDependent.current));
        //style.addProperty("background", "#654");
        addStyle(style);

    }

    private void initPropertyListStyles() {
        String prefix = PropertyMapViewImpl.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.TableBody));
        style.addProperty("table-layout", "fixed");
        style.addProperty("border-spacing", "0px !important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Numerator));
        style.addProperty("font-size", "30px");
        style.addProperty("color", "#7B8388");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Header));
        style.addProperty("font-size", "15px");
        style.addProperty("color", "#7B8388");
        style.addProperty("text-align", "left");
        style.addProperty("height", "30px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Cell));
        style.addProperty("border-color", "#999999");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-width", "1px");
        style.addProperty("vertical-align", "top");
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.Search));
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertyMapViewImpl.StyleSuffix.SearchResult));
        style.addProperty("float", "right");
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
        style.addProperty("border", "1px solid #ccc");
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
        style.addProperty("border", "1px solid #ccc");
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
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.Center));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentDetailsViewImpl.StyleSuffix.PageHeader));
        style.addProperty("margin-top", "10px");
        style.addProperty("margin-bottom", "10px");
        style.addProperty("font-size", "20px");
        addStyle(style);

    }

    private void initBaseFolderItemViewerDecoratorStyles() {
        String prefix = BasicCardDecorator.DEFAULT_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", ThemeColor.BORDER);
        style.addProperty("border-bottom-color", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.Menu));
        addStyle(style);

        //TODO change colors
        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.MenuItem));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", "#7B8388");
        addStyle(style);

        //TODO  think of a better way
        style = new Style(Selector.valueOf(prefix, BasicCardDecorator.StyleSuffix.MenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", "#7B8388");
        addStyle(style);

        //TODO need prefix+dependent Selector.valueOf implementation
        style = new Style("." + prefix + "-" + BasicCardDecorator.StyleDependent.hover);
        style.addProperty("border-color", "#7B8388!important");
        style.addProperty("border", "solid 1px");
        addStyle(style);

    }
}
