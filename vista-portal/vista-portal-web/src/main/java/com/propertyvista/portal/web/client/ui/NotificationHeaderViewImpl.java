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

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.common.client.site.Notification;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class NotificationHeaderViewImpl extends FlowPanel implements NotificationHeaderView {

    private NotificationHeaderPresenter presenter;

    private final FlowPanel contentPanel;

    public NotificationHeaderViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.NotificationContainer.name());

        contentPanel = new FlowPanel();

        add(contentPanel);

    }

    @Override
    public void setPresenter(NotificationHeaderPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<Notification> notifications) {
        contentPanel.clear();

        for (Notification notification : notifications) {

            HTML message = new HTML("<b>" + notification.getTitle() + "</b><br/>" + notification.getMessage());
            message.setStyleName(PortalWebRootPaneTheme.StyleName.NotificationItem.name());

            switch (notification.getNotificationType()) {
            case FAILURE:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.error.name());
                break;
            case ERROR:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.error.name());
                break;
            case WARN:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.warning.name());
                break;
            case INFO:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.info.name());
                break;
            case CONFIRM:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.confirm.name());
                break;

            default:
                message.addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.info.name());
                break;
            }

            contentPanel.add(message);

        }

    }
}
