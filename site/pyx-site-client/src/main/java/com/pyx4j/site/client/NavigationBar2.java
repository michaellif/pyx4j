/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class NavigationBar2 extends FlowPanel {

    private final NavigationBarType type;

    private final List<NavigationTab> tabs = new ArrayList<NavigationTab>();

    public NavigationBar2(NavigationBarType type) {
        super();
        this.type = type;

        setStyleName(SiteCSSClass.pyx4j_Site_PrimaryNavig.name());

    }

    public void add(String text, String pageName) {
        NavigationTab tab = new NavigationTab(text, pageName);
        tabs.add(tab);
        add(tab);
    }

    public void setSelected(String pageName) {
        for (NavigationTab tab : tabs) {
            tab.setSelected(tab.pageName == pageName);
        }
    }

    class NavigationTab extends HorizontalPanel {

        String text;

        Anchor anchor;

        String pageName;

        NavigationTab(String text, final String pageName) {
            super();
            this.text = text;
            this.pageName = pageName;

            anchor = new Anchor(text);

            anchor.getElement().getStyle().setProperty("outline", "0px");
            anchor.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            anchor.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    anchor.addStyleDependentName("mouseOver");
                }
            });
            anchor.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    anchor.removeStyleDependentName("mouseOver");
                }
            });

            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    History.newItem(pageName, true);
                }
            });

            anchor.getElement().getStyle().setCursor(Cursor.POINTER);

            switch (type) {
            case Primary:
                //anchor.setStyleName(SiteCSSClass.pyx4j_Site_PrimaryNavigTabAnchor.name());
                getElement().getStyle().setProperty("float", "left");
                if (BrowserType.isFirefox()) {
                    getElement().getStyle().setProperty("cssFloat", "left");
                }
                if (BrowserType.isIE()) {
                    getElement().getStyle().setProperty("display", "inline");
                }
                break;
            case Secondary:
                break;
            default:
                break;
            }

            add(anchor);
            setCellVerticalAlignment(anchor, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        void setSelected(boolean flag) {
            if (flag) {
                anchor.addStyleDependentName("selected");
            } else {
                anchor.removeStyleDependentName("selected");

            }
        }

        Anchor getAnchor() {
            return anchor;
        }

    }
}
