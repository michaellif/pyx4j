/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 30, 2013
 * @author edovgal
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.power;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultAptListTheme;

public class AptListTheme extends DefaultAptListTheme {

    @Override
    public void initStyle() {
        // TODO Auto-generated method stub
        super.initStyle();
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("width", "211px");
        style.addProperty("height", "711px");
        style.addProperty("color", "#767676");
        style.addProperty("background-color", "#DFEBF7");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteMain.name());
        style.addProperty("margin-left", "211px");
        style.addProperty("border-left", "1px solid #D1D1D1");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " .", StyleName.VistaPmsiteMain.name(), " .", StyleName.Caption2.name(), " h1");
        style.addProperty("color", "#0458B3");
        addStyle(style);

        style = new Style(".", StyleName.Caption2.name(), " #aptListModeSwitch");
        style.addProperty("width", "120px");
        style.addProperty("height", "25px");
        style.addProperty("margin-top", "20px");
        style.addProperty("padding-left", "30px");
        style.addProperty("padding-top", "5px");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("font-weight", "bold");
        style.addProperty("float", "right");
        style.addProperty("cursor", "pointer");
        style.addProperty("color", "#8D8D8D");
//        style.addProperty("background-image", "url(images/aptListIcons.png)");
        style.addProperty("background-repeat", "no-repeat");
        addStyle(style);

    }

    @Override
    protected void initSecondaryNavig() {
        // TODO Auto-generated method stub
        super.initSecondaryNavig();

        Style style = new Style(".", StyleName.VistaPmsiteAptListPage.name(), " h3");
        style.addProperty("color", "#0458B3");
        style.addProperty("font-size", "14px");
        style.addProperty("text-align", "center");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-top", "5px");
        style.addProperty("padding", "10px 0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name());
        style.addProperty("width", "190px");
        style.addProperty("margin", "10px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name(), ":first-child");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeSelButton.name());
        style.addProperty("width", "84px");
        style.addProperty("margin-left", "10px");
        style.addProperty("padding", "3px");
        style.addProperty("background-color", "#E8E8E8");
        style.addProperty("color", "#1A1A1A");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.TypeselActive.name());
        style.addProperty("color", "#E8E8E8 !important");
        style.addProperty("background-color", "#767676 !important");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), " select");
        style.addProperty("width", "188px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchTypeBox.name(), "  input[type=text]");
        style.addProperty("width", "182px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " select");
        style.addProperty("margin", "1px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), " .", StyleName.SearchField.name(), " .",
                StyleName.InputJoinBox.name());
        style.addProperty("width", "22px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchCommonBox.name(), "  input[type=text]");
        style.addProperty("width", "76px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " .", StyleName.SearchAmenityBox.name());
        style.addProperty("border-top", "1px dotted #767676");
        style.addProperty("margin-bottom", "30px");
        addStyle(style);

        style = new Style(".", StyleName.AdvancedSearchNarrowWrap.name(), " input[type=submit]");
        style.addProperty("padding", "0");
        style.addProperty("margin-left", "31px");
        addStyle(style);

    }

    @Override
    protected void initApartmentListingWrap() {
        // TODO Auto-generated method stub
        super.initApartmentListingWrap();

        Style style = new Style(".", StyleName.ListingMapview.name());
        style.addProperty("width", "744px");
        style.addProperty("height", "650px");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name());
        style.addProperty("border-bottom", "1px solid #D1D1D1");
        style.addProperty("padding", "20px");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), ":last-child");
        style.addProperty("border-bottom", "none");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), ":hover");
        style.addProperty("background-color", "#DFEBF7");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.PictureBox.name());
        style.addProperty("width", "80px");
        style.addProperty("display", "inline-block");
        style.addProperty("min-height", "75px");
        style.addProperty("text-align", "center");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.PictureBox.name(), " .",
                StyleName.Picture.name());
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name());
        style.addProperty("width", "450px");
        style.addProperty("display", "inline-block");
        style.addProperty("padding", "0 20px");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBoxEntry.name());
        style.addProperty("padding", "4px 0");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBoxEntry.name(), " .",
                StyleName.InfoBoxCaption.name());
        style.addProperty("width", "80px");
        style.addProperty("padding-right", "8px");
        style.addProperty("display", "inline-block");
        style.addProperty("font-weight", "normal");
        style.addProperty("text-align", "right");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBoxEntry.name(), " .",
                StyleName.InfoBoxText.name());
        style.addProperty("width", "340px");
        style.addProperty("display", "inline-block");
        style.addProperty("font-weight", "bold");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name(), " ul", ".",
                StyleName.InfoBoxText.name());
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name(), " .",
                StyleName.Types.name(), " li");
        style.addProperty("list-style", "none outside none");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBoxEntry.name(), " .",
                StyleName.InfoBoxText.name(), " .typeInfo");
        style.addProperty("font-weight", "normal");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name(), " .",
                StyleName.Amenities.name(), " li:first-child");
        style.addProperty("list-style", "none outside none");
        style.addProperty("margin-left", "0");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name(), " .",
                StyleName.Amenities.name(), " li");
        style.addProperty("float", "left");
        style.addProperty("margin", "0 10px");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.InfoBox.name(), " .",
                StyleName.Description.name(), " .", StyleName.InfoBoxText.name());
        style.addProperty("font-weight", "normal");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.ControlBox.name());
        style.addProperty("width", "80px");
        style.addProperty("display", "inline-block");
        style.addProperty("padding", "0 20px");
        style.addProperty("vertical-align", "top");
        addStyle(style);

        style = new Style(".", StyleName.ApartmentListingWrap.name(), " .", StyleName.ListingEntry.name(), " .", StyleName.ControlButton.name());
        style.addProperty("border-bottom", "1px solid #D1D1D1");
        style.addProperty("float", "right");
        style.addProperty("padding", "6px 0");
        addStyle(style);

        style = new Style("a.", StyleName.ListingCtlButton.name());
        style.addProperty("color", "#8D8D8D !important");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style(".", StyleName.BtnShowOnMap.name());
        style.addProperty("background-position", "0 0");
        addStyle(style);

        style = new Style(".", StyleName.ListingCtlButton.name());
        style.addProperty("width", "70px");
        style.addProperty("height", "25px");
        style.addProperty("padding-left", "30px");
        style.addProperty("padding-top", "5px");
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#8D8D8D");
        style.addProperty("display", "block");
        style.addProperty("cursor", "pointer");
        style.addProperty("background-repeat", "no-repeat");
//        style.addProperty("background-image", "url("images/aptListIcons.png")");
        addStyle(style);

        style = new Style(".", StyleName.ListingCtlButton.name(), ".", StyleName.BtnShowOnMap.name(), ":hover");
//        style.addProperty("background-image", "url("images/map_for_redridge.png")");
        addStyle(style);

        style = new Style(".", StyleName.ListingCtlButton.name(), ":hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style(".", StyleName.BtnViewDetails.name());
        style.addProperty("background-position", "0 -30px");
        addStyle(style);

        style = new Style(".", StyleName.ListingCtlButton.name(), ".", StyleName.BtnViewDetails.name(), ":hover");
//      style.addProperty("background-image", "url("images/details_for_redridge.png")");
        style.addProperty("background-position", "0 2px");
        addStyle(style);
    }

}
