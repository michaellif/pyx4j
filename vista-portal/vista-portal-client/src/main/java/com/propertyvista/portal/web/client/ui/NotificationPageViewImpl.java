/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.images.NotificationImages;

import com.propertyvista.common.client.site.Notification;
import com.propertyvista.common.client.site.Notification.NotificationType;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class NotificationPageViewImpl extends SimplePanel implements NotificationPageView {

    public interface NotificationTypeImages {

        ImageResource error();

        ImageResource info();

        ImageResource warning();
    }

    private NotificationPagePresenter presenter;

    private Label messageLabel;

    private Label debugMessageLabel;

    private final NotificationTypeImages notificationTypeImageResources;

    public NotificationPageViewImpl(NotificationTypeImages messageTypeImageResources) {
        this.notificationTypeImageResources = messageTypeImageResources;

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

    }

    public NotificationPageViewImpl() {
        this(new NotificationTypeImages() {

            private NotificationImages di;
            {
                di = GWT.create(NotificationImages.class);
            }

            @Override
            public ImageResource error() {
                return di.error();
            }

            @Override
            public ImageResource info() {
                return di.confirm();
            }

            @Override
            public ImageResource warning() {
                return di.info();
            }
        });
    }

    @Override
    public void setPresenter(NotificationPagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(Notification userMessage) {

        NotificationGadget notificationGadget = new NotificationGadget(this, getUserMessageImageResource(userMessage.getNotificationType()), userMessage
                .getNotificationType().toString());
        notificationGadget.asWidget().setWidth("100%");
        setWidget(notificationGadget);

        messageLabel.setText(userMessage.getMessage());

        debugMessageLabel.setVisible(isDebugInfoRequired());
        debugMessageLabel.setText(isDebugInfoRequired() & userMessage.getSystemInfo() != null ? userMessage.getSystemInfo() : "");
    }

    private boolean isDebugInfoRequired() {
        return ApplicationMode.isDevelopment();
    }

    private ImageResource getUserMessageImageResource(NotificationType messageType) {
        ImageResource messageImageResource = null;
        if (messageType == null) {
            messageImageResource = notificationTypeImageResources.warning();
        } else {
            switch (messageType) {
            case ERROR:
                messageImageResource = notificationTypeImageResources.error();
                break;
            case FAILURE:
                messageImageResource = notificationTypeImageResources.error();
                break;
            case INFO:
                messageImageResource = notificationTypeImageResources.info();
                break;
            case WARNING:
                messageImageResource = notificationTypeImageResources.warning();
                break;
            default:
                messageImageResource = notificationTypeImageResources.warning();
                break;
            }
        }
        return messageImageResource;
    }

    class NotificationGadget extends AbstractGadget<NotificationPageViewImpl> {

        NotificationGadget(NotificationPageViewImpl viewer, ImageResource imageResource, String title) {
            super(viewer, imageResource, title, ThemeColor.foreground);

            FlowPanel viewPanel = new FlowPanel();
            viewPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            messageLabel = new Label();
            viewPanel.add(messageLabel);

            debugMessageLabel = new Label();
            debugMessageLabel.getElement().getStyle().setMarginTop(2, Unit.EM);
            viewPanel.add(debugMessageLabel);

            setContent(viewPanel);

            setActionsToolbar(new NotificationToolbar());

        }

        class NotificationToolbar extends Toolbar {
            public NotificationToolbar() {

                Button okButton = new Button("OK", new Command() {

                    @Override
                    public void execute() {
                        presenter.acceptMessage();
                    }
                });
                okButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 1));
                add(okButton);

            }

        }

    }
}
