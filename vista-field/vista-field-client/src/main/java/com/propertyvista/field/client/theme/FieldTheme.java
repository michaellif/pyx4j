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
package com.propertyvista.field.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.richtext.DefaultRichTextEditorTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.DraggerMixin;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.common.client.theme.VistaTheme;

public class FieldTheme extends VistaTheme {

    public static enum StyleName implements IStyleName {
        LoginInputField, LoginViewPanel, VistaLogo, LoginViewSectionHeader, LoginViewSectionContent, LoginViewSectionFooter,

        LoginCaption, LoginCaptionText, LoginCaptionTextEmph, LoginButton, LoginButtonHolder, AppSelectionButton, Toolbar,

        AlertsScreenContent, AlertsInfo, SortPanel, Dialog, DialogCaption, DialogResizer, DialogContent, AlertView,

        PageHeader, PageFooter;
    }

    public FieldTheme() {
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
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultRichTextEditorTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new DefaultTabTheme());
        addTheme(new DefaultPaneTheme());

        addTheme(new DraggerMixin());

        addTheme(new BillingTheme());
        addTheme(new TransactionHistoryViewerTheme());

        //Overridden themes
        addTheme(new FieldDefaultPaneTheme());

        //Login fields:
        Style style = new Style(".", StyleName.LoginInputField.name());
        style.addProperty("width", "5em");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", StyleName.VistaLogo);
        style.addProperty("position", "relative");
        style.addProperty("text-align", "center");
        style.addProperty("maxHeight", "70px");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewPanel);
        style.addProperty("position", "relative");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionHeader.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "bottom");
        style.addProperty("text-align", "center");
        style.addProperty("margin-top", "50px");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionContent.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.LoginViewSectionFooter.name());
        style.addProperty("width", "100%");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.LoginCaption);
        style.addProperty("margin-top", "25px");
        style.addProperty("margin-bottom", "25px");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.LoginCaptionText);
        addStyle(style);

        style = new Style(".", StyleName.LoginCaptionTextEmph);
        addStyle(style);

        style = new Style(".", StyleName.LoginButtonHolder.name());
        style.addProperty("width", "10em");
        style.addProperty("text-align", "center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.AppSelectionButton);
        style.addProperty("border", "1px solid");
        style.addProperty("height", "20px");
        style.addProperty("width", "9em");
        style.addProperty("text-align", "center");
        style.addProperty("margin", "10px");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.Toolbar);
        style.addProperty("height", "50px");
        addStyle(style);

        style = new Style(".", StyleName.AlertsScreenContent);
        style.addProperty("margin-left", "15%");
        addStyle(style);

        style = new Style(".", StyleName.AlertsInfo);
        addStyle(style);

        style = new Style(".", StyleName.SortPanel);
        style.addProperty("width", "100%");
        style.addProperty("bottom", "0");
        style.addProperty("background-color", "yellow");
        addStyle(style);

        style = new Style(".", StyleName.PageHeader);
        style.addProperty("width", "100%");
        addStyle(style);

    }

}