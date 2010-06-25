/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 11, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Portlet;

public class PortletPanel extends VerticalPanel {

    private static final Logger log = LoggerFactory.getLogger(PortletPanel.class);

    private final List<InlineWidget> inlineWidgets = new ArrayList<InlineWidget>();

    private final SitePanel parent;

    private final Portlet portlet;

    public PortletPanel(SitePanel parent, Portlet portlet) {
        super();
        this.parent = parent;
        this.portlet = portlet;

        String styleName = portlet.styleName().getValue();
        if (styleName == null) {
            styleName = SiteCSSClass.pyx4j_Site_HtmlPortlet.name();
        }

        setStyleName(styleName);

        if (portlet.caption().getValue() != null) {
            HTML capturePanel = new HTML(portlet.caption().getValue());
            capturePanel.setWordWrap(false);
            add(capturePanel);
            capturePanel.setStyleName(styleName + "Header");
        } else {
            HTML headerPanel = new HTML();
            add(headerPanel);
            headerPanel.setStyleName(styleName + "EmptyHeader");
        }

        DynamicHTML bodyPanel = new DynamicHTML(portlet.html().getValue(), false);
        add(bodyPanel);
        bodyPanel.setStyleName(styleName + "Body");

    }

    public void createInlineWidgets() {
        if (!portlet.inlineWidgetIds().isNull() && portlet.inlineWidgetIds().getValue().size() > 0) {
            for (String widgetId : portlet.inlineWidgetIds().getValue()) {
                InlineWidget inlineWidget = null;
                InlineWidgetRootPanel root = InlineWidgetRootPanel.get(widgetId);
                //check in local (page) factory
                if (parent.getLocalWidgetFactory() != null) {
                    inlineWidget = parent.getLocalWidgetFactory().createWidget(widgetId);
                }
                //check in global factory
                if (inlineWidget == null) {
                    inlineWidget = SitePanel.getGlobalWidgetFactory().createWidget(widgetId);
                }
                if (root != null && inlineWidget != null) {
                    root.add((Widget) inlineWidget);
                    inlineWidgets.add(inlineWidget);
                } else {
                    log.warn("Failed to add inline widget " + widgetId + " to portlet.");
                }
            }
        }
    }

    public void populateInlineWidgets(Map<String, String> args) {
        for (InlineWidget inlineWidget : inlineWidgets) {
            inlineWidget.populate(args);
        }
    }

}
