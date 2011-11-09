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

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.NativeRadioGroup;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.widgets.client.ListBox;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.PtAppSiteView;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentViewForm;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SummaryViewForm;

public class PtAppTheme extends VistaTheme {

    public PtAppTheme() {
        initStyles();
    }

    protected void initStyles() {

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetDecoratorTheme());

        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.object1;
            }
        });

        initEntityFolderStyles();

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

    protected void initEntityFolderStyles() {
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.object1;
            }
        });

        Style style = new Style((IStyleName) DefaultEntityFolderTheme.StyleName.EntityFolder);
        style.addProperty("width", "690px");
        addStyle(style);
    }

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style(".body-nonavig");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.body2Background().getSafeUri().asString() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);
        style = new Style(".body-navig");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.bodyBackground().getSafeUri().asString() + "') repeat-x 0 0 #F7F7F7");
        addStyle(style);

    }

    @Override
    protected void initListBoxStyle() {
        super.initListBoxStyle();
        Style style = new Style(Selector.valueOf(ListBox.DEFAULT_STYLE_PREFIX) + " option");
        style.addProperty("background-color", "white");
        addStyle(style);
    }

    protected void initSiteViewStyles() {
        String prefix = PtAppSiteView.DEFAULT_STYLE_PREFIX;

        int minWidth = 930;
        int maxWidth = 930;
        int leftColumnWidth = 0;
        int rightColumnWidth = 0;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Header));
        style.addProperty("height", "115px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Caption));
        style.addProperty("width", "30%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.SecondaryNavig));
        style.addProperty("width", "70%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Message));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Content));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Footer));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE1);
        style.addProperty("clear", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PtAppSiteView.StyleSuffix.Display));
        addStyle(style);

        style = new Style(Selector.valueOf(VistaHeaderBar.DEFAULT_STYLE_PREFIX));
        style.addProperty("background-color", ThemeColors.OBJECT_TONE4);
        style.addProperty("margin", "0.4em 0 0.4em 0");
        style.addProperty("width", maxWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaHeaderBar.DEFAULT_STYLE_PREFIX, VistaHeaderBar.StyleSuffix.Caption));
        style.addProperty("padding", "0.3em 1em 0.4em 1em");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.OBJECT_TONE4);
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
        style.addProperty("color", ThemeColors.OBJECT_TONE5);
        addStyle(style);

    }

    protected void initVistaApartmentViewStyles() {

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
        style.addProperty("background", "url('" + PortalImages.INSTANCE.step().getSafeUri().asString() + "') no-repeat scroll 0 0 transparent");
        style.addProperty("height", "57px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.latest));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepLatest().getSafeUri().asString() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepValid().getSafeUri().asString() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Tab, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepInvalid().getSafeUri().asString() + "') no-repeat scroll 0 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointer().getSafeUri().asString() + "') no-repeat scroll 100% 0 transparent");
        style.addProperty("margin-right", "-14px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.latest));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerLatest().getSafeUri().asString() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerValid().getSafeUri().asString() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.LabelHolder, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.stepPointerInvalid().getSafeUri().asString() + "') no-repeat scroll 100% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder));
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.complete));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.check().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.complete) + ":hover");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.checkHover().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warning().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.StatusHolder, MainNavigViewImpl.StyleDependent.invalid) + ":hover");
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warningHover().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, MainNavigViewImpl.StyleSuffix.Label, MainNavigViewImpl.StyleDependent.current));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.pointer().getSafeUri().asString() + "') no-repeat scroll 50% 100% transparent");
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
        style.addProperty("background", "url('" + PortalImages.INSTANCE.step2().getSafeUri().asString() + "') no-repeat scroll 0 0 transparent");
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
        style.addProperty("background", "url('" + PortalImages.INSTANCE.check().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.complete) + ":hover");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SecondNavigViewImpl.StyleSuffix.StatusHolder, SecondNavigViewImpl.StyleDependent.invalid));
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warning().getSafeUri().asString() + "') no-repeat scroll 50% 0 transparent");
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
        style.addProperty("color", ThemeColors.TEXT);
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