/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 13, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class LegendPanel extends ComplexPanel {

    public LegendPanel() {
        super();
        setElement(Document.get().createLegendElement());
    }
    
    @Override
    public void add(Widget w) {
      add(w, getElement());
    }
}
