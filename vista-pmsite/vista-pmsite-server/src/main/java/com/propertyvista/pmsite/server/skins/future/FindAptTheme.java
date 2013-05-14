/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 5, 2013
 * @author Admin
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultFindAptTheme;

public class FindAptTheme extends DefaultFindAptTheme {

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteFindAptPage.name());
        style.addProperty("color", "#FFFFFF");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteFindAptPage.name(), " .", StyleName.VistaPmsiteContent.name(), " .", StyleName.Content.name());
        style.addProperty("border-top", "5px solid #000000");
//      style.addProperty("background", "url('images/FreeCableTV.png') no-repeat right");
        addStyle(style);

    }

    @Override
    protected void initAdvancedSearchWideWrap() {
        super.initAdvancedSearchWideWrap();

        Style style = new Style(".", StyleName.AdvancedSearchWideWrap.name());
        style.addProperty("width", "620px");
        style.addProperty("background-color", "#999999");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.AdvancedSearchCriteria.name());
        style.addProperty("top", "0px");
        style.addProperty("height", "383px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " input[type=submit]");
        style.addProperty("margin", "20px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelBox.name());
        style.addProperty("padding", "0 80px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("width", "215px");
        style.addProperty("letter-spacing", "1px");
        style.addProperty("color", "#000000");
        style.addProperty("background-color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " label");
        style.addProperty("padding", "6px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " input[type=checkbox]");
        style.addProperty("// margin", "0 2px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name());
        style.addProperty("padding", "0 80px");
        style.addProperty("clear", "both");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " select");
        style.addProperty("width", "215px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " input[type=text]");
        style.addProperty("width", "215px");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name());
        style.addProperty("padding", "0 80px");
        style.addProperty("*padding-bottom", "8px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name());
        style.addProperty("margin-left", "23px");
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        style.addProperty("*float", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        style.addProperty("width", "25px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " select");
        style.addProperty("width", "55px");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " input[type=text]");
        style.addProperty("width", "53px");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("border-top", "1px solid #fff");
        style.addProperty("padding", "20px 80px 0 80px");
        style.addProperty("margin-bottom", "40px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name());
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name(), " label");
        style.addProperty("width", "88px");
        style.addProperty("font-size", "13px");
        style.addProperty("zoom", "1");
        style.addProperty("*display", "inline");
        style.addProperty("*_float", "left");
        style.addProperty("_clear", "both");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.TypeselActiveHover.name());
        style.addProperty("color", "#CCCCCC !important");
        style.addProperty("background-color", "#000000 !important");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.TypeselActive.name());
        style.addProperty("color", "#CCCCCC !important");
        style.addProperty("background-color", "#000000 !important");
        style.addProperty("width", "214px");
        addStyle(style);

    }
}