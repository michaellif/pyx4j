/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

import com.propertyvista.portal.web.client.resources.PortalImages;

public class PortalWebRootPaneTheme extends Theme {

    public static enum StyleName implements IStyleName {
        PageHeader, PageFooter, PageFooterFollowUs,

        MainToolbar, BrandImage,

        MainMenu, MainMenuHolder, MainMenuNavigItem, MainMenuLabel, MainMenuIcon, MainMenuHeader, MainMenuHeaderPhoto, MainMenuHeaderName, MainMenuFooter,

        Comm, CommContent, CommCallout, CommHeader, CommHeaderTitle, CommHeaderWriteAction, CommMessage,

        ExtraGadget, ExtraGadgetItem, ExtraGadgetItemTitle,

        NotificationContainer, NotificationItem, NotificationItemTitle, NotificationItemCloseButton;
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, active, sideMenu, collapsedMenu, sideComm, error, warning, info, confirm
    }

    public PortalWebRootPaneTheme() {
        initHeaderStyles();
        initMainToolbarStyles();
        initMainMenuStyles();
        initFooterStyles();
        initExtraGadgetStyles();
        initNotificationStyles();
        initCommunicationStyles();

    }

    private void initHeaderStyles() {
        Style style = new Style(".", StyleName.PageHeader);
        addStyle(style);
    }

