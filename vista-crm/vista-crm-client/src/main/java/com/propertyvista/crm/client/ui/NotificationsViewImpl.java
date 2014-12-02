/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.IconButton;

import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.crm.client.resources.CrmImages;

public class NotificationsViewImpl extends FlowPanel implements NotificationsView {

    private static final I18n i18n = I18n.get(NotificationsViewImpl.class);

    private NotificationsPresenter presenter;

    public NotificationsViewImpl() {

    }

    @Override
    public void setPresenter(NotificationsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNotifications(List<Notification> notifications) {
        CrmRootPane rootPane = (CrmRootPane) AppSite.instance().getRootPane();
        rootPane.allocateNotificationsSpace(notifications.size());

        clear();

        if (notifications.size() == 0) {
            setVisible(false);
        } else {
            setVisible(true);
            for (final Notification notification : notifications) {

                FlowPanel message = new FlowPanel();
                message.setHeight(CrmRootPane.NOTIFICATION_HEIGHT + "px");

                message.setStyleName(SiteViewTheme.StyleName.SiteViewNotificationItem.name());

                HTML title = new HTML(notification.getTitle());
                title.setStyleName(SiteViewTheme.StyleName.SiteViewNotificationItemTitle.name());

                HTML body = new HTML(notification.getMessage());
                body.setStyleName(SiteViewTheme.StyleName.SiteViewNotificationItemBody.name());

                message.add(title);
                message.add(body);

                IconButton closeButton = new IconButton(i18n.tr("Close"), CrmImages.INSTANCE.delButton(), new Command() {

                    @Override
                    public void execute() {
                        presenter.acceptMessage(notification);
                    }
                });
                message.add(closeButton);
                closeButton.setStyleName(SiteViewTheme.StyleName.SiteViewNotificationItemCloseButton.name());

                switch (notification.getNotificationType()) {
                case FAILURE:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.error.name());
                    break;
                case ERROR:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.error.name());
                    break;
                case WARNING:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.warning.name());
                    break;
                case INFO:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.info.name());
                    break;
                case STATUS:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.confirm.name());
                    break;

                default:
                    message.addStyleDependentName(SiteViewTheme.StyleDependent.info.name());
                    break;
                }

                add(message);

            }

        }

    }
}
