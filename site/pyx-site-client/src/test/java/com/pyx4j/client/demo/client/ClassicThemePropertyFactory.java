/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import com.pyx4j.site.client.themes.classic.ClassicThemeProperty;

public class ClassicThemePropertyFactory {

    public static ClassicThemeProperty getProperty() {
        return getLightSkin();
        //return getDarkSkin();
    }

    static ClassicThemeProperty getLightSkin() {
        ClassicThemeProperty property = new ClassicThemeProperty();
        property.setBackground("#F8F8F8");
        property.setContentPanelWidth(968);
        property.setContentPanelTopMargin(20);
        property.setContentPanelBottomMargin(20);
        property.setHeaderHeight(200);
        property.setFooterHeight(100);
        property.setHeaderBackground("url('images/container-header.gif') no-repeat");
        property.setFooterBackground("url('images/container-footer.gif') no-repeat 50% 100%");
        property.setMainPanelBackground("url('images/container-main.gif') repeat-y");

        return property;
    }

    static ClassicThemeProperty getDarkSkin() {
        ClassicThemeProperty property = new ClassicThemeProperty();
        property.setBackground("#21262C url('images/background.jpg') repeat-x");
        property.setContentPanelWidth(924);
        property.setHeaderHeight(400);
        property.setFooterHeight(50);
        return property;
    }

}
