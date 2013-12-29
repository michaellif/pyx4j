/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class SiteViewTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewAction, SiteViewHeader, SiteViewNavigation, SiteViewFooter, SiteViewNavigContainer, SiteViewShortCuts, SiteViewShortCutsItem;
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, collapsed
    }

    public SiteViewTheme() {
        // All viewable area:
        Style style = new Style(".", StyleName.SiteView.name());
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(".", StyleName.SiteViewContent.name());
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // Header:
        style = new Style(".", StyleName.SiteViewHeader.name());
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.7);
        style.addProperty("color", "white");
        style.addProperty("height", "100%");
        style.addProperty("width", "100%");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // Footer:
        style = new Style(".", StyleName.SiteViewFooter.name());
//        style.addProperty("background", "url('" + VistaImages.INSTANCE.logo().getSafeUri().asString() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", ThemeColor.object1);
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(".", StyleName.SiteViewNavigContainer.name());
        style.addProperty("border-right", "4px solid");
        style.addProperty("border-right-color", ThemeColor.object1);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        /*
         * components within the class:
         */
        // stack header
        style = new Style(".", StyleName.SiteViewNavigContainer.name(), " .gwt-StackLayoutPanelHeader");
        style.addProperty("font-size", "1.3em");
        style.addProperty("padding-left", "0.5em");
        style.addProperty("cursor", "pointer");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.6);
        addStyle(style);

        style = new Style(".", StyleName.SiteViewNavigContainer.name(), "-", StyleDependent.collapsed.name(), " .gwt-Label");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewNavigContainer.name(), " a:link, .", StyleName.SiteViewNavigContainer.name(), " a:visited, .",
                StyleName.SiteViewNavigContainer.name(), " a:active");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewNavigContainer.name(), " .gwt-StackLayoutPanelHeader-", StyleDependent.selected.name());
        style.addProperty("font-weight", "bold");
        style.addTextShadow(ThemeColor.foreground, "1px 1px 0");
        style.addGradient(ThemeColor.object1, 1, ThemeColor.object1, 0.8);
        addStyle(style);

        // stack content
        style = new Style(".", StyleName.SiteViewNavigContainer.name(), " .gwt-StackLayoutPanelContent");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "1em");
        style.addProperty("background-color", ThemeColor.foreground, 0.02);
        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(".", StyleName.SiteViewAction.name());
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColor.object1, 0.1);
        style.addProperty("font-size", "1em");
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewAction.name(), " td");
        style.addProperty("vertical-align", "middle !important");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        // anchors within the ActionBar:
        style = new Style(".", StyleName.SiteViewAction.name(), " a:link, .", StyleName.SiteViewAction.name(), " a:visited, .",
                StyleName.SiteViewAction.name(), " a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColor.object1, 0.2);
        addStyle(style);

        style = new Style(".", StyleName.SiteViewAction.name(), " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCuts.name());
        style.addProperty("border-right", "4px solid");
        style.addProperty("border-right-color", ThemeColor.object1);
        addStyle(style);

        /*
         * components within the class:
         */
        //stack header
        style = new Style(".", StyleName.SiteViewShortCuts.name(), " .gwt-StackLayoutPanelHeader");
        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-left", "1em");
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("border-top", "solid 4px");
        style.addProperty("border-top-color", ThemeColor.object1);
        // NOTE: must correspond with the header size defined by stackpanel
        style.addProperty("line-height", "2.2em");
        addStyle(style);

        // stack content
        style = new Style(".", StyleName.SiteViewShortCuts.name(), " .gwt-StackLayoutPanelContent");
        style.addProperty("font-size", "1.1em");
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        addStyle(style);

        style = new Style(".", StyleName.SiteViewShortCuts.name(), " a:link, .", StyleName.SiteViewShortCuts.name(), " a:visited, .",
                StyleName.SiteViewShortCuts.name(), " a:active");
        style.addProperty("text-decoration", "none");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}