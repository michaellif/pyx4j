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
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;

public class PMSiteFutureTheme extends PMSiteTheme {

    public PMSiteFutureTheme(Stylesheet stylesheet) {
        super(stylesheet);
    }

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        super.initCommonStyle();

        Style style = new Style("body");
        style.addProperty("background", "#CCCCCC");
//      style.addProperty("background", "#fff url('images/bg-pattern.jpg')");
        addStyle(style);

        style = new Style("input,select,textarea");
        style.addProperty("color", "#333333");
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style("select");
        style.addProperty("color", "#999999");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style("input[type=submit]");
        style.addProperty("float", "right");
        style.addProperty("background", "#000000");
        style.addProperty("border", "medium none");
        style.addProperty("color", "#fff");
        style.addProperty("padding", "10px  !important");
        style.addProperty("font-size", "15px");
        addStyle(style);

        style = new Style("a");
        style.addProperty("color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.Main.name());
        style.addProperty("background", "#fff");
        style.addProperty("border-top", "6px solid #000000");
        addStyle(style);

    }

    @Override
    protected void initContainer() {
        super.initContainer();

        Style style = new Style(".", StyleName.Container.name());
        style.addProperty("width", "920px");
        style.addProperty("padding", "0 20px");
        style.addProperty("margin", "0 auto");
        style.addProperty("background", "#ffffff");
        addStyle(style);
    }

    @Override
    protected void initVistaPmsiteContent() {
        super.initVistaPmsiteContent();

        Style style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name());
        style.addProperty("padding", "5px 0");
        style.addProperty("border-top", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name(), " h1");
        style.addProperty("font-size", "14pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name());
        style.addProperty("padding", "5px 0");
        style.addProperty("border-top", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " h1");
        style.addProperty("font-size", "14pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-weight", "normal");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " .", StyleName.RightControl.name());
        style.addProperty("margin", "5px 0");
        style.addProperty("color", "#B6B8BA");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " .", StyleName.RightControl.name(), ":hover");
        style.addProperty("color", "#E6E7E8");
        addStyle(style);

    }

    @Override
    protected void initHeader() {
        super.initHeader();

        Style style = new Style(".", StyleName.Header.name(), " .", StyleName.Main.name());
        style.addProperty("height", "150px");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name());
        style.addProperty("position", "absolute");
        style.addProperty("width", "600px");
        style.addProperty("text-align", "center");
        style.addProperty("margin", "5px 160px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a:last-child");
        style.addProperty("color", "#999999");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a:first-child");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a:hover");
        style.addProperty("color", "#999999");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name());
        style.addProperty("top", "70%");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li");
        style.addProperty("margin", "0 4px");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li span");
        style.addProperty("display", "block");
        style.addProperty("width", "120px");
        style.addProperty("border-top", "1px solid #000000");
        style.addProperty("padding", "10px 20px");
        style.addProperty("white-space", "nowrap");
        style.addProperty("top", "70%");
        style.addProperty("text-align", "left");
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li span:hover");
        style.addProperty("border-top", "4px solid #000000");
        style.addProperty("padding-top", "7px");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a");
        style.addProperty("color", "#000000");
        addStyle(style);
    }

    @Override
    protected void initFooter() {
        super.initFooter();

        Style style = new Style(".", StyleName.Footer.name());
        style.addProperty("z-index", "10");
        addStyle(style);

        style = new Style(".", StyleName.Footer.name(), " .", StyleName.Main.name());
        style.addProperty("height", "100%");
        style.addProperty("border-top", "1px solid #000000");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " label");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " li");
        style.addProperty("width", "192px");
        addStyle(style);

        style = new Style(".", StyleName.FooterLocations.name(), " a");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.FooterLocations.name(), " a:hover");
        style.addProperty("color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.FooterLinks.name());
        style.addProperty("padding-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name());
        style.addProperty("border-left", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), " a");
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), " a:hover");
        style.addProperty("color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.FooterCopyright.name());
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name(), " label");
        style.addProperty("color", "#000000");
        addStyle(style);

    }
}
