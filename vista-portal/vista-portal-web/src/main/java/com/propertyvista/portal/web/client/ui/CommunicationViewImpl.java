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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class CommunicationViewImpl extends FlowPanel implements CommunicationView {

    private final HTML calloutHandler;

    public CommunicationViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Communication.name());

        FlowPanel contentPanel = new FlowPanel();

        for (int i = 0; i < 6; i++) {
            Label label = new Label("This is Communication Message #" + i);
            contentPanel.add(label);
        }

        calloutHandler = new HTML("<svg xmlns='http://www.w3.org/2000/svg' version='1.1'><polyline points='16,0 0,16 32,16' class='"
                + PortalWebRootPaneTheme.StyleName.CommunicationCallout.name() + "'/></svg>");
        calloutHandler.getElement().getStyle().setPosition(Position.ABSOLUTE);
        calloutHandler.getElement().getStyle().setProperty("right", "38px");
        calloutHandler.getElement().getStyle().setProperty("top", "0px");
        add(calloutHandler);
        add(contentPanel);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            calloutHandler.setVisible(false);
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideComm.name());
            break;
        case tabletPortrait:
        case tabletLandscape:
        case monitor:
            calloutHandler.setVisible(true);
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideComm.name());
            break;
        }
    }

    @Override
    public void setPresenter(CommunicationPresenter presenter) {
        // TODO Auto-generated method stub

    }

}
