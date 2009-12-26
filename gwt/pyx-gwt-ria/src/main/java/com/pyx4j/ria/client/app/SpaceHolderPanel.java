/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SpaceHolderPanel extends SimplePanel {

    SpaceHolderPanel() {
        setVisible(false);
    }

    @Override
    public void add(Widget w) {
        super.add(w);
        setVisible(true);
    }

    @Override
    public void clear() {
        super.clear();
        setVisible(false);
    }

}
