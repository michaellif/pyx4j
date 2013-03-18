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
package com.propertyvista.pmsite.server.skins.future;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

import com.propertyvista.pmsite.server.skins.PMSiteSkin;

public class Main extends Theme {

    public Main() {
        initCommonTagStyles();
        // TODO - add other common portal styles/themes as needed
        // ...
        initStyle();
    }

    protected void initCommonTagStyles() {
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

    public void initStyle() {
        Style style = new Style(".", PMSiteSkin.Styles.Header);
        style.addProperty("width", "100%");
        addStyle(style);
    }
}
