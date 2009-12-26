/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.style.Theme.CSSClass;

public class BarSeparator extends SimplePanel {

    public BarSeparator() {
        super(DOM.createSpan());
        setStyleName(CSSClass.pyx4j_BarSeparator.name());
        getElement().setInnerHTML("&nbsp;");
    }

}
