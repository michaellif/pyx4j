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
package com.propertyvista.field.client.ui.components.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.field.client.event.ChangeLayoutEvent;
import com.propertyvista.field.client.event.LayoutAction;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.theme.FieldTheme;

public class AlertToolbarViewImpl extends HorizontalPanel implements AlertToolbarView {

    private final Label alertLabel;

    public AlertToolbarViewImpl() {
        setSize("100%", "100%");
        setStyleName(FieldTheme.StyleName.Toolbar.name());

        final Image backImage = new Image(FieldImages.INSTANCE.back());
        backImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new ChangeLayoutEvent(LayoutAction.DiscardListerLayoutAndShiftAlerts));
                History.back();
            }
        });

        final Image contextMenuImage = new Image(FieldImages.INSTANCE.contextMenu());
        alertLabel = new Label();

        HorizontalPanel container = new HorizontalPanel();
        container.setSize("100%", "100%");

        container.add(backImage);
        container.add(alertLabel);
        container.add(contextMenuImage);

        container.setCellHorizontalAlignment(backImage, ALIGN_LEFT);
        container.setCellHorizontalAlignment(alertLabel, ALIGN_CENTER);
        container.setCellHorizontalAlignment(contextMenuImage, ALIGN_RIGHT);

        add(container);
    }

    @Override
    public void setAlert(String alert) {
        alertLabel.setText(alert);
    }

}
