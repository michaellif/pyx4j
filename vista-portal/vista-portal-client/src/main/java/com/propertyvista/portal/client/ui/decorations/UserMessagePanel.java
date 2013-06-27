/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.common.client.events.UserMessageHandler;
import com.propertyvista.common.client.site.Notification.NotificationType;
import com.propertyvista.portal.client.resources.PortalImages;

public class UserMessagePanel extends HorizontalPanel implements UserMessageHandler {

    private final HTML message;

    private final Image image;

    public UserMessagePanel() {
        setWidth("100%");
        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);

        HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
        add(side);

        add(new HTML("&nbsp;&nbsp;&nbsp;"));
        image = new Image();
        add(image);

        message = new HTML();
        message.getElement().getStyle().setPaddingLeft(15, Unit.PX);

        add(message);
        setCellVerticalAlignment(message, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(message, "100%");

        setVisible(false);

        AppSite.getEventBus().addHandler(UserMessageEvent.getType(), this);
    }

    public void setMessage(String msg, NotificationType type) {
        String color;
        switch (type) {
        case INFO:
            color = "#BBB";
            image.setResource(PortalImages.INSTANCE.userMessageInfo());
            break;
        case WARN:
            color = "#F3931F";
            image.setResource(PortalImages.INSTANCE.warning());
            break;
        case ERROR:
        case FAILURE:
            color = "#E12900";
            image.setResource(PortalImages.INSTANCE.warning());
            break;
        default:
            color = "inherit";
            image.setResource(PortalImages.INSTANCE.userMessageInfo());
        }
        message.setHTML(msg);
        message.getElement().getStyle().setColor(color);

        setVisible(true);
    }

    public void clearMessage() {
        message.setHTML("");
        setVisible(false);
    }

    @Override
    public void onUserMessage(UserMessageEvent event) {
        if (event.getMessage() != null) {
            setMessage(event.getMessage(), event.getMessageType());
        }
    }
}
