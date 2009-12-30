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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SiteDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(SiteDemo.class);

    public void onModuleLoad() {

        SiteSection leftBorder = new SiteSection("Left", "green");

        LayoutPanel contentPanel = new LayoutPanel();
        contentPanel.setWidth("400px");

        SiteSection rightBorder = new SiteSection("Right", "green");

        SiteSection center = new SiteSection("Center", "red");
        contentPanel.add(center);
        contentPanel.setWidgetHorizontalPosition(center, Alignment.STRETCH);

        //        rootPanel.add(new SiteSection("West", "green"));
        //        rootPanel.add(new SiteSection("East", "green"));
        //
        //        rootPanel.add(new SiteSection("South", "blue"));
        //        rootPanel.add(new SiteSection("North", "blue"));

        FlowPanel rootPanel = new FlowPanel();
        rootPanel.add(leftBorder);
        DOM.setStyleAttribute(leftBorder.getElement(), "cssFloat", "left");
        rootPanel.add(contentPanel);
        DOM.setStyleAttribute(contentPanel.getElement(), "cssFloat", "left");
        rootPanel.add(rightBorder);
        DOM.setStyleAttribute(rightBorder.getElement(), "cssFloat", "left");

        RootLayoutPanel.get().add(rootPanel);

    }

    class SiteSection extends HTML {
        SiteSection(String capture, String color) {
            super(capture);
            DOM.setStyleAttribute(getElement(), "border", "1px solid " + color);
        }
    }

}
