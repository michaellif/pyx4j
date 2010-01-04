/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.user.client.ui.SimplePanel;

public class PagePanel extends SimplePanel {

    public PagePanel() {
        add(new ContentPanel());

        getElement().getStyle().setBackgroundColor("#F8F8F8");

        //        getElement().getStyle().setBackgroundColor("#21262C");
        //        getElement().getStyle().setBackgroundImage("url('images/background.jpg')");
        //        getElement().getStyle().setProperty("backgroundRepeat", "repeat-x");
    }
}
