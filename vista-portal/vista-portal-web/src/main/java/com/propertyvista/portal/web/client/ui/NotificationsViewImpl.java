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
import com.google.gwt.user.client.ui.Label;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class NotificationsViewImpl extends FlowPanel implements NotificationsView {

    public NotificationsViewImpl() {

        getElement().getStyle().setProperty("margin", "10px");

        setStyleName(PortalWebRootPaneTheme.StyleName.Notifications.name());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setSize("400px", "600px");
        contentPanel.getElement().getStyle().setProperty("border", "solid 1px #aaa");
        contentPanel.getElement().getStyle().setProperty("backgroundColor", "white");

        for (int i = 0; i < 6; i++) {
            Label label = new Label("This is message #" + i);
            label.getElement().getStyle().setProperty("margin", "30px");
            contentPanel.add(label);
        }

        add(contentPanel);

    }

}
