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

import com.propertyvista.pmsite.server.skins.base.DefaultFindAptTheme;

public class FindAptTheme extends DefaultFindAptTheme {

    @Override
    public void initStyle() {
        // TODO Auto-generated method stub
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style(".", StyleName.Caption.name());
        style.addProperty("height", "70px");
        style.addProperty("padding", "0 0 0 30px");
        style.addProperty("background", "none repeat scroll 0 0 #6B547A");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption.name(), " h1");
        style.addProperty("color", "#EDE8F0");
        style.addProperty("line-height", "70px");
        style.addProperty("font-size", "36px");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteContent.name(), " .", StyleName.Content.name());
//        style.addProperty("background-image", "url("images/foto_for_rockville.jpg")");
        style.addProperty("background-position", "right 30px");
        style.addProperty("background-repeat", "no-repeat");
        addStyle(style);
    }

    @Override
    protected void initAdvancedSearchWideWrap() {
        // TODO Auto-generated method stub
        super.initAdvancedSearchWideWrap();

        Style style = new Style(".", StyleName.AdvancedSearchWideWrap.name());
        style.addProperty("width", "566px");
        style.addProperty("padding", "30px 0 30px 30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelBox.name());
        style.addProperty("height", "60px");
        style.addProperty("width", "100%");
        style.addProperty("margin-top", "0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("width", "226px");
        style.addProperty("background-color", "#DDD3B4");
        style.addProperty("letter-spacing", "3px");
        style.addProperty("padding", "6px 20px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.TypeselActive.name());
        style.addProperty("color", "#DDD3B4 !important");
        style.addProperty("background-color", "#988445 !important");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.TypeselActiveHover.name());
        style.addProperty("color", "#DDD3B4 !important");
        style.addProperty("background-color", "#988445 !important");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " label");
        style.addProperty("padding", "6px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name());
        style.addProperty("display", "block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name());
        style.addProperty("width", "175px");
        style.addProperty("margin-left", "15px");
        style.addProperty("float", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " select");
        style.addProperty("margin", "1px");
        style.addProperty("width", "266px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " input[type=text]");
        style.addProperty("width", "260px");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name());
        style.addProperty("width", "175px");
        style.addProperty("margin-left", "15px");
        style.addProperty("float", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " select");
        style.addProperty("width", "67px");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("width", "34px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " input[type=text]");
        style.addProperty("width", "62px");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("width", "100%");
        style.addProperty("border-top", "1px dotted #767676");
        style.addProperty("padding-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name());
        style.addProperty("width", "600px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name(), " label");
        style.addProperty("width", "125px");
        style.addProperty("font-size", "13px");
        addStyle(style);
    }

}
