/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 8, 2013
 * @author TOSHIBA
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.strict;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultLandingTheme;

public class LandingTheme extends DefaultLandingTheme {

    @Override
    public void initStyle() {
        // TODO Auto-generated method stub
        super.initStyle();
    }

    @Override
    protected void initBanner() {
        // TODO Auto-generated method stub
        super.initBanner();

        Style style = new Style(".", StyleName.BannerImg.name());
        style.addProperty("display", "none");
        addStyle(style);
    }

    @Override
    protected void initInfobox() {
        // TODO Auto-generated method stub
        super.initInfobox();

        Style style = new Style(".", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("color", "#EDE8F0");
        style.addProperty("font-size", "22px");
        style.addProperty("line-height", "40px");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxHeader.name());
        style.addProperty("background", "#5D466B");
        style.addProperty("padding", "0 0 0 20px");
        addStyle(style);
    }

    @Override
    protected void initQuickSearchBox() {
        // TODO Auto-generated method stub
        super.initQuickSearchBox();

        Style style = new Style(".", StyleName.QuickSearchBox.name(), " .", StyleName.InfoboxHeader.name());
        style.addProperty("background", "#6B547A");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("border-right", "1px solid #DCD2E1");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBoxField.name(), " label");
        style.addProperty("padding", "0 0 3px");
        style.addProperty("display", "block");
        style.addProperty("color", "#555555");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " select");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " .submit");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " input[type=submit]");
        style.addProperty("width", "128px");
        style.addProperty("margin", "15px auto 0");
        style.addProperty("padding", "5px 0");
        style.addProperty("font-size", "16px");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("cursor", "pointer");
        style.addProperty("color", "#EEE9D8");
        style.addProperty("border", "medium none");
        style.addProperty("background", "none repeat scroll 0 0 #544107");
        addStyle(style);
    }

    @Override
    protected void initNewsBox() {
        // TODO Auto-generated method stub
        super.initNewsBox();

        Style style = new Style(".", StyleName.NewsBox.name());
        style.addProperty("min-height", "450px");
        style.addProperty("border-right", "1px solid #DCD2E1");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.InfoboxHeader.name());
        style.addProperty("background", "none repeat scroll 0 0 #6B547A");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding", "10px 20px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.HeadLine.name());
        style.addProperty("color", "#AA965C");
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Date.name());
        style.addProperty("color", "#A3A3A3");
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Text.name());
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Text.name(), " .", StyleName.ReadMore.name());
        style.addProperty("color", "#AA965C");
        style.addProperty("font-size", "20px");
        style.addProperty("line-height", "14px");
        style.addProperty("vertical-align", "text-top");
        style.addProperty("padding-left", "0");
        addStyle(style);
    }

    @Override
    protected void initPromoBox() {
        // TODO Auto-generated method stub
        super.initPromoBox();

        Style style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding", "20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.CitySelectionForm.name());
        style.addProperty("margin", "0 0 20px 20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("margin-left", "20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.Picture.name());
        style.addProperty("border", "1px solid #544107");
        addStyle(style);
    }

    @Override
    protected void initTestimBox() {
        // TODO Auto-generated method stub
        super.initTestimBox();

        Style style = new Style(".", StyleName.ReadMore.name());
        style.addProperty("color", "#EDE8F0");
        style.addProperty("font-size", "16px");
        style.addProperty("line-height", "14px");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding", "20px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("background", "#EEE9D8");
        style.addProperty("border", "1px solid #DDD3B4");
        style.addProperty("border-radius", "15px 15px 15px 15px");
        style.addProperty("float", "left");
        style.addProperty("height", "80px");
        style.addProperty("margin", "20px 10px 30px");
        style.addProperty("padding", "20px");
        style.addProperty("width", "560px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Name.name());
        style.addProperty("color", "#1A1A1A");
        style.addProperty("padding", "10px 0 0");
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Callout.name());
        style.addProperty("position", "absolute");
        style.addProperty("left", "500px");
        style.addProperty("top", "140px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.CalloutLine.name());
        style.addProperty("fill", "#EEE9D8");
        style.addProperty("stroke", "#DDD3B4");
        style.addProperty("stroke-width", "1");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxContent.name(), " .", StyleName.ReadMore.name());
        style.addProperty("color", "#AA965C");
        style.addProperty("font-size", "20px");
        style.addProperty("line-height", "14px");
        style.addProperty("vertical-align", "text-top");
        style.addProperty("padding-left", "0");
        addStyle(style);
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteMainWrap.name());
        style.addProperty("background", "#EDE8F0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("width", "300px");
        addStyle(style);
    }

}
