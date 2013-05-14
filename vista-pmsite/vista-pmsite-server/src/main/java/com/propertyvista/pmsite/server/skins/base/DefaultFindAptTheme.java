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
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public class DefaultFindAptTheme extends PMSiteThemeBase {

    public static enum StyleName implements IStyleName {

        VistaPmsiteFindAptPage, VistaPmsiteContent, VistaPmsiteMain,

        Caption, Content,

        AdvancedSearchWideWrap, AdvancedSearchCriteria,

        InputBlockFrame, SearchTypeSelBox, SearchTypeBox, SearchCommonBox, SearchAmenityBox,

        SearchTypeSelButton, SearchField, InputJoinBox, AmenityList, TypeselActive, TypeselActiveHover,

        ErrorPanel,
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        initCommonStyle();
        initAdvancedSearchWideWrap();

    }

    protected void initCommonStyle() {
        Style style = new Style(".", StyleName.VistaPmsiteFindAptPage.name());
        style.addProperty("font-size", "16px");
        addStyle(style);

    }

    protected void initAdvancedSearchWideWrap() {
        Style style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.ErrorPanel.name());
        style.addProperty("padding", "2px");
        style.addProperty("padding-left", "15px");
        style.addProperty("margin", "0");
        style.addProperty("margin-top", "10px");
        style.addProperty("color", "red");
        style.addProperty("background", "white");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelBox.name());
        style.addProperty("float", "left");
        style.addProperty("margin-top", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("font-size", "20px");
        style.addProperty("margin-left", "30px");
        style.addProperty("text-align", "center");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name(), ":first-child");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " label");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " input[type=radio]");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name());
        style.addProperty("float", "left");
        style.addProperty("margin-top", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " .", StyleName.SearchField.name());
        style.addProperty("float", "left");
        style.addProperty("margin-left", "25px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchTypeBox.name(), " .", StyleName.SearchField.name(),
                ":first-child");
        style.addProperty("float", "left");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name());
        style.addProperty("float", "left");
        style.addProperty("margin-top", "30px");
        style.addProperty("clear", "both");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name());
        style.addProperty("float", "left");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(),
                ":first-child");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("display", "inline-block");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("float", "left");
        style.addProperty("clear", "both");
        style.addProperty("margin-top", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " br");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name());
        style.addProperty("display", "inline-block");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchWideWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name(), " label");
        style.addProperty("display", "inline-block");
        addStyle(style);

    }
}
