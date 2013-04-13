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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.event.EventSource;
import com.propertyvista.field.client.event.ScreenShiftEvent;
import com.propertyvista.field.client.resources.FieldImages;

public class AlertsInfoViewImpl extends SimplePanel implements AlertsInfoView {

    private int unreadAlerts = 0;

    public AlertsInfoViewImpl() {
        final Image alertsImage = new Image(FieldImages.INSTANCE.alerts());
        alertsImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.instance();
                AppSite.getEventBus().fireEvent(new ScreenShiftEvent(EventSource.AlertsImage));
            }
        });

        add(alertsImage);
    }

    @Override
    public void setUnread(int alertsNumber) {
        this.unreadAlerts = alertsNumber;
    }

    @Override
    public boolean isVisible() {
        return unreadAlerts > 0 && super.isVisible();
    }
}
