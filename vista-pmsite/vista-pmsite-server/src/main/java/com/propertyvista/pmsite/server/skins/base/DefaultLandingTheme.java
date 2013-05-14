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
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public class DefaultLandingTheme extends PMSiteThemeBase {

    public static enum StyleName implements IStyleName {

        Infobox, InfoboxHeader, InfoboxHeaderTitle, InfoboxBody, InfoboxDecor, InfoboxContent,

        TestimBox, QuickSearchBox, QuickSearchBoxField, PromoBox, NewsBox, GadgetBox,

        VistaPmsiteMainWrap, VistaPmsiteSidebar, VistaPmsiteMain,

        ReadMore, Date, HeadLine, Text, CitySelectionForm, Address, Picture, Name, Callout, CalloutLine,

        Banner, BannerImg;
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        initBanner();

        initInfobox();

        initQuickSearchBox();
        initNewsBox();
        initPromoBox();
        initTestimBox();

        initCommonStyle();
    }

    protected void initBanner() {

        Style style = new Style(".", StyleName.BannerImg.name());
        style.addProperty("height", "375px");
        style.addProperty("background-repeat", "no-repeat");
        addStyle(style);
    }

    protected void initInfobox() {

        Style style = new Style(".", StyleName.InfoboxBody.name());
        style.addProperty("overflow", "hidden");
        addStyle(style);
    }

    protected void initQuickSearchBox() {

        Style style = new Style(".", StyleName.QuickSearchBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding", "20px");
        addStyle(style);
    }

    protected void initNewsBox() {

        Style style = new Style(".", StyleName.NewsBox.name());
        style.addProperty("padding", "20px 0");
        addStyle(style);
    }

    protected void initPromoBox() {

        Style style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("float", "left");
        style.addProperty("width", "140px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.Address.name(), ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

    }

    protected void initTestimBox() {

        Style style = new Style(".", StyleName.TestimBox.name());
        style.addProperty("overflow", "hidden");
        style.addProperty("float", "left");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxDecor.name());
        style.addProperty("display", "inline-block");
        style.addProperty("position", "relative");
        addStyle(style);

    }

    protected void initCommonStyle() {
        Style style = new Style(".", StyleName.VistaPmsiteMainWrap.name());
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("float", "left");
        style.addProperty("width", "280px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name());
        style.addProperty("margin-left", "300px");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBoxField.name());
        style.addProperty("padding-bottom", "15px");
        addStyle(style);

    }
}
