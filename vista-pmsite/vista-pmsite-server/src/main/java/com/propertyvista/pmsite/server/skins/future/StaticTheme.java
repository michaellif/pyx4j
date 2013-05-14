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
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultStaticTheme;

public class StaticTheme extends DefaultStaticTheme {

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name());
        style.addProperty("border-top", "1px solid #000000");
        style.addProperty("margin-right", "20px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li");
        style.addProperty("font-size", "16px");
        style.addProperty("line-height", "28px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li a");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li a:hover");
        style.addProperty("color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name());
        style.addProperty("border-top", "5px solid #000000");
        style.addProperty("padding", "20px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h2");
        style.addProperty("font-size", "14pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "15px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h3");
        style.addProperty("font-size", "13pt");
        style.addProperty("font-family", "monospace");
        addStyle(style);
    }

    @Override
    protected void initNewsBody() {
        super.initNewsBody();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.Date.name());
        style.addProperty("color", "#999999");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.HeadLine.name());
        style.addProperty("font-size", "14pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.Text.name());
        style.addProperty("padding", "5px 0");
        addStyle(style);

    }

    @Override
    protected void initTestimBody() {
        super.initTestimBody();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name());
        style.addProperty("background-color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name(), " .", StyleName.Name.name());
        style.addProperty("font-size", "12pt");
        style.addProperty(" font-family", "monospace");
        addStyle(style);

    }

}
