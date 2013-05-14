/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author TOSHIBA
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.power;

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

        Style style = new Style(".", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("border-right", "1px solid #D1D1D1 !important");
        addStyle(style);
    }

    @Override
    protected void initInfobox() {
        // TODO Auto-generated method stub
        super.initInfobox();

        Style style = new Style(".", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("font-size", "22px");
        style.addProperty("line-height", "40px");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxBody.name(), " .submit");
        style.addProperty("text-align", "center");
        addStyle(style);
    }

    @Override
    protected void initQuickSearchBox() {
        // TODO Auto-generated method stub
        super.initQuickSearchBox();

        Style style = new Style(".", StyleName.QuickSearchBox.name());
        style.addProperty("background", "#0458B3");
        style.addProperty("padding", "20px 30px");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("color", "#A4C5E8");
        style.addProperty("padding", "0 0 3px");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " select");
        style.addProperty("margin-bottom", "15px");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " input[type=submit]");
        style.addProperty("padding", "0");
        addStyle(style);

        //check this
        style = new Style(".", StyleName.QuickSearchBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("color", "#0458B3");
        addStyle(style);

        style = new Style(".narrowBox", " .", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("color", "#DFEBF7");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("color", "#0458B3 !important");
        addStyle(style);

    }

    @Override
    protected void initNewsBox() {
        // TODO Auto-generated method stub
        super.initNewsBox();

        Style style = new Style(".", StyleName.NewsBox.name());
        style.addProperty("padding", "20px 30px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.HeadLine.name());
        style.addProperty("color", "#00C4FF");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Date.name());
        style.addProperty("color", "#A3A3A3");
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Text.name());
        style.addProperty("padding-top", "10px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Text.name(), " .", StyleName.ReadMore.name());
        style.addProperty("color", "#00C4FF");
        style.addProperty("font-size", "16px");
        style.addProperty("padding-left", "10px");
        addStyle(style);
    }

    @Override
    protected void initPromoBox() {
        // TODO Auto-generated method stub
        super.initPromoBox();

        Style style = new Style(".", StyleName.PromoBox.name());
        style.addProperty("padding", "20px 10px 0");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("margin-left", "0px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.CitySelectionForm.name());
        style.addProperty("margin", "0 0 20px 20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("margin-left", "20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.Picture.name());
        style.addProperty("border", "1px solid #FFFFFF");
        addStyle(style);
    }

    @Override
    protected void initTestimBox() {
        // TODO Auto-generated method stub
        super.initTestimBox();

        Style style = new Style(".", StyleName.TestimBox.name());
        style.addProperty("padding", "20px 10px");
        addStyle(style);

        style = new Style(".", StyleName.ReadMore.name());
        style.addProperty("color", "#FF0000 !important");
        style.addProperty("font-size", "16px");
        style.addProperty("padding-left", "10px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxDecor.name());
        style.addProperty("margin", "20px 3px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("background", "#A4C5E8");
        style.addProperty("border", "1px solid #89B3E1");
        style.addProperty("border-radius", "15px 15px 15px 15px");
        style.addProperty("height", "100px");
        style.addProperty("margin", "10px");
        style.addProperty("padding", "20px");
        style.addProperty("width", "260px");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Name.name());
        style.addProperty("padding", "10px 0 0");
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Callout.name());
        style.addProperty("position", "absolute");
        style.addProperty("left", "250px");
        style.addProperty("top", "150px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.CalloutLine.name());
        style.addProperty("fill", "#A4C5E8");
        style.addProperty("stroke", "#89B3E1");
        style.addProperty("stroke-width", "1");
        addStyle(style);
    }

    @Override
    protected void initCommonStyle() {
        // TODO Auto-generated method stub
        super.initCommonStyle();

        Style style = new Style(".", StyleName.VistaPmsiteMain.name());
        style.addProperty("margin-left", "0");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteMainWrap.name());
        style.addProperty("background-color", "#FFFFFF");
        addStyle(style);

        style = new Style(".", StyleName.VistaPmsiteSidebar.name());
        style.addProperty("border-right", "1px solid #C1D7F0");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBoxField.name());
        style.addProperty("padding-bottom", "0px");
        addStyle(style);
    }

}
