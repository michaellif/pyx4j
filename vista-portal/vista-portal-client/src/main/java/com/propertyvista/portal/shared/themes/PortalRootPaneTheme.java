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
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

import com.propertyvista.portal.shared.resources.PortalImages;

public class PortalRootPaneTheme extends Theme {

    public static enum StyleName implements IStyleName {
        PageHeader, PageFooter, PageFooterFollowUs, PageFooterPmcInfo, PageFooterPmcInfoContent, PageFooterPmcInfoText,

        MainToolbar, BrandImage,

        MainMenu, MainMenuHolder, MainMenuNavigItem, MainMenuLabel, MainMenuIcon, MainMenuHeader, MainMenuHeaderPhoto, MainMenuHeaderName, MainMenuFooter,

        Comm, CommContent, CommCallout, CommHeader, CommHeaderTitle, CommHeaderWriteAction, CommMessage, AllertButton,

        ExtraGadget,

        NotificationContainer, NotificationItem, NotificationItemTitle, NotificationItemCloseButton,

        NotificationGadget, TermsGadget, TermsGadgetContent,

        MenuPointer, ButtonPointer;
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, active, sideMenu, collapsedMenu, sideComm, error, warning, info, confirm, alertOn
    }

    public PortalRootPaneTheme() {
        initHeaderStyles();
        initMainToolbarStyles();
        initMainMenuStyles();
        initFooterStyles();
        initExtraGadgetStyles();
        initHeaderNotificationStyles();
        initPageNotificationStyles();
        initCommunicationStyles();
        initTermsGadgetStyles();
        initPointerStyles();
    }

    private void initHeaderStyles() {
        Style style = new Style(".", StyleName.PageHeader);
        style.addProperty("max-width", "1200px");
        style.addProperty("margin", "auto");
        addStyle(style);
    }

