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
package com.propertyvista.portal.ptapp.client.themes;

import com.pyx4j.forms.client.ui.NativeRadioGroup;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.style.ColorFactory;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.ThemeColor;

import com.propertyvista.common.client.ui.decorations.VistaHeaderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.ApartmentUnitsTable;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.PaymentViewForm;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.SiteView;
import com.propertyvista.portal.ptapp.client.ui.SummaryViewForm;

public abstract class VistaTheme extends com.propertyvista.common.client.theme.VistaTheme {

    public VistaTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        initBodyStyles();
        initListBoxStyle();

        initSiteViewStyles();
        initVistaApartmentViewStyles();
        initVistaSummaryViewStyles();
        initVistaCaptionViewStyles();
        initVistaMainNavigViewStyles();
        initVistaSecondNavigViewStyles();
        initPaymentRadioButtonGroupStyles();
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
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style(".body-nonavig");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.body2Background().getURL() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);
        style = new Style(".body-navig");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.bodyBackground().getURL() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);

    }

    @Override
    protected void initListBoxStyle() {
        super.initListBoxStyle();
        Style style = new Style(Selector.valueOf(ListBox.DEFAULT_STYLE_PREFIX), " option");
        style.addProperty("background-color", "white");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = SiteView.DEFAULT_STYLE_PREFIX;

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

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Header));
        style.addProperty("height", "115px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Caption));
        style.addProperty("width", "30%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.SecondaryNavig));
        style.addProperty("width", "70%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Message));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Content));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Footer));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE1);
        style.addProperty("clear", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Display));
        addStyle(style);

        style = new Style(Selector.valueOf(VistaHeaderDecorator.DEFAULT_STYLE_PREFIX));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE4);
        style.addProperty("margin", "0.4em 0 0.4em 0");
        style.addProperty("width", "960px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaHeaderDecorator.DEFAULT_STYLE_PREFIX, VistaHeaderDecorator.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
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

    protected void initVistaApartmentViewStyles() {
        String prefix = ApartmentUnitsTable.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitListHeader));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE5);
        style.addProperty("width", "700px");
        style.addProperty("height", "2em");
        style.addProperty("margin-top", "-10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitListHeader) + " td");
        style.addProperty("line-height", "2em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitRowPanel));
        style.addProperty("background-color", "auto");
        style.addProperty("border", "none");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitRowPanel, ApartmentUnitsTable.StyleDependent.selected));
        style.addProperty("background-color", ThemeColor.OBJECT_TONE2);
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-left", "1px solid #bbb");
        style.addProperty("border-right", "1px solid #bbb");
        style.addProperty("cursor", "default");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitRowPanel, ApartmentUnitsTable.StyleDependent.hover));
        style.addProperty("background-color", ThemeColor.SELECTION);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-left-color", ThemeColor.SELECTION);
        style.addProperty("border-right-color", ThemeColor.SELECTION);
        style.addProperty("color", ThemeColor.SELECTION_TEXT);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitDetailPanel));
        style.addProperty("border", "none");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, ApartmentUnitsTable.StyleSuffix.UnitDetailPanel, ApartmentUnitsTable.StyleDependent.selected));
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("border-left", "1px solid #bbb");
        style.addProperty("border-right", "1px solid #bbb");
        addStyle(style);

    }

    protected void initVistaSummaryViewStyles() {
        String prefix = SummaryViewForm.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, SummaryViewForm.StyleSuffix.DigitalSignature));
        style.addProperty("background-color", "#50585F");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SummaryViewForm.StyleSuffix.DigitalSignatureLabel));
        style.addProperty("color", "#fff");
        style.addProperty("font-size", "1.4em");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        style.addProperty("text-shadow", "0 -1px 0 #333333");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SummaryViewForm.StyleSuffix.DigitalSignatureEdit));
        addStyle(style);
    }

    private void initVistaCaptionViewStyles() {
        String prefix = CaptionViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, CaptionViewImpl.StyleSuffix.Label));
        style.addProperty("color", "#FFFFFF");
        style.addProperty("font-size", "26px");
        style.addProperty("font-weight", "normal");
        style.addProperty("line-height", "92px");
        style.addProperty("height", "68px");
        style.addProperty("text-transform", "uppercase");

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
        style.addProperty("background", "url('" + PortalImages.INSTANCE.step().getURL() + "') no-repeat scroll 0 0 transparent");
        style.addProperty("height", "57px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.latest));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepLatest().getURL() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepValid().getURL() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepInvalid().getURL() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointer().getURL() + "') no-repeat scroll 100% 0 transparent");
        style.addProperty("margin-right", "-14px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.latest));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerLatest().getURL() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerValid().getURL() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerInvalid().getURL() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.check().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.complete) + ":hover");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.checkHover().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warning().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.invalid) + ":hover");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warningHover().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.pointer().getURL() + "') no-repeat scroll 50% 100% transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label));
        style.addProperty("height", "57px");
        style.addProperty("line-height", "74px");
        style.addProperty("color", "#fff");
        style.addProperty("font-size", "15px");
        style.addProperty("font-style", "normal");
        style.addProperty("text-shadow", "0 -1px 0 #333333");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("padding-left", "29px");
        style.addProperty("padding-right", "29px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        //style.addProperty("background", "#654");
        addStyle(style);

    }

    private void initVistaSecondNavigViewStyles() {
        String prefix = SecondNavigViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "100%");
        style.addProperty("height", "50px");
        style.addProperty("margin-top", "7px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Holder));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Tab));
        style.addProperty("height", "46px");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.step2().getURL() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Tab, SecondNavigViewImpl.StyleDependent.current));
        style.addProperty("height", "50px");
        style.addProperty("background", "#F7F7F7"); // should be body colour!..
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Tab, SecondNavigViewImpl.StyleDependent.latest));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Tab, SecondNavigViewImpl.StyleDependent.complete));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Tab, SecondNavigViewImpl.StyleDependent.invalid));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.check().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.complete) + ":hover");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warning().getURL() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.invalid) + ":hover");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Label));
        style.addProperty("color", "#fff");
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-style", "normal");
        style.addProperty("padding-left", "1em");
        style.addProperty("padding-right", "1em");
        style.addProperty("padding-top", "2em");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.Label, SecondNavigViewImpl.StyleDependent.current));
        style.addProperty("color", ThemeColor.TEXT);
        addStyle(style);
    }

    private void initPaymentRadioButtonGroupStyles() {

        String prefix = PaymentViewForm.PAYMENT_BUTTONS_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("margin-top", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item));
        style.addProperty("width", "100%");
        style.addProperty("padding-top", "3px");
        style.addProperty("height", "27px");
        style.addProperty("border-top", "1px solid #F7F7F7");
        style.addProperty("border-bottom", "1px solid #F7F7F7");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item) + " input");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item) + " label");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, NativeRadioGroup.StyleSuffix.Item, NativeRadioGroup.StyleDependent.selected));
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentForm));
        style.addProperty("border-radius", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentImages));
        style.addProperty("margin-top", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentImages) + " div");
        style.addProperty("height", "27px");
        style.addProperty("padding-top", "5px");
        style.addProperty("padding-right", "10px");
        style.addProperty("padding-left", "10px");
        style.addProperty("border-top-left-radius", "3px");
        style.addProperty("border-bottom-left-radius", "3px");
        style.addProperty("border-left", "1px solid #F7F7F7");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentImages) + " div.selected");
        style.addProperty("padding-top", "4px");
        style.addProperty("height", "26px");
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("border-left", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentImages) + " div img");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentFee));
        style.addProperty("margin-top", "10px");
        style.addProperty("position", "relative");
        style.addProperty("z-index", "2");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentFee) + " div");
        style.addProperty("padding-right", "50px");
        style.addProperty("padding-top", "8px");
        style.addProperty("height", "24px");
        style.addProperty("padding-left", "50px");
        style.addProperty("padding-right", "20px");
        style.addProperty("z-index", "2");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.PaymentFee) + " div.selected");
        style.addProperty("padding-top", "7px");
        style.addProperty("height", "23px");
        style.addProperty("border-top", "1px solid #bbb");
        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("background-color", "white");
        addStyle(style);
    }

}