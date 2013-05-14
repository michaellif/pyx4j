/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 26, 2013
 * @author Admin
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultAptListTheme;

public class AptListTheme extends DefaultAptListTheme {

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteContent.name());
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("width", "280px");
        style.addProperty("border-top", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteMain.name());
        style.addProperty("margin-left", "300px");
        addStyle(style);
    }

    @Override
    protected void initSecondaryNavig() {
        super.initSecondaryNavig();

        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " h3");
        style.addProperty("font-size", "14pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-weight", "normal");
        style.addProperty("padding", "5px 0");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.AdvancedSearchNarrowWrap.name());
        style.addProperty("border-top", "5px solid #000000");
        style.addProperty("background", "#999999");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.AdvancedSearchCriteria.name());
        style.addProperty("padding", "20px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("width", "90px");
        style.addProperty("margin-left", "30px");
        style.addProperty("padding", "3px 0");
        style.addProperty("background-color", "#CCCCCC");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " input[type=radio]");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " label");
        style.addProperty("color", "#E6E7E8");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), ".", StyleName.TypeselActiveHover.name());
        style.addProperty("background-color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), ".", StyleName.TypeselActiveHover.name(),
                " label");
        style.addProperty("color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.TypeselActive.name());
        style.addProperty("background-color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.TypeselActive.name(), " label");
        style.addProperty("color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " label");
        style.addProperty("color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name());
        style.addProperty("padding-left", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), " select");
        style.addProperty("width", "210px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), "  input[type=text]");
        style.addProperty("width", "210px");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name());
        style.addProperty("padding-left", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " select");
        style.addProperty("width", "92px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), "  input[type=text]");
        style.addProperty("width", "92px");
        style.addProperty("border", "1px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("width", "26px");
        style.addProperty("color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("border-top", "1px solid #FFFFFF");
        style.addProperty("margin-left", "30px");
        style.addProperty("width", "210px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name());
        style.addProperty("width", "190px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " input[type=submit]");
        style.addProperty("margin-top", "30px");
        addStyle(style);

    }
}
