/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 23, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

public class TransparentDeckPanel extends DeckPanel {

    @Override
    public void add(Widget w) {
        super.add(w, getElement());
        initChildWidget(w);
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        super.insert(w, getElement(), beforeIndex, true);
        initChildWidget(w);
    }

    private void initChildWidget(Widget w) {
        Element child = w.getElement();
        DOM.setStyleAttribute(child, "width", "100%");
        DOM.setStyleAttribute(child, "height", "100%");
        w.setVisible(true);
    }

    @Override
    public boolean remove(Widget w) {
        if (!super.remove(w)) {
            return false;
        }
        return true;
    }

    /**
     * Shows the widget at the specified index. This causes the currently- visible widget
     * to be hidden.
     * 
     * @param index
     *            the index of the widget to be shown
     */
    @Override
    public void showWidget(int index) {
        checkIndex(index);

    }

    private void checkIndex(int index) {
        if ((index < 0) || (index >= getWidgetCount())) {
            throw new IndexOutOfBoundsException();
        }
    }

}
