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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.site.client.themes.classic.AnchorNavigationBar;
import com.pyx4j.site.client.themes.classic.ContentPanel;
import com.pyx4j.site.client.themes.classic.PagePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SiteDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(SiteDemo.class);

    public void onModuleLoad() {

        PagePanel pagePanel = new PagePanel();

        {
            //            SiteSection centerPanel = new SiteSection("Center", "red");
            //            centerPanel.setSize("500px", "1000px");
            //            rootPanel.add(centerPanel);
            //            rootPanel.setCellWidth(centerPanel, "500px");
            //            rootPanel.setCellHorizontalAlignment(centerPanel, HasHorizontalAlignment.ALIGN_CENTER);

            //            AnchorNavigationBar navig = new AnchorNavigationBar();
            //            {
            //                Anchor anchor = new Anchor("aaaa1", "aaaa1");
            //                navig.add(anchor);
            //            }
            //            {
            //                Anchor anchor = new Anchor("bbbb", "bbbb");
            //                navig.add(anchor);
            //            }
            //            {
            //                Anchor anchor = new Anchor("cccc", "cccc");
            //                navig.add(anchor);
            //            }
            //            rootPanel.add(navig);
        }

        RootPanel.get().add(pagePanel);

    }

    class SiteSection extends HTML {
        SiteSection(String capture, String color) {
            super(capture);
            DOM.setStyleAttribute(getElement(), "background", color);
        }
    }

}
