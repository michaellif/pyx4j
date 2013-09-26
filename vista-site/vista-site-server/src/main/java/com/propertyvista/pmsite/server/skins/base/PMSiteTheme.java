/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeDescriminator;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteStyleFactory;
import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public abstract class PMSiteTheme extends PMSiteThemeBase {

    public static enum Stylesheet implements ThemeDescriminator {
        AptDetails, AptList, BuildingInfoPanel, CityPage, Error, FindApt, FloorplanInfoPanel, Inquiry, InquiryOk, InquiryPanel, Landing, Resident, Static, Terms, UnitDetails
    }

    public PMSiteTheme(Stylesheet stylesheet) {
        super();

        try {
            addTheme(PMSiteStyleFactory.create(getClass(), stylesheet));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {

        initCommonStyle();
    }

    private void initCommonStyle() {
        Style style = new Style("img,form,fieldset");
        style.addProperty("border", "medium none");
        addStyle(style);

        style = new Style("form,fieldset");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("body,h1,h2,h3,h4,h5,h6,dl,dt,dd,p");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("q");
        style.addProperty("quotes", "none");
        addStyle(style);

        style = new Style("textarea:focus");
        style.addProperty("outline", "medium none");
        addStyle(style);

        style = new Style("html,body");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style("table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0");
        addStyle(style);
    }

}
