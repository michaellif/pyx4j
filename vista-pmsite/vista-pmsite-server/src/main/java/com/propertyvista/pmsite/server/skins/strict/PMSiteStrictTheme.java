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
package com.propertyvista.pmsite.server.skins.strict;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;

public class PMSiteStrictTheme extends PMSiteTheme {

    public PMSiteStrictTheme(Stylesheet stylesheet) {
        super(stylesheet);
    }

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style("body");
        style.addProperty("background", "-moz-linear-gradient(center top, #988445 0px, #FFFFFF 400px) no-repeat scroll 0 0 transparent");
        style.addProperty("background", "-webkit-linear-gradient(top, #988445 0px, #FFFFFF 400px) no-repeat scroll 0 0 transparent");
        style.addProperty("background", "-o-linear-gradient(center top, #988445 0px, #FFFFFF 400px) no-repeat scroll 0 0 transparent");
        style.addProperty("background", "-ms-linear-gradient(top, #988445 0px, #FFFFFF 400px)");
        style.addProperty("background", "linear-gradient(#988445 0px, #FFFFFF 400px)");
        style.addProperty("filter", "progid:DXImageTransform.Microsoft.gradient(startColorstr='#988445', endColorstr='#FFFFFF',GradientType=0)");
        style.addProperty("background-repeat", "no-repeat");
        addStyle(style);

        style = new Style("input[type='submit']");
        style.addProperty("width", "128px");
        style.addProperty("margin", "15px auto 0");
        style.addProperty("padding", "5px 0");
        style.addProperty("background", "none repeat scroll 0 0 #544107");
        style.addProperty("border", "medium none");
        style.addProperty("color", "#EEE9D8");
        style.addProperty("cursor", "pointer");
        style.addProperty("font-size", "16px");
        style.addProperty("text-transform", "uppercase");
        addStyle(style);
    }

    @Override
    protected void initContainer() {
        // TODO Auto-generated method stub
        super.initContainer();

        Style style = new Style(".", StyleName.Container.name());
        style.addProperty("width", "960px");
        style.addProperty("margin", "0 auto");
        style.addProperty("background-color", "#EDE8F0");
        addStyle(style);

        style = new Style("a");
        style.addProperty("color", "#544107");
        addStyle(style);
    }

    @Override
    protected void initVistaPmsiteContent() {
        // TODO Auto-generated method stub
        super.initVistaPmsiteContent();

        Style style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Content.name());
        style.addProperty("border-left", "1px solid #D1D1D1");
        style.addProperty("background-color", "#EDE8F0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name());
        style.addProperty("height", "70px");
        style.addProperty("padding", "0 0 0 30px");
        style.addProperty("background", "none repeat scroll 0 0 #6B547A");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name(), " h1");
        style.addProperty("color", "#EDE8F0");
        style.addProperty("font-size", "36px");
        style.addProperty("line-height", "70px");
        addStyle(style);
    }

    @Override
    protected void initHeader() {
        // TODO Auto-generated method stub
        super.initHeader();

        Style style = new Style(".", StyleName.Header.name(), " .", StyleName.Main.name());
        style.addProperty("height", "130px");
        style.addProperty("margin-top", "0");
        style.addProperty("position", "relative");
        style.addProperty("background", "none repeat scroll 0 0 #544107");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name());
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name(), ":hover");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("#", StyleName.SiteLogo.name());
        style.addProperty("height", "93px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a");
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name());
        style.addProperty("width", "615px");
        style.addProperty("height", "50px");
        style.addProperty("bottom", "0");
        style.addProperty("right", "0");
        style.addProperty("padding-bottom", "30px");
        style.addProperty("text-align", "center");
        style.addProperty("vertical-align", "bottom");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a");
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a:hover span");
        style.addProperty("border-bottom", "1px solid #DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " .active a span");
        style.addProperty("border-bottom", "2px solid #DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li");
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li span");
        style.addProperty("font-size", "18px");
        style.addProperty("line-height", "50px");
        style.addProperty("margin", "0 10px");
        style.addProperty("padding", "2px 0");
        addStyle(style);
    }

    @Override
    protected void initFooter() {
        // TODO Auto-generated method stub
        super.initFooter();

        Style style = new Style(".", StyleName.Footer.name(), " .", StyleName.Main.name());
        style.addProperty("background", "#544107");
        addStyle(style);

        style = new Style(".", StyleName.FooterLocations.name(), " a");
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name());
        style.addProperty("width", "790px");
        style.addProperty("vertical-align", "top");
        style.addProperty("margin-left", "10px");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " label");
        style.addProperty("color", "#EDE8F0");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " li");
        style.addProperty("width", "195px");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name(), " label");
        style.addProperty("color", "#EDE8F0");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name());
        style.addProperty("border-left", "1px solid #DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), " a");
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name());
        style.addProperty("margin-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name(), " a");
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.FooterCopyright.name());
        style.addProperty("color", "#DDD3B4");
        addStyle(style);

        style = new Style(".", StyleName.LegalPoweredby.name());
        style.addProperty("color", "#DDD3B4");
        addStyle(style);
    }

}
