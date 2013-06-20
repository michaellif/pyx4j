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
package com.propertyvista.field.client.ui.components.alerts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.propertyvista.field.client.resources.FieldImages;

public class AlertsInfoViewImpl extends FlowPanel implements AlertsInfoView {

    private int unreadAlerts = 0;

    private final Image alertsImage;

    private final Image closeImage;

    public AlertsInfoViewImpl() {
        alertsImage = createImage(FieldImages.INSTANCE.alerts());
        closeImage = createImage(FieldImages.INSTANCE.close());
        closeImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                setNoUnreadStyle();
            }
        });
        setUnreadStyle();

        add(alertsImage);
        add(closeImage);
    }

    @Override
    public void setUnread(int alertsNumber) {
        assert (alertsNumber >= 0);
        this.unreadAlerts = alertsNumber;
        setUnreadStyle();
    }

    @Override
    public void decreaseUnread() {
        if (unreadAlerts > 0) {
            unreadAlerts--;
            if (unreadAlerts == 0) {
                setCloseStyle();
            }
        }
    }

    private Image createImage(ImageResource resource) {
        Image image = new Image(resource);
        image.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        });

        return image;
    }

    private void setUnreadStyle() {
        alertsImage.setVisible(true);
        closeImage.setVisible(false);
    }

    private void setCloseStyle() {
        alertsImage.setVisible(false);
        closeImage.setVisible(true);
    }

    private void setNoUnreadStyle() {
        alertsImage.setVisible(false);
        closeImage.setVisible(false);
    }
}
