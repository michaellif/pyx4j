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
 * @author Admin
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public class DefaultStaticTheme extends PMSiteThemeBase {

    public static enum StyleName implements IStyleName {
        VistaPmsiteStaticPage, VistaPmsiteContent, VistaPmsiteSidebar, VistaPmsiteMain,

        SecondaryNavig, Caption, Content,

        NewsBody, TestimBody,

        NewsItem, Date, HeadLine, Text,

        TestimItem, Quote, Name,

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        initCommonStyle();
        initNewsBody();
        initTestimBody();

    }

    protected void initCommonStyle() {
        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name());
        style.addProperty("float", "left");
        style.addProperty("width", "211px");
        style.addProperty("min-height", "400px");
        style.addProperty("padding", "30px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li");
        style.addProperty("padding", "0 10px 0 35px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li.active");
//      style.addProperty("background", "url('images/menu_active.png') no-repeat left center");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name());
        style.addProperty("overflow", "hidden");
        style.addProperty("text-align", "justify");
        style.addProperty("min-height", "400px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h2");
        style.addProperty("line-height", "20px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h3");
        style.addProperty("line-height", "20px");
        style.addProperty("padding", "0 0 25px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " p");
        style.addProperty("padding", "0.5em 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " address");
        style.addProperty("padding", "0 0 25px");
        addStyle(style);

    }

    protected void initNewsBody() {
        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name());
        style.addProperty("padding-bottom", "20px");
        addStyle(style);

    }

    protected void initTestimBody() {
        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name());
        style.addProperty("padding", "20px");
        style.addProperty("margin-bottom", "20px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name(), " .", StyleName.Name.name());
        style.addProperty("text-align", "right");
        addStyle(style);

    }
}
