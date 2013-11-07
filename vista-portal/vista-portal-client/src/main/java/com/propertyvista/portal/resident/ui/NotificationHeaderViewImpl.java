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
package com.propertyvista.portal.resident.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.IconButton;

import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class NotificationHeaderViewImpl extends FlowPanel implements NotificationHeaderView {

    private static final I18n i18n = I18n.get(NotificationHeaderViewImpl.class);

    private NotificationHeaderPresenter presenter;

    private final FlowPanel contentPanel;

    public NotificationHeaderViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.NotificationContainer.name());

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
        if (notifications.size() == 0) {
            setVisible(false);
        } else {
            setVisible(true);
            for (final Notification notification : notifications) {

                FlowPanel message = new FlowPanel();
                message.setStyleName(PortalRootPaneTheme.StyleName.NotificationItem.name());

                HTML title = new HTML(notification.getTitle());
                title.setStyleName(PortalRootPaneTheme.StyleName.NotificationItemTitle.name());

                HTML body = new HTML(notification.getMessage());

                message.add(title);
                message.add(body);

                IconButton closeButton = new IconButton(i18n.tr("Close"), PortalImages.INSTANCE.delButton(), new Command() {

                    @Override
                    public void execute() {
                        presenter.acceptMessage(notification);
                    }
                });
                message.add(closeButton);
                closeButton.setStyleName(PortalRootPaneTheme.StyleName.NotificationItemCloseButton.name());

                switch (notification.getNotificationType()) {
                case FAILURE:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.error.name());
                    break;
                case ERROR:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.error.name());
                    break;
                case WARNING:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.warning.name());
                    break;
                case INFO:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.info.name());
                    break;
                case STATUS:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.confirm.name());
                    break;

                default:
                    message.addStyleDependentName(PortalRootPaneTheme.StyleDependent.info.name());
                    break;
                }

                contentPanel.add(message);

            }

        }

    }
}
