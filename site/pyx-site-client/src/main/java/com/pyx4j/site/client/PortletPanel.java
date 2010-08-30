/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 11, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.meta.NavigUtils;
import com.pyx4j.widgets.client.Button;

public class PortletPanel extends ContentPanel {

    private static final Logger log = LoggerFactory.getLogger(PortletPanel.class);

    private final VerticalPanel container;

    private final Portlet portlet;

    public PortletPanel(SitePanel parent, final Portlet portlet, ClientBundleWithLookup bundle) {
        super(parent);
        this.portlet = portlet;

        container = new VerticalPanel();

        String styleName = portlet.styleName().getValue();
        if (styleName == null) {
            styleName = SiteCSSClass.pyx4j_Site_HtmlPortlet.name();
        }

        container.setStyleName(styleName);

        if (portlet.caption().getValue() != null) {
            HTML captionPanel = new HTML(portlet.caption().getValue());
            captionPanel.setWordWrap(false);
            container.add(captionPanel);
            captionPanel.setStyleName(styleName + "Header");
        } else {
            HTML headerPanel = new HTML();
            container.add(headerPanel);
            headerPanel.setStyleName(styleName + "EmptyHeader");
        }

        DynamicHTML bodyPanel = new DynamicHTML(portlet.html().getValue(), bundle, true);
        container.add(bodyPanel);
        bodyPanel.setStyleName(styleName + "Body");

        if (!portlet.navigNode().isNull()) {
            Button button = new Button(portlet.actionLabel().getStringView());
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AbstractSiteDispatcher.show(NavigUtils.getPageUri(portlet.navigNode().getValue()));
                }
            });

            button.getElement().getStyle().setMarginTop(10, Unit.PX);

            container.add(button);
            container.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);
        }
        setWidget(container);
    }

    public void createInlineWidgets() {
        if (!portlet.inlineWidgetIds().isNull() && portlet.inlineWidgetIds().getValue().size() > 0) {
            for (String widgetId : portlet.inlineWidgetIds().getValue()) {
                InlineWidget inlineWidget = null;
                InlineWidgetRootPanel root = InlineWidgetRootPanel.get(widgetId);
                //check in local (page) factory
                if (getSitePanel().getLocalWidgetFactory() != null) {
                    inlineWidget = getSitePanel().getLocalWidgetFactory().createWidget(widgetId);
                }
                //check in global factory
                if (inlineWidget == null) {
                    inlineWidget = SitePanel.getGlobalWidgetFactory().createWidget(widgetId);
                }
                if (root != null && inlineWidget != null) {
                    root.add((Widget) inlineWidget);
                    addInlineWidget(inlineWidget);
                } else {
                    log.warn("Failed to add inline widget " + widgetId + " to portlet.");
                }
            }
        }
    }

}
