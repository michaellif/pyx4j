/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.client.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.site.client.SitePanel;
import com.pyx4j.site.client.domain.Site;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SiteDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(SiteDemo.class);

    public void onModuleLoad() {

        Site site = new EasySite();

        SitePanel pagePanel = new SitePanel(site);
        RootPanel.get().add(pagePanel);

    }

    class SiteSection extends HTML {
        SiteSection(String capture, String color) {
            super(capture);
            DOM.setStyleAttribute(getElement(), "background", color);
        }
    }

}
