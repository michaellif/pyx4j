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
 * Created on Apr 23, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

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
