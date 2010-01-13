/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.domain.CommandLink;
import com.pyx4j.site.client.domain.Link;
import com.pyx4j.site.client.domain.PageLink;
import com.pyx4j.site.client.domain.PageUri;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class LinkBar extends ComplexPanel {

    public static enum LinkBarType {
        Header, Footer
    }

    private final LinkBarType type;

    private final Element ul;

    private boolean empty = true;

    public LinkBar(LinkBarType type) {
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
        switch (type) {
        case Header:
            setStyleName(SiteCSSClass.pyx4j_Site_HeaderLinks.name());
            break;
        case Footer:
            setStyleName(SiteCSSClass.pyx4j_Site_FooterLinks.name());
            break;
        default:
            break;
        }

    }

    public void add(Link link) {
        LinkItem tab = null;
        if (link instanceof PageLink) {
            PageLink pageLink = (PageLink) link;
            tab = new LinkItem(new LinkItemAnchor(pageLink.html, pageLink.uri));
        } else if (link instanceof CommandLink) {
            CommandLink commandLink = (CommandLink) link;
            tab = new LinkItem(new LinkItemAnchor(commandLink.html, commandLink.command));
        }
        ul.appendChild(tab.getElement());
        add(tab, ul);
    }

    public void add(String html, Command command) {
        LinkItem tab = new LinkItem(new LinkItemAnchor(html, command));
        ul.appendChild(tab.getElement());
        add(tab, ul);
    }

    class LinkItemAnchor extends Anchor {

        LinkItemAnchor(String html, final PageUri uri) {
            super(html, true, "");

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    History.newItem(uri.getUri(), true);
                }
            });

            init();
        }

        LinkItemAnchor(String html, final Command command) {
            super(html, true, "");

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    command.execute();
                }
            });

            init();
        }

        private void init() {
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

        }

    }

    class LinkItem extends Panel {

        private final LinkItemAnchor anchor;

        LinkItem(LinkItemAnchor linkItemAnchor) {
            setElement(Document.get().createLIElement());
            switch (type) {
            case Header:
                UIObject.setStyleName(getElement(), SiteCSSClass.pyx4j_Site_HeaderLink.name());
                break;
            case Footer:
                UIObject.setStyleName(getElement(), SiteCSSClass.pyx4j_Site_FooterLink.name());
                break;
            default:
                break;
            }

            getElement().getStyle().setProperty("display", "inline");
            if (BrowserType.isFirefox()) {
                getElement().getStyle().setProperty("cssFloat", "left");
            } else {
                getElement().getStyle().setProperty("float", "left");
            }

            if (empty) {
                empty = false;
            } else {
                Element separator = Document.get().createSpanElement().cast();
                separator.getStyle().setProperty("display", "inline");
                separator.setInnerText("| ");
                DOM.appendChild(getElement(), separator);
            }

            anchor = linkItemAnchor;

            anchor.getElement().getStyle().setProperty("display", "inline");

            DOM.appendChild(getElement(), anchor.getElement());
            adopt(anchor);

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
