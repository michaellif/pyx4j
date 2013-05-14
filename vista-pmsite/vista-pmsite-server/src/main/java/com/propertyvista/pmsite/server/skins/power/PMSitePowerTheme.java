/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.power;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;

public class PMSitePowerTheme extends PMSiteTheme {

    public PMSitePowerTheme(Stylesheet stylesheet) {
        super(stylesheet);
    }

    @Override
    public void initStyle() {
        super.initStyle();
        // TODO - do all modification to common style here

    }

    @Override
    protected void initCommonStyle() {
        super.initCommonStyle();

        Style style = new Style("select");
        style.addProperty("background-color", "#FFFFFF");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style("input, select, textarea");
        style.addProperty("font", "13px Arial, Helvetica, sans-serif");
        addStyle(style);

        style = new Style("html, body");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style("input[type=submit]");
        style.addProperty("width", "128px");
        style.addProperty("height", "32px");
        style.addProperty("margin", "15px auto 0");
        style.addProperty("cursor", "pointer");
        style.addProperty("background", "#FF0000");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("color", "#FFE6E6");
        style.addProperty("font-size", "16px");
        addStyle(style);

        style = new Style("a");
        style.addProperty("color", "#0458B3");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name());
        style.addProperty("border-left", "1px solid #D1D1D1");
        style.addProperty("height", "80px");
        style.addProperty("padding", "40px 0 0 30px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name(), " h1");
        style.addProperty("color", "#0458B3");
        style.addProperty("font-size", "36px");
        style.addProperty("font-weight", "normal");
        addStyle(style);
    }

    @Override
    protected void initContainer() {
        // TODO Auto-generated method stub
        super.initContainer();

        Style style = new Style(".", StyleName.Container.name());
        style.addProperty("width", "960px");
        style.addProperty("margin", "0 auto");
        addStyle(style);
    }

    @Override
    protected void initVistaPmsiteContent() {
        // TODO Auto-generated method stub
        super.initVistaPmsiteContent();

        Style style = new Style(".", StyleName.VistaPmsiteContent.name());
        style.addProperty("background-color", "#DFEBF7");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name());
        style.addProperty("background-color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.Caption2.name());
        style.addProperty("height", "60px");
        style.addProperty("border-bottom", "1px solid #D1D1D1");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " h1");
        style.addProperty("color", "#767676");
        style.addProperty("font-size", "16px");
        style.addProperty("font-weight", "bold");
        style.addProperty("padding", "22px 0 0 24px");
        style.addProperty("text-transform", "uppercase");
        addStyle(style);
    }

    @Override
    protected void initHeader() {
        // TODO Auto-generated method stub
        super.initHeader();

        Style style = new Style(".", StyleName.Header.name(), " .", StyleName.Main.name());
        style.addProperty("height", "164px");
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name());
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a:hover");
        style.addProperty("color", "#00C4FF");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name());
        style.addProperty("background", "#0458B3");
        style.addProperty("top", "104px");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a");
        style.addProperty("color", "#DFEBF7");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a:hover");
        style.addProperty("color", "#FF0000");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li span");
        style.addProperty("font-size", "18px");
        style.addProperty("line-height", "60px");
        style.addProperty("margin", "0 30px");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " .active a");
        style.addProperty("background-color", "#FF0000");
        style.addProperty("color", "#FFE6E6");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li.active");
        style.addProperty("background-color", "#FF0000");
        style.addProperty("color", "#FFE6E6");
        addStyle(style);
    }

    @Override
    protected void initFooter() {
        // TODO Auto-generated method stub
        super.initFooter();

        Style style = new Style(".", StyleName.Footer.name());
        style.addProperty("background", "#FAF1E1");
        style.addProperty("border-top", "1px solid #D1D1D1");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name());
        style.addProperty("margin-left", "10px");
        style.addProperty("width", "790px");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " label");
        style.addProperty("color", "#767676");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name(), " label");
        style.addProperty("color", "#767676");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " li");
        style.addProperty("width", "195px");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " a:hover");
        style.addProperty("color", "#00C4FF");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name());
        style.addProperty("border-left", "1px solid #1A1A1A");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), " a");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), " a:hover");
        style.addProperty("color", "#00C4FF");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name());
        style.addProperty("margin-left", "10px");
        addStyle(style);

    }

}
