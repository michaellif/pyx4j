/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;

public class NavigationBar extends ComplexPanel {

    private final UListElement ul;

    private final Map<String, NavigationTab> tabs = new HashMap<String, NavigationTab>();

    public NavigationBar() {
        super();
        ul = Document.get().createULElement();
        ul.getStyle().setProperty("listStyleType", "none");
        setElement(ul);
    }

    public void add(String text, String pageName) {
        NavigationTab tab = new NavigationTab(text, pageName);
        tabs.put(pageName, tab);
        ul.appendChild(tab.getLiElement());
    }

    public void setSelected(String name) {
        for (String tabName : tabs.keySet()) {
            tabs.get(tabName).setSelected(tabName == name);
        }
    }

    class NavigationTab {
        Element li;

        Anchor anchor;

        NavigationTab(String text, String pageName) {
            anchor = new Anchor(text, "#" + pageName);
            anchor.getElement().getStyle().setProperty("margin", "6px");
            anchor.getElement().getStyle().setColor("green");
            anchor.getElement().getStyle().setProperty("outline", "0px");
            anchor.getElement().getStyle().setBackgroundImage(ImageFactory.getImages().divider().getURL());
            anchor.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
            anchor.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    // TODO Auto-generated method stub
                    anchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
                }
            });
            anchor.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    anchor.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
                }
            });

            anchor.getElement().getStyle().setCursor(Cursor.POINTER);

            li = Document.get().createLIElement().cast();
            //        li.getStyle().setProperty("float", "left");
            //        li.getStyle().setProperty("cssFloat", "left");
            li.getStyle().setProperty("display", "inline");
            //li.getStyle().setProperty("display", "block");
            add(anchor, li);
        }

        void setSelected(boolean flag) {
            if (flag) {
                anchor.getElement().getStyle().setColor("red");
            } else {
                anchor.getElement().getStyle().setColor("green");

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
