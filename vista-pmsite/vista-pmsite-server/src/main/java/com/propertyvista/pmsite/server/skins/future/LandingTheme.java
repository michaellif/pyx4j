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
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;

import com.propertyvista.pmsite.server.skins.base.DefaultLandingTheme;

public class LandingTheme extends DefaultLandingTheme {

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    protected void initBanner() {
        super.initBanner();

        Style style = new Style(".", StyleName.Banner.name());
        style.addProperty("border-top", "5px solid #000000");
        style.addProperty("margin-bottom", "30px");
        addStyle(style);
    }

    @Override
    protected void initInfobox() {
        super.initInfobox();

        Style style = new Style(".", StyleName.Infobox.name());
        addStyle(style);

        style = new Style(".", StyleName.InfoboxHeader.name());
        style.addProperty("border-top", "1px solid #000000");
        style.addProperty("padding", "5px 0");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxHeader.name(), " .", StyleName.ReadMore.name());
        style.addProperty("color", "#fff");
        style.addProperty("background", "#000000");
        style.addProperty("padding", "6px");
        style.addProperty("margin-left", "2em");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxHeaderTitle.name());
        style.addProperty("font-family", "monospace");
        style.addProperty("font-size", "14pt");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxBody.name());
        style.addProperty("border-top", "5px solid #000000");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxBody.name(), " .", StyleName.ReadMore.name());
        style.addProperty("color", "#808080");
        style.addProperty("font-size", "20px");
        style.addProperty("line-height", "14px");
        style.addProperty("vertical-align", "text-top");
        addStyle(style);

        style = new Style(".", StyleName.InfoboxContent.name());
        addStyle(style);

    }

    @Override
    protected void initQuickSearchBox() {

        super.initQuickSearchBox();

        Style style = new Style(".", StyleName.QuickSearchBox.name());
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("background", "#999999");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " label");
        style.addProperty("display", "block");
        style.addProperty("color", "#fff");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.QuickSearchBox.name(), " select");
        style.addProperty("width", "240px");
        style.addProperty("margin", "0 0 6px 0");
        addStyle(style);

    }

    @Override
    protected void initNewsBox() {

        super.initNewsBox();

        Style style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("margin-bottom", "20px");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Date.name());
        style.addProperty("color", "#828282");
        style.addProperty("font-size", "12px");
        style.addProperty("margin", "12px 0 4px 0");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.HeadLine.name());
        style.addProperty("display", "block");
        style.addProperty("color", "#000000");
        style.addProperty("font-size", "12pt");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-transform", "uppercase");
        style.addProperty("margin-bottom", "5px");
        style.addProperty("line-height", "100%");
        addStyle(style);

        style = new Style(".", StyleName.NewsBox.name(), " .", StyleName.Text.name());
        style.addProperty("font-size", "9pt");
        style.addProperty("text-align", "justify");
        style.addProperty("line-height", "11pt");
        addStyle(style);

    }

    @Override
    protected void initPromoBox() {
        super.initPromoBox();

        Style style = new Style(".", StyleName.PromoBox.name());
        style.addProperty("margin-bottom", "20px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("border-top", "5px solid #000000");
        style.addProperty("margin-right", "5px");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.Address.name());
        style.addProperty("color", "#000000");
        addStyle(style);

        style = new Style(".", StyleName.PromoBox.name(), " .", StyleName.CitySelectionForm.name());
        style.addProperty("margin", "10px 0");
        addStyle(style);

    }

    @Override
    protected void initTestimBox() {
        super.initTestimBox();

        Style style = new Style(".", StyleName.TestimBox.name());
        style.addProperty("margin-bottom", "20px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxDecor.name());
        style.addProperty("float", "right");
        style.addProperty("max-height", "220px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxDecor.name(), ":first-child");
        style.addProperty("float", "left");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxContent.name());
        style.addProperty("width", "255px");
        style.addProperty("padding", "20px");
        style.addProperty("background", "#CCCCCC");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-size", "12pt");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.InfoboxBody.name());
        style.addProperty("padding-top", "15px");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Name.name());
        style.addProperty("color", "#000000");
        style.addProperty("padding", "10px 0 0 0");
        style.addProperty("text-align", "right");
        style.addProperty("font-family", "monospace");
        style.addProperty("font-size", "12pt");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.Callout.name());
        style.addProperty("position", "relative");
        style.addProperty("left", "135px");
        style.addProperty("max-height", "7%");
        addStyle(style);

        style = new Style(".", StyleName.TestimBox.name(), " .", StyleName.CalloutLine.name());
        style.addProperty("fill", "#CCCCCC");
        style.addProperty("stroke", "#CCCCCC");
        style.addProperty("stroke-width", "1");
        addStyle(style);

    }
}
