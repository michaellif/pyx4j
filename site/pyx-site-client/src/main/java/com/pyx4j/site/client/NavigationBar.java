/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class NavigationBar extends ComplexPanel {

    private final NavigationBarType type;

    private final Element ul;

    private final List<NavigationTab> tabs = new ArrayList<NavigationTab>();

    private NavigationTab firstTab;

    private NavigationTab lastTab;

    public NavigationBar(NavigationBarType type) {
        super();
        this.type = type;

        Element div = Document.get().createDivElement().cast();
        ul = Document.get().createULElement().cast();
        ul.getStyle().setProperty("listStyleType", "none");
        ul.getStyle().setProperty("height", "100%");
        ul.getStyle().setProperty("margin", "0");
        ul.getStyle().setProperty("padding", "0");
        ul.getStyle().setProperty("display", "inline");

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
        if (firstTab == null) {
            firstTab = tab;
        }
        lastTab = tab;
        tabs.add(tab);
        ul.appendChild(tab.getElement());
        add(tab, ul);
    }

    public void setSelected(String pageName) {
        for (NavigationTab tab : tabs) {
            tab.setSelected(tab.pageName == pageName);
        }
    }

    class NavigationTabAnchor extends Anchor {
        NavigationTabAnchor(String text, final String pageName) {
            super("<span>" + text + "</span>", true);

            getElement().getStyle().setProperty("outline", "0px");
            getElement().getStyle().setCursor(Cursor.POINTER);

            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    getParent().addStyleDependentName("mouseOver");
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    getParent().removeStyleDependentName("mouseOver");
                }
            });

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    History.newItem(pageName, true);
                }
            });

        }
    }

    class NavigationTab extends Panel {

        private final NavigationTabAnchor anchor;

        private final String pageName;

        NavigationTab(String text, final String pageName) {
            this.pageName = pageName;

            setElement(Document.get().createLIElement());
            UIObject.setStyleName(getElement(), SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());

            anchor = new NavigationTabAnchor(text, pageName);

            switch (type) {
            case Primary:
                getElement().getStyle().setProperty("display", "inline-block");
                anchor.getElement().getStyle().setProperty("display", "block");
                if (BrowserType.isFirefox()) {
                    anchor.getElement().getStyle().setProperty("cssFloat", "left");
                } else {
                    anchor.getElement().getStyle().setProperty("float", "left");
                }
                break;
            case Secondary:
                getElement().getStyle().setProperty("display", "block");
                break;
            default:
                getElement().getStyle().setProperty("display", "block");
                break;
            }

            DOM.appendChild(getElement(), anchor.getElement());
            adopt(anchor);

        }

        void setSelected(boolean flag) {
            if (flag) {
                addStyleDependentName("selected");
            } else {
                removeStyleDependentName("selected");

            }
        }

        void setFirst(boolean flag) {
            if (flag) {
                addStyleDependentName("first");
            } else {
                removeStyleDependentName("first");

            }
        }

        Anchor getAnchor() {
            return anchor;
        }

        @Override
        public boolean remove(Widget child) {
            return false;
        }

        @Override
        public Iterator<Widget> iterator() {
            return new Iterator<Widget>() {
                boolean hasElement = anchor != null;

                public boolean hasNext() {
                    return hasElement;
                }

                public Widget next() {
                    if (!hasElement || (anchor == null)) {
                        throw new NoSuchElementException();
                    }
                    hasElement = false;
                    return anchor;
                }

                public void remove() {
                }
            };
        }

    }
}
