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

import java.util.List;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.CaptionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.SecondNavigViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.payment.PaymentViewForm;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.AgreeFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.LeaseTemsFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SignatureFolder;

public class PtAppTheme extends VistaTheme {

    public PtAppTheme() {
        initStyles();
    }

    protected void initStyles() {
        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new FlexFormPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.object1;
            }
        });
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new PtAppSitePanelTheme());
        addTheme(new NewPaymentMethodEditorTheme());

        initBodyStyles();
        initCellListStyle();
        initEntityFolderStyles();

        initVistaCaptionViewStyles();
        initVistaMainNavigViewStyles();
        initVistaSecondNavigViewStyles();

        initSummaryStepViewStyles();
        initPaymentStepViewStyles();
        initMenuBarStyles();

        initSuggestBoxStyle();

        if (VistaTODO.enableWelcomeWizardDemoMode) {
            //initWizardDemoFont();
            initDisclosurePanelStyles();
        }
    }

    private void initWizardDemoFont() {
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            List<Style> styles = getStyles(".gwt-HTML");
            String fonts = "\"Comic Sans MS\", Arial";
            for (Style style : styles) {
                style.addProperty("font-family", fonts);
            }
            if (styles.isEmpty()) {
                Style style = new Style(".gwt-HTML");
                style.addProperty("font-family", fonts);
                addStyle(style);
            }
            {
                Style style = new Style("body");
                style.addProperty("font-family", fonts);
                addStyle(style);
            }
        }
    }

    private void initDisclosurePanelStyles() {
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            {
                Style style = new Style(".gwt-DisclosurePanel");
                style.addProperty("border-style", "outset");
                style.addProperty("border-width", "1px");
                style.addProperty("border-radius", "10px");
                style.addProperty("padding-left", "1em");
                style.addProperty("padding-right", "1em");
                style.addProperty("padding-top", "5px");
                style.addProperty("margin-bottom", "5px");
                addStyle(style);
            }
            {
                Style style = new Style(".gwt-DisclosurePanel .header");
                style.addProperty("color", ThemeColor.object1);
                style.addProperty("font-size", "18px");
                style.addProperty("font-weight", "bold");
                style.addProperty("text-decoration", "none");
                addStyle(style);
            }

        }
    }

    protected void initEntityFolderStyles() {
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.object1;
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
    }

    private void initVistaCaptionViewStyles() {
        String prefix = CaptionViewImpl.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix, CaptionViewImpl.StyleSuffix.Label));
        style.addProperty("color", "#FFFFFF");
        style.addProperty("font-size", "26px");
        style.addProperty("font-weight", "normal");
        style.addProperty("line-height", "1em");
        style.addProperty("margin-top", "10px");
        style.addProperty("height", "1em");
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
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);
    }

    protected void initSummaryStepViewStyles() {
        // Lease Terms:
        String prefix = LeaseTemsFolder.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, LeaseTemsFolder.StyleSuffix.Content));
        style.addProperty("font-weight", "normal");

        style.addProperty("color", "black");
        style.addProperty("background-color", "white"); // synch with LeaseTemsFolder.StyleSuffix.Scroll!

        style.addProperty("padding-left", "0.5em");
        style.addProperty("padding-right", "0.5em");

        addStyle(style);

        style = new Style(Selector.valueOf(prefix, LeaseTemsFolder.StyleSuffix.Agrees));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, LeaseTemsFolder.StyleSuffix.Scroll));
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-color", "#bbb");

        style.addProperty("background-color", "white"); // synch with LeaseTemsFolder.StyleSuffix.Content!

        style.addProperty("height", "20em");
        addStyle(style);

        prefix = AgreeFolder.DEFAULT_STYLE_PREFIX;

        style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AgreeFolder.StyleSuffix.Person));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, AgreeFolder.StyleSuffix.Agree));
        addStyle(style);

        // Signatures:
        prefix = SignatureFolder.DEFAULT_STYLE_PREFIX;

        style = new Style(Selector.valueOf(prefix));
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SignatureFolder.StyleSuffix.Label));
        style.addProperty("color", "#fff");
        style.addProperty("font-size", "1.2em");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        style.addProperty("text-shadow", "0 -1px 0 #333333");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SignatureFolder.StyleSuffix.Edit));
        addStyle(style);
    }

    private void initPaymentStepViewStyles() {
        // Lease Terms:
        String prefix = PaymentViewForm.DEFAULT_STYLE_PREFIX;

        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.oneTimePaymentTerms));
        style.addProperty("color", "black");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PaymentViewForm.StyleSuffix.recurrentPaymentTerms));
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-color", "#bbb");

        style.addProperty("font-weight", "normal");

        style.addProperty("background-color", "white");
        style.addProperty("color", "black");

        style.addProperty("padding-left", "0.5em");
        style.addProperty("padding-right", "0.5em");

        style.addProperty("height", "20em");
        addStyle(style);
    }

}