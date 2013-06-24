/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class MessageViewImpl extends FlowPanel implements MessageView {

    public MessageViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Messages.name());

        WidgetsImageBundle images = ImageFactory.getImages();

        HTML error = new HTML("<b>Error Notification</b><br>Error Message goes here");
        error.getElement().getStyle().setProperty("textAlign", "center");
        error.getElement().getStyle().setProperty("background", "url('" + images.error().getSafeUri().asString() + "') no-repeat scroll 10px center");
        error.getElement().getStyle().setPaddingTop(10, Unit.PX);
        error.setHeight("40px");
        error.getElement().getStyle().setProperty("border", "1px solid #E09293");
        error.getElement().getStyle().setProperty("borderRadius", "5px");
        error.getElement().getStyle().setProperty("margin", "2px");
        error.getElement().getStyle().setProperty("backgroundColor", "#FFD2D3");

        HTML info = new HTML("<b>Notification</b><br>Message goes here");
        info.getElement().getStyle().setProperty("textAlign", "center");
        info.getElement().getStyle().setProperty("background", "url('" + images.info().getSafeUri().asString() + "') no-repeat scroll 10px center");
        info.getElement().getStyle().setPaddingTop(10, Unit.PX);
        info.setHeight("40px");
        info.getElement().getStyle().setProperty("border", "1px solid #9ADF8F");
        info.getElement().getStyle().setProperty("borderRadius", "5px");
        info.getElement().getStyle().setProperty("margin", "2px");
        info.getElement().getStyle().setProperty("backgroundColor", "#D4FFCD");

        FlowPanel contentPanel = new FlowPanel();

        contentPanel.add(error);
        contentPanel.add(info);

        add(contentPanel);

    }

}