    private void initMainToolbarStyles() {
        Style style = new Style(".", StyleName.MainToolbar);
        style.addProperty("position", "relative");
        style.addProperty("width", "100%");
        style.addProperty("min-width", "320px");
        style.addProperty("height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("height", "2.6em");
        style.addGradient(ThemeColor.foreground, 1, ThemeColor.foreground, 0.95);
        style.addProperty("background", ThemeColor.foreground, 0.7);
        style.addProperty("border-color", ThemeColor.foreground, 0.75);
        style.addProperty("border-radius", "5px");
        style.addProperty("margin", "0  0 0 10px");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.ButtonText);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("line-height", "2.6em");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.Toolbar);
        style.addProperty("margin", "8px 10px 0 8px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", DefaultWidgetsTheme.StyleName.ToolbarItem);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(".", StyleName.BrandImage);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

    }

    private void initMainMenuStyles() {
        Style style = new Style(".", StyleName.MainMenu);
        style.addProperty("width", "14em");
        style.addProperty("margin", "10px 0 ");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.collapsedMenu);
        style.addProperty("width", "auto");
        style.addProperty("height", "auto");
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.collapsedMenu, " .", StyleName.MainMenuLabel);
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuNavigItem);
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("line-height", "34px");
        style.addProperty("height", "34px");
        style.addProperty("white-space", "nowrap");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "5px");
        style.addProperty("list-style", "none");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-bottom-width", "0px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuNavigItem, ":first-child");
        style.addProperty("border-radius", "5px 5px 0 0");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuNavigItem, ":last-child");
        style.addProperty("border-bottom-width", "1px");
        style.addProperty("border-radius", "0 0 5px 5px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuNavigItem, "-", StyleDependent.active);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.MainMenuNavigItem, ":hover");
        style.addProperty("background", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.MainMenuLabel);
        style.addProperty("float", "left");
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuIcon);
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.collapsedMenu, " .", StyleName.MainMenuNavigItem, ":hover .", StyleName.MainMenuLabel);
        style.addProperty("display", "inline");
        style.addProperty("position", "absolute");
        style.addProperty("margin-top", "-6px");
        style.addProperty("padding", "5px 5px 5px 10px");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-left-width", "0px");
        style.addProperty("border-radius", "0 5px 5px 0");
        style.addProperty("background", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.sideMenu);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.sideMenu, " .", StyleName.MainMenuNavigItem);
        style.addProperty("border-radius", "0");
        style.addProperty("border-left-width", "0");
        style.addProperty("border-right-width", "0");
        style.addProperty("border-color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.sideMenu, " .", StyleName.MainMenuNavigItem, ":first-child");
        style.addProperty("border-top-width", "0");
        addStyle(style);

        style = new Style(".", StyleName.MainMenu, "-", StyleDependent.sideMenu, " .", StyleName.MainMenuNavigItem, ":last-child");
        style.addProperty("border-bottom-width", "0");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuHeader);
        style.addProperty("height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuHeaderPhoto);
        style.addProperty("height", "34px");
        style.addProperty("margin", "13px 5px 0 5px");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuHeaderName);
        style.addProperty("line-height", "60px");
        style.addProperty("margin-left", "5px");
        style.addProperty("vertical-align", "top");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.MainMenuFooter, " .", StyleName.MainMenuNavigItem);
        style.addProperty("background", ThemeColor.foreground, 0.6);
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("border-color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.MainMenuFooter, " .", StyleName.MainMenuNavigItem, ":hover");
        style.addProperty("background", ThemeColor.foreground, 0.5);
        addStyle(style);

    }

    private void initFooterStyles() {
        Style style = new Style(".", StyleName.PageFooter);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.PageFooterFollowUs, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("height", "30px");
        style.addProperty("line-height", "30px");
        style.addProperty("display", "block");
        addStyle(style);

    }

    private void initExtraGadgetStyles() {
        Style style = new Style(".", StyleName.ExtraGadget);
        style.addProperty("width", "220px");
        addStyle(style);

        style = new Style(".", StyleName.ExtraGadgetItem);
        style.addProperty("margin", "10px 10px 0 0");
        style.addProperty("padding", "10px ");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "5px");
        style.addProperty("background", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.ExtraGadgetItemTitle);
        style.addProperty("font-weight", "bold");
        addStyle(style);

    }

    private void initCommunicationStyles() {
        Style style = new Style(".", StyleName.Comm);
        style.addProperty("width", "400px");
        style.addBoxShadow(ThemeColor.foreground, "5px 5px 5px");
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("border-radius", "5px");
        style.addProperty("border-width", "1px");
        style.addProperty("border-color", ThemeColor.foreground, 0.8);
        style.addProperty("border-style", "solid");
        style.addProperty("margin-top", "15px");
        addStyle(style);

        style = new Style(".", StyleName.CommContent);
        style.addProperty("max-height", "400px");
        style.addProperty("height", "400px");
        addStyle(style);

        style = new Style(".", StyleName.CommCallout);
        style.addProperty("fill", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.CommHeader);
        style.addProperty("line-height", "60px");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        addStyle(style);

        style = new Style(".", StyleName.CommHeaderTitle);
        style.addProperty("margin-left", "5px");
        style.addProperty("vertical-align", "top");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.CommHeaderWriteAction);
        style.addProperty("margin", "17px 15px 0 5px");
        style.addProperty("vertical-align", "top");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.CommMessage);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("padding", "5px");
        style.addProperty("width", "100%");
        style.addProperty("border-bottom-width", "1px");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.8);
        style.addProperty("border-bottom-style", "solid");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm);
        style.addProperty("width", "100%");
        style.addProperty("height", "100%");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        style.addProperty("border-radius", "0");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm, " .", StyleName.CommContent);
        style.addProperty("max-height", "none");
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.Comm, "-", StyleDependent.sideComm, " .", StyleName.CommHeaderTitle);
        style.addProperty("line-height", "60px");
        addStyle(style);

    }

    private void initNotificationStyles() {
        Style style = new Style(".", StyleName.NotificationContainer);
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NotificationItem);
        style.addProperty("text-align", "center");
        style.addProperty("padding", "6px 6px 6px 50px");
        style.addProperty("min-height", "40px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-radius", "5px");
        style.addProperty("margin", "0 10px 4px 10px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".", StyleName.NotificationItem, "-" + StyleDependent.error);
        style.addProperty("background", "url('" + PortalImages.INSTANCE.error().getSafeUri().asString() + "') no-repeat scroll 10px center");
        style.addProperty("background-color", ThemeColor.contrast3, 0.3);
        style.addProperty("border-color", ThemeColor.contrast3, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NotificationItem, "-" + StyleDependent.warning);
        style.addProperty("background", "url('" + PortalImages.INSTANCE.warning().getSafeUri().asString() + "') no-repeat scroll 10px center");
        style.addProperty("background-color", ThemeColor.contrast2, 0.3);
        style.addProperty("border-color", ThemeColor.contrast2, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NotificationItem, "-" + StyleDependent.info);
        style.addProperty("background", "url('" + PortalImages.INSTANCE.info().getSafeUri().asString() + "') no-repeat scroll 10px center");
        style.addProperty("background-color", ThemeColor.contrast5, 0.3);
        style.addProperty("border-color", ThemeColor.contrast5, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NotificationItem, "-" + StyleDependent.confirm);
        style.addProperty("background", "url('" + PortalImages.INSTANCE.confirm().getSafeUri().asString() + "') no-repeat scroll 10px center");
        style.addProperty("background-color", ThemeColor.contrast4, 0.3);
        style.addProperty("border-color", ThemeColor.contrast4, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NotificationItemTitle);
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.NotificationItemCloseButton);
        style.addProperty("display", "inline-block");
        style.addProperty("position", "absolute");
        style.addProperty("right", "5px");
        style.addProperty("top", "5px");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
