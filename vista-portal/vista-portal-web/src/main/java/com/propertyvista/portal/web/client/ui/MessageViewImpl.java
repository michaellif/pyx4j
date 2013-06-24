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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class MessageViewImpl extends FlowPanel implements MessageView {

    public MessageViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Messages.name());

        HTML warning = new HTML("<b>Error Notification</b><br>Error Message goes here");
        warning.getElement().getStyle().setProperty("textAlign", "center");

        warning.getElement().getStyle()
                .setProperty("background", "url('" + PortalImages.INSTANCE.warning().getSafeUri().asString() + "') no-repeat scroll 30px center");
        warning.setHeight("40px");

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setProperty("backgroundColor", "#FFD2D3");

        contentPanel.add(warning);

        add(contentPanel);

    }

}
