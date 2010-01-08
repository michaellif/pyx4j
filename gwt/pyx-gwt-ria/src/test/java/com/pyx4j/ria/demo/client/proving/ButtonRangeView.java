/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.pyx4j.ria.client.ImageFactory;
import com.pyx4j.widgets.client.Button;

public class ButtonRangeView extends AbstractProvingView {

    public ButtonRangeView() {
        super("Button Range", ImageFactory.getImages().debugOn());
        super.setDescription("Test Buttons styles");

        createWidgetTestActionGroup("GWT Button", new com.google.gwt.user.client.ui.Button("Click Button"));

        createWidgetTestActionGroup("Button", new Button("Click Button"));

        createWidgetTestActionGroup("Toggle Button", new com.pyx4j.widgets.client.ToggleButton("Toggle"));
    }

}
