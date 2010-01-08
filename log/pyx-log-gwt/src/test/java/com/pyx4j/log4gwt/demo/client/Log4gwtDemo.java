/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Log4gwtDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final VerticalPanel mainPanel = new VerticalPanel();
        RootPanel.get().add(mainPanel);

        mainPanel.add(new LogEventsPanel());

    }

}
