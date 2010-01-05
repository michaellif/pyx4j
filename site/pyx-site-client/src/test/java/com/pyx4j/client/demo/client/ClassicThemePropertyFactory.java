/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import com.pyx4j.site.client.ClassicThemeProperties;

public class ClassicThemePropertyFactory {

    public static ClassicThemeProperties getProperty() {
        //return getLightSkin();
        return getDarkSkin();
    }

    static ClassicThemeProperties getLightSkin() {
        ClassicThemeProperties property = new ClassicThemeProperties();
        property.background = "#F8F8F8";
        property.contentPanelWidth = 968;
        property.contentPanelTopMargin = 20;
        property.contentPanelBottomMargin = 20;
        property.headerHeight = 200;
        property.footerHeight = 100;
        property.headerBackground = "url('images/container-header.gif') no-repeat";
        property.footerBackground = "url('images/container-footer.gif') no-repeat 50% 100%";
        property.mainPanelBackground = "url('images/container-main.gif') repeat-y";
        property.headerCaptionsLeft = 60;
        property.headerCaptionsTop = 167;
        property.headerCaptionsColor = "#ff6600";

        return property;
    }

    static ClassicThemeProperties getDarkSkin() {
        ClassicThemeProperties property = new ClassicThemeProperties();
        property.background = "#21262C url('images/background.jpg') repeat-x";
        property.headerBackground = "url('images/topHdr_ecommerce.jpg') no-repeat";
        property.footerBackground = "url('images/topHdr_ecommerce.jpg') no-repeat 50% 100%";
        property.contentPanelWidth = 924;
        property.headerHeight = 250;
        property.footerHeight = 40;
        property.headerCaptionsLeft = 260;
        property.headerCaptionsTop = 167;
        property.headerCaptionsColor = "#ff6600";
        return property;
    }
}
