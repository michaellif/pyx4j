/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeDescriminator;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteStyleFactory;
import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public abstract class PMSiteTheme extends PMSiteThemeBase {

    public static enum Stylesheet implements ThemeDescriminator {
        AptDetails, AptList, BuildingInfoPanel, CityPage, Error, FindApt, FloorplanInfoPanel, Inquiry, InquiryOk, InquiryPanel, Landing, Resident, Static, Terms, UnitDetails
    }

    public static enum StyleName implements IStyleName {
        Container, Header, Footer, VistaPmsiteContent,

        Main, VistaPmsiteMain,

        SiteTitle, NoLogo, SiteAuthInsert, Navigation, FooterLocations, FooterLinks, FooterLink, FooterSocial, Locale, FooterCopyright, LegalPoweredby, Caption, Caption2,

        IconSocial, Twitter, Facebook, Youtube, Flickr, PoweredLogo, RightControl,

        Greeting, LangItem, SiteLogo, Content;
    }

    public PMSiteTheme(Stylesheet stylesheet) {
        super();

        try {
            addTheme(PMSiteStyleFactory.create(getClass(), stylesheet));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        initCommonStyle();

        initContainer();
        initVistaPmsiteContent();
        initHeader();
        initFooter();

    }

    protected void initCommonStyle() {
        Style style = new Style("img,form,fieldset");
        style.addProperty("border", "medium none");
        addStyle(style);

        style = new Style("form,fieldset");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("body,h1,h2,h3,h4,h5,h6,dl,dt,dd,p");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("q");
        style.addProperty("quotes", "none");
        addStyle(style);

        style = new Style("input:focus,textarea:focus,select:focus");
        style.addProperty("outline", "medium none");
        addStyle(style);

        style = new Style("html,body");
        style.addProperty("width", "100%");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style("body");
        style.addProperty("font", "13px Arial, Helvetica, sans-serif");
        addStyle(style);

        style = new Style("input,select,textarea");
        style.addProperty("color", "#1A1A1A");
        style.addProperty("font", "13px Arial, Helvetica, sans-serif");
        addStyle(style);

        style = new Style("input[type=submit]");
        style.addProperty("cursor", "pointer");
        style.addProperty("padding", "0.5em");
        addStyle(style);

        style = new Style("table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("a");
        style.addProperty("outline", "medium none");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(".", StyleName.Main.name());
        style.addProperty("position", "relative");
        addStyle(style);

    }

    protected void initContainer() {
    }

    protected void initVistaPmsiteContent() {
        Style style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name());
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name(), " h1");
        style.addProperty("font-weight", "normal");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " h1");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " .", StyleName.RightControl.name());
        style.addProperty("float", "right");
        style.addProperty("height", "25px");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("float", "left");
        addStyle(style);

    }

    protected void initHeader() {
        Style style = new Style(".", StyleName.Header.name());
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name());
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style("#", StyleName.SiteTitle.name(), " a:hover");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("#", StyleName.NoLogo.name());
        style.addProperty("font-size", "40px");
        style.addProperty("line-height", "80px");
        style.addProperty("margin-righ", "20px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name());
        style.addProperty("float", "right");
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " .", StyleName.Greeting.name());
        style.addProperty("font-weight", "bold");
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style("#", StyleName.SiteAuthInsert.name(), " a");
        style.addProperty("margin-right", "1em");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name());
        style.addProperty("height", "60px");
        style.addProperty("position", "absolute");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul");
        style.addProperty("list-style", "none outside none");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " ul li");
        style.addProperty("display", "inline-block");
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        addStyle(style);

        style = new Style(".", StyleName.Navigation.name(), " a");
        style.addProperty("text-decoration", "none");
        addStyle(style);

    }

    protected void initFooter() {
        Style style = new Style(".", StyleName.Footer.name());
        style.addProperty("position", "relative");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name());
        style.addProperty("display", "inline-block");
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        style.addProperty("list-style-type", "none");
        style.addProperty("padding", "0");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("padding-bottom", "10px");
        addStyle(style);

        style = new Style("ul.", StyleName.FooterLocations.name(), " li");
        style.addProperty("float", "left");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.FooterLinks.name());
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name(), ":first-child");
        style.addProperty("border-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.FooterLink.name());
        style.addProperty("padding", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.Locale.name());
        style.addProperty("margin-top", "40px");
        style.addProperty("text-align", "center");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name());
        style.addProperty("margin-left", "20px");
        style.addProperty("padding", "0 3px");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name(), ":first-child");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.LangItem.name(), " a");
        style.addProperty("color", "inherit");
        addStyle(style);

        style = new Style(".", StyleName.FooterCopyright.name());
        style.addProperty("text-align", "center");
        style.addProperty("line-height", "60px");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "140px");
        style.addProperty("margin-left", "10px");
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name(), " span");
        style.addProperty("padding", "0");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(".", StyleName.FooterSocial.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("text-transform", "uppercase");
        addStyle(style);

        style = new Style(".", StyleName.IconSocial.name());
        style.addProperty("display", "inline-block");
        style.addProperty("width", "32px");
        style.addProperty("height", "32px");
//      style.addProperty("background", "url(images/social4.png) no-repeat");
        addStyle(style);

        style = new Style(".", StyleName.IconSocial.name(), " .", StyleName.Twitter.name());
        style.addProperty("background-position", "0 0");
        addStyle(style);

        style = new Style(".", StyleName.IconSocial.name(), " .", StyleName.Facebook.name());
        style.addProperty("background-position", "-32px 0");
        addStyle(style);

        style = new Style(".", StyleName.IconSocial.name(), " .", StyleName.Youtube.name());
        style.addProperty("background-position", "-64px 0");
        addStyle(style);

        style = new Style(".", StyleName.IconSocial.name(), " .", StyleName.Flickr.name());
        style.addProperty("background-position", "-96px 0");
        addStyle(style);

        style = new Style(".", StyleName.LegalPoweredby.name());
        style.addProperty("text-align", "center");
        style.addProperty("line-height", "20px");
        addStyle(style);

        style = new Style(".", StyleName.PoweredLogo.name());
//      style.addProperty("background", "url('images/vista.png') no-repeat");
        style.addProperty("display", "inline-block");
        style.addProperty("width", "100px");
        style.addProperty("height", "20px");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

    }
}
