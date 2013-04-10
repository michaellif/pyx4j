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

import com.propertyvista.common.client.theme.VistaTheme;

public class FieldTheme extends VistaTheme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewHeader, SiteMainArea, SiteViewDisplay, LoginInputField, LoginViewPanel, LoginViewSectionHeader, LoginViewSectionContent, LoginViewSectionFooter, LoginOrLineSeparator, LoginCaption, LoginCaptionText, LoginCaptionTextEmph, LoginButton, LoginButtonHolder, AppSelectionButton, Toolbar, ToolbarImage, ToolbarImageHolder, ToolbarLabel, BuildingLister, BuildingDetails, MenuScreen, OverlapScreenNormal, OverlapScreenShifted, SortPanel, SearchPanel, SearchPanelToolbar, SearchResults;
    }

    public FieldTheme() {
        initStyles();
    }

    protected void initStyles() {
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
        style.addProperty("width", "20em");
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
        style.addProperty("border-color", "gray");

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
        style.addProperty("width", "20em");
        style.addProperty("text-align", "center");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.AppSelectionButton);
        style.addProperty("border", "1px solid");
        style.addProperty("height", "20px");
        style.addProperty("width", "20em");
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
    }

}