    private void initMainToolbarStyles() {
        Style style = new Style(".", StyleName.MainToolbar);
        style.addProperty("position", "relative");
        style.addProperty("width", "100%");
        style.addProperty("min-width", "320px");
        style.addProperty("height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", WidgetTheme.StyleName.Toolbar);
        style.addProperty("margin", "8px 10px 0 8px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.MainToolbar, " .", WidgetTheme.StyleName.ToolbarItem);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(".", StyleName.BrandImage);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "top");
        addStyle(style);

    }

    protected void initMainMenuStyles() {
        Style style = new Style(".", StyleName.MainMenu);
        style.addProperty("width", "14em");
        style.addProperty("margin", "10px 0 0 10px");
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
        style.addProperty("padding", "6px");
        style.addProperty("list-style", "none");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-bottom-width", "0px");
        style.addProperty("position", "relative");
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
        style.addProperty("padding-bottom", "30px");
        addStyle(style);

        style = new Style(".", StyleName.PageFooter, " .", WidgetTheme.StyleName.Anchor);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.PageFooter, " .", WidgetTheme.StyleName.Toolbar, " .", WidgetTheme.StyleName.Anchor);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        addStyle(style);

        style = new Style(".", StyleName.PageFooterFollowUs);
        style.addProperty("padding", "10px 0");
        addStyle(style);

        style = new Style(".", StyleName.PageFooterFollowUs, " .", WidgetTheme.StyleName.Button);
        style.addProperty("color", ThemeColor.foreground, 0.01);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("height", "30px");
        style.addProperty("line-height", "30px");
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.PageFooterPmcInfoContent);
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style(".", StyleName.PageFooterPmcInfoText);
        style.addProperty("padding", "10px");
        addStyle(style);

    }

    private void initExtraGadgetStyles() {
        Style style = new Style(".", StyleName.ExtraGadget);
        style.addProperty("width", "220px");
        style.addProperty("text-align", "center");
        style.addProperty("display", "inline-block");
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

        style = new Style(".", StyleName.AllertButton);
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style(".", StyleName.AllertButton, " .", WidgetTheme.StyleName.ButtonText);
        style.addProperty("background", "none repeat scroll 0 0 #FF0000");
        style.addProperty("border-radius", "5px");
        style.addProperty("font-size", "11px");
        style.addProperty("height", "auto");
        style.addProperty("line-height", "11px");
        style.addProperty("margin", "0 11px");
        style.addProperty("position", "absolute");
        style.addProperty("left", "15px");
        style.addProperty("top", "4px");
        style.addProperty("padding", "2px 4px");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AllertButton, "-", StyleDependent.alertOn, " .", WidgetTheme.StyleName.ButtonText);
        style.addProperty("display", "inline");
        addStyle(style);

    }

    private void initHeaderNotificationStyles() {
        Style style = new Style(".", StyleName.NotificationContainer);
        style.addProperty("padding-top", "10px");
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100%");
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

    private void initPageNotificationStyles() {
        Style style = new Style(".", StyleName.NotificationGadget, " .", DashboardTheme.StyleName.GadgetBody);
        style.addProperty("text-align", "center");
        style.addProperty("padding-top", "20px");
        addStyle(style);
    }

    private void initTermsGadgetStyles() {
        Style style = new Style(".", StyleName.TermsGadget);
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TermsGadget, " .", DashboardTheme.StyleName.GadgetHeader);
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.TermsGadgetContent);
        style.addProperty("text-align", "left");
        style.addProperty("padding", "20px");
        addStyle(style);

    }

    private void initPointerStyles() {
        Style style = new Style(".", StyleName.MenuPointer);
        style.addProperty("position", "absolute");
        style.addProperty("right", "-42px");
        style.addProperty("height", "41px");
        style.addProperty("z-index", "100");
        style.addProperty("top", "3px");
        style.addProperty("-webkit-animation", "bounceH 2s infinite");
        style.addProperty("-moz-animation", "bounceH 2s infinite");
        style.addProperty("-ms-animation", "bounceH 2s infinite");
        style.addProperty("animation", "bounceH 2s infinite");
        addStyle(style);

        String keyframes = "\n\n@-webkit-keyframes bounceH {\n" + //
                "0%, 20%, 50%, 80%, 100% {-webkit-transform: translateX(0); transform: translateX(0);}\n" + //
                "40% {-webkit-transform: translateX(30px); transform: translateX(30px);}\n" + //);
                "60% {-webkit-transform: translateX(15px); transform: translateX(15px);}\n }\n";
        addAtRule(keyframes);

        keyframes = "\n\n@keyframes bounceH {\n" + //
                "0%, 20%, 50%, 80%, 100% {-webkit-transform: translateX(0); transform: translateX(0);}\n" + //
                "40% {-webkit-transform: translateX(30px); transform: translateX(30px);}\n" + //);
                "60% {-webkit-transform: translateX(15px); transform: translateX(15px);}\n }\n";
        addAtRule(keyframes);

        style = new Style(".", StyleName.ButtonPointer);
        style.addProperty("position", "absolute");
        style.addProperty("bottom", "-45px");
        style.addProperty("z-index", "100");
        style.addProperty("-webkit-animation", "bounceV 2s infinite");
        style.addProperty("-moz-animation", "bounceV 2s infinite");
        style.addProperty("-ms-animation", "bounceV 2s infinite");
        style.addProperty("animation", "bounceV 2s infinite");
        style.addProperty("width", "100%");
        style.addProperty("left", "5px");
        addStyle(style);

        keyframes = "\n\n@-webkit-keyframes bounceV {\n" + //
                "0%, 20%, 50%, 80%, 100% {-webkit-transform: translateY(0); transform: translateY(0);}\n" + //
                "40% {-webkit-transform: translateY(30px); transform: translateY(30px);}\n" + //);
                "60% {-webkit-transform: translateY(15px); transform: translateY(15px);}\n }\n";
        addAtRule(keyframes);

        keyframes = "\n\n@keyframes bounceV {\n" + //
                "0%, 20%, 50%, 80%, 100% {-webkit-transform: translateY(0); transform: translateY(0);}\n" + //
                "40% {-webkit-transform: translateY(30px); transform: translateY(30px);}\n" + //);
                "60% {-webkit-transform: translateY(15px); transform: translateY(15px);}\n }\n";
        addAtRule(keyframes);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
