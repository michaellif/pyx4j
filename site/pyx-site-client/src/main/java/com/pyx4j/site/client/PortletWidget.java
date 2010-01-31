/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 11, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.domain.Portlet;

public class PortletWidget extends VerticalPanel {

    public PortletWidget(Portlet portlet) {
        super();
        setStyleName(SiteCSSClass.pyx4j_Site_HtmlPortlet.name());

        getElement().getStyle().setProperty("margin", "0px");

        HTML capturePanel = new HTML(portlet.capture().getValue());
        capturePanel.setWordWrap(false);
        add(capturePanel);
        capturePanel.setStyleName(SiteCSSClass.pyx4j_Site_HtmlPortletHeader.name());

        HTML bodyPanel = new HTML(portlet.html().getValue());
        bodyPanel.setWordWrap(false);
        add(bodyPanel);
        bodyPanel.setStyleName(SiteCSSClass.pyx4j_Site_HtmlPortletBody.name());

    }

}
