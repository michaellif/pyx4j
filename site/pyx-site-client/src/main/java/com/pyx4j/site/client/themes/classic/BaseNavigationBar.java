/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class BaseNavigationBar extends ComplexPanel {

    private final UListElement ul;

    public BaseNavigationBar() {
        ul = Document.get().createULElement();
        ul.getStyle().setProperty("listStyleType", "none");
        setElement(ul);
    }

    @Override
    public void add(Widget w) {
        Element li = Document.get().createLIElement().cast();
        //        li.getStyle().setProperty("float", "left");
        //        li.getStyle().setProperty("cssFloat", "left");
        li.getStyle().setProperty("display", "inline");
        //li.getStyle().setProperty("display", "block");

        ul.appendChild(li);
        add(w, li);
    }

}
