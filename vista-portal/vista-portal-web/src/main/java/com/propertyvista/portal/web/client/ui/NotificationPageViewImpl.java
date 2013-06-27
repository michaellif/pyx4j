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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.images.NotificationImages;

import com.propertyvista.common.client.site.Notification;
import com.propertyvista.common.client.site.Notification.NotificationType;

public class NotificationPageViewImpl extends Composite implements NotificationPageView {

    public interface NotificationTypeImages {

        ImageResource error();

        ImageResource info();

        ImageResource warning();
    }

    private static final I18n i18n = I18n.get(NotificationPageView.class);

    private final SimplePanel notificationTypeImageHolder;

    private final Label messageLabel;

    private final Label debugMessageLabel;

    private Presenter presenter;

    private final NotificationTypeImages notificationTypeImageResources;

    public NotificationPageViewImpl(NotificationTypeImages messageTypeImageResources) {
        this.notificationTypeImageResources = messageTypeImageResources;
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        notificationTypeImageHolder = new SimplePanel();
        viewPanel.add(notificationTypeImageHolder);

        messageLabel = new Label();
        viewPanel.add(messageLabel);

        debugMessageLabel = new Label();
        debugMessageLabel.getElement().getStyle().setMarginTop(2, Unit.EM);
        viewPanel.add(debugMessageLabel);

        SimplePanel buttonHolder = new SimplePanel();
        buttonHolder.setWidth("100%");
        buttonHolder.getElement().getStyle().setMarginTop(2, Unit.EM);

        Button acceptMessageButton = new Button(i18n.tr("Ok"), new Command() {
            @Override
            public void execute() {
                presenter.acceptMessage();
            }
        });
        acceptMessageButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.NONE); // this is a must because there are people who write strage CSS

        buttonHolder.setWidget(acceptMessageButton);

        viewPanel.add(buttonHolder);

        initWidget(viewPanel);
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
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(Notification userMessage) {
        notificationTypeImageHolder.setWidget(new Image(getUserMessageImageResource(userMessage.getNotificationType())));
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
            case WARN:
                messageImageResource = notificationTypeImageResources.warning();
                break;
            default:
                messageImageResource = notificationTypeImageResources.warning();
                break;
            }
        }
        return messageImageResource;
    }
}
