/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.tester.ui;

import com.pyx4j.site.client.theme.AppSiteTheme;
import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.style.Style;

public class DefaultTheme extends AppSiteTheme {

    @Override
    protected void initBodyStyles() {
        super.initBodyStyles();
        Style style = new Style("body");
        style.addProperty("background-color", "#eee");
        addStyle(style);
    }

    @Override
    protected void initSiteViewStyles() {
        String prefix = SiteView.DEFAULT_STYLE_PREFIX;

        int minWidth = 760;
        int maxWidth = 960;
        int leftColumnWidth = 230;
        int rightColumnWidth = 200;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("background-color", "#fff");
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Header));
        style.addProperty("height", "100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.MainNavig));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Center));
        style.addProperty("width", "100%");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Main));
        style.addProperty("height", "100%");
        style.addProperty("margin", "0 " + rightColumnWidth + "px 0 " + leftColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Left));
        style.addProperty("float", "left");
        style.addProperty("width", leftColumnWidth + "px");
        style.addProperty("margin-left", "-100%");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Right));
        style.addProperty("float", "left");
        style.addProperty("width", rightColumnWidth + "px");
        style.addProperty("margin-left", "-" + rightColumnWidth + "px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, SiteView.StyleSuffix.Footer));
        style.addProperty("clear", "left");
        addStyle(style);

    }

}
