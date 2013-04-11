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
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.VistaTheme;

public class FieldTheme extends VistaTheme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewHeader, SiteMainArea, SiteViewDisplay, LoginInputField, LoginViewPanel, LoginViewSectionHeader, LoginViewSectionContent, LoginViewSectionFooter, LoginOrLineSeparator, LoginCaption, LoginCaptionText, LoginCaptionTextEmph, LoginButton, LoginButtonHolder, AppSelectionButton, Toolbar, ToolbarImage, ToolbarImageHolder, ToolbarLabel, BuildingLister, BuildingDetails, MenuScreen, OverlapScreenNormal, OverlapScreenShifted, SortPanel, SearchPanel, SearchPanelToolbar, SearchResults, Dialog, DialogCaption, DialogResizer, DialogContent, FormFlexPanelH1, FormFlexPanelH1Label, FormFlexPanelH2, FormFlexPanelH2Label, FormFlexPanelH3, FormFlexPanelH3Label, FormFlexPanelH4, FormFlexPanelH4Label, FormFlexPanelActionWidget;
    }

    public FieldTheme() {
        initStyles();
        initDialogBoxStyles();

    }

    protected void initStyles() {

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.background;
            }
        });
        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });

        // All viewable area:
        Style style = new Style(".", StyleName.SiteView.name());
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(".", StyleName.SiteViewContent.name());
        addStyle(style);

        // Header:
        style = new Style(".", StyleName.SiteViewHeader.name());
        style.addProperty("width", "100%");
        style.addProperty("height", "10%");
        addStyle(style);

        // Main Area:
        style = new Style(".", StyleName.SiteMainArea.name());
        style.addProperty("width", "100%");
        style.addProperty("height", "90%");
        addStyle(style);

        //Login fields:
        style = new Style(".", StyleName.LoginInputField.name());
        style.addProperty("width", "6em");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
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
        style.addProperty("margin-top", "10%");
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

        style = new Style(".", StyleName.LoginOrLineSeparator);
        style.addProperty("position", "absolute");
        style.addProperty("top", "0%");
        style.addProperty("bottom", "0%");
        style.addProperty("left", "100%");
        style.addProperty("right", "100%");
        style.addProperty("border-color", "green !important");

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

        style = new Style(".", StyleName.BuildingLister);
        style.addProperty("background-color", "blue");
        addStyle(style);

        style = new Style(".", StyleName.BuildingDetails);
        style.addProperty("background-color", "black");
        addStyle(style);

        style = new Style(".", StyleName.Toolbar);
        addStyle(style);

        style = new Style(".", StyleName.ToolbarImage);
        style.addProperty("position", "relative");
        style.addProperty("left", "0");
        addStyle(style);

        style = new Style(".", StyleName.ToolbarImageHolder);
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", StyleName.ToolbarLabel);
        style.addProperty("position", "relative");
        style.addProperty("width", "50px");
        addStyle(style);

        style = new Style(".", StyleName.MenuScreen);
        style.addProperty("position", "fixed");
        style.addProperty("z-index", "-1");
        style.addProperty("background-color", "green");
        addStyle(style);

        style = new Style(".", StyleName.OverlapScreenNormal);
        style.addProperty("margin-left", "auto");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(".", StyleName.OverlapScreenShifted);
        style.addProperty("margin-left", "80%");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(".", StyleName.SortPanel);
        style.addProperty("margin-top", "5%");
        style.addProperty("width", "100%");
        style.addProperty("height", "50%");
        style.addProperty("background-color", "yellow");
        addStyle(style);

        style = new Style(".", StyleName.SearchPanel);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("background-color", "white");
        addStyle(style);

        style = new Style(".", StyleName.SearchPanelToolbar);
        addStyle(style);

        style = new Style(".", StyleName.SearchResults);
        style.addProperty("margin-top", "auto");
        style.addProperty("margin-bottom", "auto");
        style.addProperty("background-color", "#C0C0C0");
        addStyle(style);

        style = new Style(".", StyleName.Dialog);
        style.addProperty("background-color", ThemeColor.object1, 1);
        style.addProperty("width", "390px");
        style.addProperty("box-shadow", "10px 10px 5px rgba(0, 0, 0, 0.3)");
        addStyle(style);

        style = new Style(".", StyleName.DialogCaption);
        style.addProperty("background", ThemeColor.object1, 0.8);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "10px");
        style.addProperty("width", "380px");
        addStyle(style);

        style = new Style(".", StyleName.DialogResizer);
        style.addProperty("background", ThemeColor.object1, 0.8);
        style.addProperty("filter", "alpha(opacity=95)");
        style.addProperty("opacity", "0.95");
        addStyle(style);

        style = new Style(".", StyleName.DialogContent);
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("width", "380px");
        style.addProperty("height", "100%");
        style.addProperty("padding", "10px");
        addStyle(style);

    }

}