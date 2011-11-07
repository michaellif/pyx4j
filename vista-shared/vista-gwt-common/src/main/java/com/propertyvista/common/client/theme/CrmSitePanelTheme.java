/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 7, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

import com.propertyvista.common.client.resources.VistaImages;

public class CrmSitePanelTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SiteView, SiteViewContent, SiteViewAction, SiteViewHeader, SiteViewNavigation, SiteViewFooter, SiteViewDisplay, SiteViewNavigContainer;
    }

    public CrmSitePanelTheme() {
        // All viewable area:
        Style style = new Style(".", CrmSitePanelTheme.StyleName.SiteView.name());
        style.addProperty("color", ThemeColors.foreground);
        addStyle(style);

        // DockLayoutPanel:
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewContent.name());
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        // Header:
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewHeader.name());
        style.addGradient(ThemeColors.object1, 1, ThemeColors.object1, 0.7);
        style.addProperty("color", "white");
        style.addProperty("font-size", "1.1em");
        style.addProperty("padding-left", "0.3em");
        addStyle(style);

        // Footer:
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewFooter.name());
        style.addProperty("background", "url('" + VistaImages.INSTANCE.logo().getSafeUri().asString() + "') no-repeat scroll left center transparent");
        style.addProperty("background-color", ThemeColors.object1);
        addStyle(style);

        // NavigationContainer (Accordion menu):
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewNavigContainer.name());
        style.addProperty("min-width", "100px");
        addStyle(style);

        // Action (Header right side hyperlinks):
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewAction.name());
        //style.addProperty("min-width", "700px");
        style.addProperty("color", ThemeColors.object1, 0.1);
        style.addProperty("font-size", "1em");
        addStyle(style);

        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewAction.name(), " td");
        style.addProperty("vertical-align", "middle !important");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        // anchors within the ActionBar:
        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewAction.name(), " a:link, .", CrmSitePanelTheme.StyleName.SiteViewAction.name(),
                " a:visited, .", CrmSitePanelTheme.StyleName.SiteViewAction.name(), " a:active");
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.object1, 0.2);
        addStyle(style);

        style = new Style(".", CrmSitePanelTheme.StyleName.SiteViewAction.name(), " a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);
    }
}
