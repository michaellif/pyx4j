/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class ActionButton implements IsWidget {

    private final FlowPanel contentPanel;

    public ActionButton(Button button) {
        contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        contentPanel.add(button);

        SimplePanel pointerPanel = new SimplePanel(new Image(PortalImages.INSTANCE.pointerV()));
        pointerPanel.setStyleName(PortalRootPaneTheme.StyleName.ButtonPointer.name());
        pointerPanel.setVisible(false);

        contentPanel.add(pointerPanel);

    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

}
