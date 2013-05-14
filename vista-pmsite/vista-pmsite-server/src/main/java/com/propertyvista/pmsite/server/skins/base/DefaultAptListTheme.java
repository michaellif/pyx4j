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
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public class DefaultAptListTheme extends PMSiteThemeBase {

    public static enum StyleName implements IStyleName {

        VistaPmsiteAptListPage, VistaPmsiteContent, VistaPmsiteSidebar, VistaPmsiteMain,

        AdvancedSearchNarrowWrap, SecondaryNavig, AdvancedSearchCriteria, Caption2, ApartmentListingWrap, SearchTypeSelBox, SearchTypeSelButton, TypeselActive, TypeselActiveHover, SearchTypeBox, InputBlockFrame, SearchCommonBox, SearchField, InputJoinBox, SearchAmenityBox, AmenityList, AptListModeSwitch, AptListMode_List, AptListMode_Map,

        ErrorPanel,

        ListingMapview, ListingListview, ListingEntry, AptInfoMapModal,

        PictureBox, InfoBox, ControlBox,

        InfoBoxEntry, ControlButton,

        Address, Types, Amenities, Description,

        Picture, InfoBoxCaption, InfoBoxText,

        ListingCtlButton,

        BtnShowOnMap, BtnViewDetails,

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        initCommonStyle();
        initSecondaryNavig();
        initApartmentListingWrap();
    }

    protected void initCommonStyle() {
        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.ErrorPanel.name());
        style.addProperty("padding", "2px");
        style.addProperty("padding-left", "15px");
        style.addProperty("margin", "0");
        style.addProperty("margin-top", "10px");
        style.addProperty("color", "red");
        style.addProperty("background", "white");
        style.addProperty("display", "inline-block");
        addStyle(style);
    }

    protected void initSecondaryNavig() {
        Style style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelBox.name());
        style.addProperty("height", "40px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("float", "left");
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "14px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " input[type=radio]");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), " label");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("white-space", "nowrap");
        style.addProperty("padding", "4px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.InputBlockFrame.name());
        style.addProperty("margin-top", "20px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name());
        style.addProperty("height", "100px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), " select");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), "  input[type=text]");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " select");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), "  input[type=text]");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("display", "inline-block");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.SearchField.name(), ">label");
        style.addProperty("display", "block");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name());
        style.addProperty("display", "block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name(), " label");
        style.addProperty("width", "70px");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name(), " .", StyleName.AmenityList.name(),
                " label:lang(fr)");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.AmenityList.name(), " br");
        style.addProperty("display", "none");
        addStyle(style);
    }

    protected void initApartmentListingWrap() {
        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteSidebar.name());
        addStyle(style);

    }

}
