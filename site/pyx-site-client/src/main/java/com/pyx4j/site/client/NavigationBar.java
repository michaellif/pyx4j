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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class NavigationBar extends ComplexPanel {

    private final NavigationBarType type;

    private final UListElement ul;

    private final List<NavigationTab> tabs = new ArrayList<NavigationTab>();

    public NavigationBar(NavigationBarType type) {
        super();
        this.type = type;

        Element div = Document.get().createDivElement().cast();
        ul = Document.get().createULElement();
        ul.getStyle().setProperty("listStyleType", "none");
        ul.getStyle().setProperty("height", "100%");
        ul.getStyle().setProperty("margin", "0");
        ul.getStyle().setProperty("paddingTop", "4px");

        if (BrowserType.isFirefox()) {
            ul.getStyle().setProperty("cssFloat", "left");
        } else {
            ul.getStyle().setProperty("float", "left");
        }

        div.appendChild(ul);
        setElement(div);
        setStyleName(SiteCSSClass.pyx4j_Site_PrimaryNavig.name());
    }

    public void add(String text, String pageName) {
        NavigationTab tab = new NavigationTab(text, pageName);
        tabs.add(tab);
        ul.appendChild(tab.getLiElement());
    }

    public void setSelected(String pageName) {
        for (NavigationTab tab : tabs) {
            tab.setSelected(tab.pageName == pageName);
        }
    }

    class NavigationTab {

        String text;

        Element li;

        Anchor anchor;

        String pageName;

        NavigationTab(String text, final String pageName) {
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

            li = Document.get().createLIElement().cast();

            anchor.setStyleName(SiteCSSClass.pyx4j_Site_PrimaryNavigTabAnchor.name());
            UIObject.setStyleName(li, SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());

            switch (type) {
            case Primary:
                li.getStyle().setProperty("display", "inline");
                break;
            case Secondary:
                li.getStyle().setProperty("display", "block");
                break;
            default:
                li.getStyle().setProperty("display", "block");
                break;
            }
            add(anchor, li);
        }

        void setSelected(boolean flag) {
            if (flag) {
                anchor.addStyleDependentName("selected");
            } else {
                anchor.removeStyleDependentName("selected");

            }
        }

        Element getLiElement() {
            return li;
        }

        Anchor getAnchor() {
            return anchor;
        }

    }
}
