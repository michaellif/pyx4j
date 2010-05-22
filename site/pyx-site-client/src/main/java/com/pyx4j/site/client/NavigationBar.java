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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.util.ResourceUriUtil;
import com.pyx4j.widgets.client.util.BrowserType;

public class NavigationBar extends ComplexPanel {

    public static enum NavigationBarType {
        Primary, Secondary
    }

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
        ul.getStyle().setProperty("display", "inline-block");

        if (BrowserType.isFirefox()) {
            ul.getStyle().setProperty("cssFloat", "left");
        } else {
            ul.getStyle().setProperty("float", "left");
        }

        div.appendChild(ul);
        setElement(div);
        setStyleName(SiteCSSClass.pyx4j_Site_PrimaryNavig.name());

    }

    public void add(String text, String uri) {
        NavigationTab tab = new NavigationTab(text, uri);
        if (firstTab == null) {
            firstTab = tab;
            firstTab.addStyleDependentName("first");
        }
        if (lastTab != null) {
            lastTab.removeStyleDependentName("last");
        }
        lastTab = tab;
        lastTab.addStyleDependentName("last");

        tabs.add(tab);
        ul.appendChild(tab.getElement());
        add(tab, ul);
    }

    public void setSelected(String uri) {
        for (NavigationTab tab : tabs) {
            tab.setSelected(ResourceUriUtil.isContained(tab.uri, uri));
        }
    }

    class NavigationTabAnchor extends Anchor {
        NavigationTabAnchor(String text, final String uri) {
            super("<span>" + text + "</span>", true);

            getElement().getStyle().setProperty("outline", "0px");
            getElement().getStyle().setCursor(Cursor.POINTER);

            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    getParent().addStyleDependentName("hover");
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    getParent().removeStyleDependentName("hover");
                }
            });

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AbstractSiteDispatcher.show(uri);
                    getParent().removeStyleDependentName("hover");
                    // Prevent IE from triggering Window.ClosingEvent
                    event.preventDefault();
                }
            });

        }
    }

    class NavigationTab extends Panel {

        private final NavigationTabAnchor anchor;

        private final String uri;

        NavigationTab(String text, final String uri) {
            this.uri = uri;

            setElement(Document.get().createLIElement());
            UIObject.setStyleName(getElement(), SiteCSSClass.pyx4j_Site_PrimaryNavigTab.name());

            anchor = new NavigationTabAnchor(text, uri);

            switch (type) {
            case Primary:
                if (BrowserType.isIE8()) {
                    getElement().getStyle().setProperty("display", "inline-block");
                } else {
                    getElement().getStyle().setProperty("display", "inline");
                }
                anchor.getElement().getStyle().setProperty("display", "inline-block");
                if (BrowserType.isFirefox()) {
                    getElement().getStyle().setProperty("cssFloat", "left");
                } else {
                    getElement().getStyle().setProperty("float", "left");
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
