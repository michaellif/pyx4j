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
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class CommunicationViewImpl extends FlowPanel implements CommunicationView {

    public CommunicationViewImpl() {

        getElement().getStyle().setProperty("marginTop", "15px");

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName(PortalWebRootPaneTheme.StyleName.Communication.name());

        for (int i = 0; i < 6; i++) {
            Label label = new Label("This is Communication Message #" + i);
            contentPanel.add(label);
        }

        HTML calloutHandler = new HTML("<svg xmlns='http://www.w3.org/2000/svg' version='1.1'><polyline points='16,0 0,16 32,16' class='"
                + PortalWebRootPaneTheme.StyleName.CommunicationCallout.name() + "'/></svg>");
        calloutHandler.getElement().getStyle().setPosition(Position.ABSOLUTE);
        calloutHandler.getElement().getStyle().setProperty("right", "38px");
        calloutHandler.getElement().getStyle().setProperty("top", "0px");
        add(calloutHandler);
        add(contentPanel);

    }

    @Override
    public void setPresenter(CommunicationPresenter presenter) {
        // TODO Auto-generated method stub

    }

}
