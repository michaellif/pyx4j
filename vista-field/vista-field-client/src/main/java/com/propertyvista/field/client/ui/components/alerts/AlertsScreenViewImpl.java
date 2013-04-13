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

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.Label;

import com.propertyvista.field.client.theme.FieldTheme;

public class AlertsScreenViewImpl extends SimplePanel implements AlertsScreenView {

    public AlertsScreenViewImpl() {
        setSize("100%", "100%");
    }

    @Override
    public void setAlerts(List<String> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return;
        }

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName(FieldTheme.StyleName.AlertsScreenContent.name());
        for (String alert : alerts) {
            layout.add(createLabel(alert));
        }

        add(layout);
    }

    private Label createLabel(String content) {
        Label label = new Label();
        label.setText(content);
        return label;
    }
}
