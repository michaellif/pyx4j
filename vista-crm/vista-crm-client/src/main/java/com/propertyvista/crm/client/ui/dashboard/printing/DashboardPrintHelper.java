/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-01
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.dashboard.printing;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;

public class DashboardPrintHelper {

    private static final List<String> PRINT_EL_CLASS_BLACKLIST = Arrays.asList("Button", "DataTableActionsBar", "DataTableColumnSelector");

    private static class DashboardPrintPalete extends Palette {

    }

    private static class DashboardPrintTheme extends Theme {

        public DashboardPrintTheme() {
            Style style = new Style("*");
            style.addProperty("color", "black");
            style.addProperty("font-size", "12px");
            addStyle(style);

            style = new Style("a, a:link, a:visited, a:hover, a:active");
            style.addProperty("color", "black");
            style.addProperty("text-decoration", "none");
            addStyle(style);

            style = new Style(".pyx4j_BoardHolderCaption");
            style.addProperty("font-weight", "bold");
            style.addProperty("font-size", "1.3em");
            addStyle(style);

            style = new Style(".pyx4j_BoardHolder");
            style.addProperty("margin-bottom", "0.3em");
            style.addProperty("margin-right", "0.3em");
            style.addProperty("border-width", "1px");
            style.addProperty("border-color", "black");
            style.addProperty("border-style", "solid");
            style.addProperty("padding", "5px");
            addStyle(style);

            style = new Style(".FormFlexPanelH2");
            style.addProperty("border-bottom", "2px solid black");
            style.addProperty("margin", "6px 0 4px");
            style.addProperty("width", "100%");

            addStyle(style);
            style = new Style(".FormFlexPanelH2Label");
            style.addProperty("color", "black");
            style.addProperty("font-size", "1.2em");
            style.addProperty("padding", "3px");
            addStyle(style);

            style = new Style("." + CounterGadgetInstanceBase.StyleNames.CounterGadgetCaption);
            style.addProperty("color", "black");
            style.addProperty("font-size", "1.1em");
            style.addProperty("font-weight", "bold");
            addStyle(style);
        }

        @Override
        public final ThemeId getId() {
            return new ClassBasedThemeId(getClass());
        }
    }

    private static String printTheme;

    static {
        StringBuilder stylesString = new StringBuilder();
        DashboardPrintTheme theme = new DashboardPrintTheme();
        Palette palette = new DashboardPrintPalete();
        for (Style style : theme.getAllStyles()) {
            stylesString.append(style.getCss(theme, palette));
        }
        printTheme = stylesString.toString();
    }

    public static String makePrintLayout(Element element) {
        StringBuilder printLayoutBuilder = new StringBuilder();
        printLayoutBuilder.append("<!doctype html>");
        printLayoutBuilder.append("<html><head><style type=\"text/css\">");
        printLayoutBuilder.append(printTheme);
        printLayoutBuilder.append("</style></head>");

        printLayoutBuilder.append("<body>");
        clean(element);
        printLayoutBuilder.append(DOM.toString(element));
        printLayoutBuilder.append("</body></html>");
        return printLayoutBuilder.toString();
    }

    private static void clean(Node n) {

        if (hasDefinedType(n) && n.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) n;
            if (e.getTagName().endsWith("svg")) {
                return;
            } else if (PRINT_EL_CLASS_BLACKLIST.contains(e.getClassName())) {
                e.removeFromParent();
            } else {
                // remove scroll bars
                String overflow = e.getStyle().getOverflow();
                if ("auto".equals(overflow) | "scroll".equals(overflow)) {
                    e.getStyle().setOverflow(Overflow.HIDDEN);
                }
                NodeList<Node> children = n.getChildNodes();
                final int len = children.getLength();
                for (int i = 0; i < len; ++i) {
                    clean(children.getItem(i));
                }
            }
        }
    }

    /** this hack is required since for some unknown reason (for me) some nodes fail with getNodeType, especially in production mode and IE */
    private static native boolean hasDefinedType(JavaScriptObject n) /*-{
		return n != null && n.nodeType != undefined;
    }-*/;

}
