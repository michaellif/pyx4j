/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2013
 * @author TOSHIBA
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.power;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultStaticTheme;

public class StaticTheme extends DefaultStaticTheme {

    @Override
    public void initStyle() {
        // TODO Auto-generated method stub
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name());
        style.addProperty("padding", "35px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li:active");
//        style.addProperty("background", "url(images/menu_active.png) no-repeat scroll left center transparent");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li");
        style.addProperty("color", "#1A1A1A");
        style.addProperty("font-size", "17px");
        style.addProperty("font-weight", "bold");
        style.addProperty("line-height", "38px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li a");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.SecondaryNavig.name(), " ul li a:hover");
        style.addProperty("color", "#00C4FF");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name());
        style.addProperty("border-left", "1px solid #D1D1D1");
        style.addProperty("border-top", "1px solid #D1D1D1");
        style.addProperty("min-height", "396px");
        style.addProperty("overflow", "hidden");
        style.addProperty("padding", "30px 30px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h2");
        style.addProperty("padding", "0 0 25px");
        style.addProperty("font-size", "18px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " p");
        style.addProperty("padding", "0 0 25px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.Content.name(), " h3");
        style.addProperty("font-size", "16px");
        addStyle(style);

    }

    @Override
    protected void initNewsBody() {
        // TODO Auto-generated method stub
        super.initNewsBody();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.Date.name());
        style.addProperty("color", "#A3A3A3");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.HeadLine.name());
        style.addProperty("color", "#00C4FF");
        style.addProperty("font-size", "18px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.NewsItem.name(), " .", StyleName.Text.name());
        style.addProperty("padding", "10px 0");
        addStyle(style);
    }

    @Override
    protected void initTestimBody() {
        // TODO Auto-generated method stub
        super.initTestimBody();

        Style style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name());
        style.addProperty("background-color", "#A4C5E8");
        style.addProperty("border-radius", "15px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteStaticPage.name(), " .", StyleName.TestimItem.name(), " .", StyleName.Name.name());
        style.addProperty("font-size", "14px");
        addStyle(style);
    }

}
