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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.rpc.FieldSiteMap;

public class AlertsScreenViewImpl extends SimplePanel implements AlertsScreenView {

    private final VerticalPanel layout;

    public AlertsScreenViewImpl() {
        setSize("100%", "100%");

        layout = new VerticalPanel();
        layout.setStyleName(FieldTheme.StyleName.AlertsScreenContent.name());
        add(layout);
    }

    @Override
    public void setAlerts(List<String> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return;
        }

        layout.setStyleName(FieldTheme.StyleName.AlertsScreenContent.name());
        for (String alert : alerts) {
            layout.add(createLabel(alert));
        }
    }

    private Label createLabel(String content) {
        final Label label = new Label();
        label.setText(content);
        label.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppPlace nextPlace = new FieldSiteMap.AlertViewer();
                nextPlace.placeArg("alert", label.getText());
                AppSite.getPlaceController().goTo(nextPlace);

                removeLabel(label);
            }
        });
        return label;
    }

    private void removeLabel(Label label) {
        layout.remove(label);
    }
}
