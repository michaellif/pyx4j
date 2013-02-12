/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.commons.css;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleInjector;

public class StyleManger {

    private static final Logger log = LoggerFactory.getLogger(StyleManger.class);

    public static final String DO_NOT_PRINT_CLASS_NAME = "do-not-print";

    private static class SingletonHolder {
        public static final StyleManger INSTANCE = new StyleManger();
    }

    public static StyleManger instance() {
        return SingletonHolder.INSTANCE;
    }

    private Theme theme;

    private Palette palette;

    private static String[] alternativeHostnames;

    private static int alternativeHostnameIdx;

    private StyleManger() {
    }

    public static void setAlternativeHostnames(String... names) {
        alternativeHostnames = names;
        alternativeHostnameIdx = 0;
    }

    public static void setAlternativeHostnameSufix(String host, String hostName, String... sufix) {
        alternativeHostnames = new String[sufix.length];
        String domainName = host.substring(host.lastIndexOf('.', host.lastIndexOf('.') - 1));
        for (int i = 0; i < sufix.length; i++) {
            alternativeHostnames[i] = hostName + sufix[i] + domainName;
        }
        alternativeHostnameIdx = 0;
    }

    public static String getAlternativeHostname() {
        if (alternativeHostnames == null) {
            return "";
        }
        int idx = alternativeHostnameIdx;
        alternativeHostnameIdx++;
        if (alternativeHostnameIdx >= alternativeHostnames.length) {
            alternativeHostnameIdx = 0;
        }
        return "http://" + alternativeHostnames[idx] + "/";
    }

    public static void installTheme(Theme theme, Palette palette) {
        if (instance().theme != null && instance().theme.equals(theme) && instance().palette != null && instance().palette.equals(palette)) {
            return;
        }
        instance().theme = theme;
        instance().palette = palette;
        alternativeHostnameIdx = 0;

        String themeStr = getThemeString();

        cleanUpInjectedStyles();
        log.debug("install style {} ", theme.getClass().getName());
        log.trace("{}", themeStr);
        StyleInjector.inject(themeStr, true);
    }

    private static void cleanUpInjectedStyles() {
        Element head = Document.get().getElementsByTagName("head").getItem(0);
        if (head == null) {
            // Let GWT StyleInjector throw exception 
            return;
        }
        NodeList<Element> styleElements = head.getElementsByTagName("style");
        if (styleElements != null) {
            ArrayList<Element> styleElementsCahe = new ArrayList<Element>();
            for (int i = 0; i < styleElements.getLength(); i++) {
                styleElementsCahe.add(styleElements.getItem(i));
            }
            for (Element element : styleElementsCahe) {
                element.removeFromParent();
            }
        }
    }

    public static Theme getTheme() {
        return instance().theme;
    }

    public static Palette getPalette() {
        return instance().palette;
    }

    public static String getThemeString() {
        StringBuilder stylesString = new StringBuilder();

        // Add do-not-print css: 
        stylesString.append("@media print {");
        stylesString.append("." + DO_NOT_PRINT_CLASS_NAME + "{display: none;}");
        stylesString.append("}");

        for (Style style : instance().theme.getAllStyles()) {
            stylesString.append(style.toString(instance().theme, instance().palette));
        }

        return stylesString.toString();
    }
}
