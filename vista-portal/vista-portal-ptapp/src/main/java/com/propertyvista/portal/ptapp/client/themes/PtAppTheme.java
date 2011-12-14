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
import com.pyx4j.forms.client.ui.DefaultCCOmponentsTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SignatureFolder;

public class PtAppTheme extends VistaTheme {

    public PtAppTheme() {
        initStyles();
    }

    protected void initStyles() {

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());

        addTheme(new DefaultWidgetDecoratorTheme());

        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.object1;
            }
        });

        initEntityFolderStyles();

        addTheme(new DefaultDatePickerTheme());
        addTheme(new PtAppSitePanelTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCCOmponentsTheme());

        addTheme(new NewPaymentMethodEditorTheme());

        initBodyStyles();
        initCellListStyle();

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
        style.addProperty("width", "100%");
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

        style = new Style(Selector.valueOf(VistaHeaderBar.DEFAULT_STYLE_PREFIX));
        style.addProperty("background-color", ThemeColors.object1, 0.4);
        style.addProperty("margin", "0.4em 0 0.4em 0");
        style.addProperty("width", "930px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.object1, 0.4);
        style.addProperty("margin-bottom", "0.3em");
        style.addProperty("width", "400px");
        addStyle(style);
    }

    protected void initVistaSummaryViewStyles() {
        String prefix = SignatureFolder.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, SignatureFolder.StyleSuffix.DigitalSignature));
        style.addProperty("color", ThemeColors.foreground);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SignatureFolder.StyleSuffix.DigitalSignatureLabel));
        style.addProperty("color", "#fff");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        style.addProperty("text-shadow", "0 -1px 0 #333333");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SignatureFolder.StyleSuffix.DigitalSignatureEdit));
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
        style.addProperty("color", ThemeColors.foreground);
        addStyle(style);
    }

    private void initPaymentRadioButtonGroupStyles() {
    }

}