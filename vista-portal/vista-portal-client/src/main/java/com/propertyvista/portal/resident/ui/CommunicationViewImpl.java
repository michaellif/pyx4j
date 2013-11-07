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
package com.propertyvista.portal.resident.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class CommunicationViewImpl extends FlowPanel implements CommunicationView, RequiresResize {

    private CommunicationPresenter presenter;

    private final DockLayoutPanel contentPanel;

    private final HeaderHolder headerHolder;

    private final FlowPanel mainHolder;

    private final HTML calloutHandler;

    public CommunicationViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.Comm.name());

        calloutHandler = new HTML("<svg xmlns='http://www.w3.org/2000/svg' version='1.1'><polyline points='16,0 0,16 32,16' class='"
                + PortalRootPaneTheme.StyleName.CommCallout.name() + "'/></svg>");
        calloutHandler.getElement().getStyle().setPosition(Position.ABSOLUTE);
        calloutHandler.getElement().getStyle().setProperty("right", "38px");
        calloutHandler.getElement().getStyle().setProperty("top", "0px");

        headerHolder = new HeaderHolder();
        mainHolder = new FlowPanel();

        contentPanel = new DockLayoutPanel(Unit.PX);
        contentPanel.setStyleName(PortalRootPaneTheme.StyleName.CommContent.name());

        add(calloutHandler);
        add(contentPanel);

        contentPanel.addNorth(headerHolder, 60);

        contentPanel.add(new ScrollPanel(mainHolder));

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
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.sideComm.name());
            break;
        case tabletPortrait:
        case tabletLandscape:
        case monitor:
        case huge:
            calloutHandler.setVisible(true);
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideComm.name());
            break;
        }
    }

    @Override
    public void setPresenter(CommunicationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<Message> messages) {
        mainHolder.clear();
        for (final Message message : messages) {
            mainHolder.add(new MessagePanel(message));
        }
    }

    class MessagePanel extends FlexTable {

        private final Image photoImage;

        private final Label subjectField;

        private final Label messageField;

        private final Label dateField;

        private final Label senderField;

        public MessagePanel(Message message) {
            setStyleName(PortalRootPaneTheme.StyleName.CommMessage.name());

            photoImage = new Image(PortalImages.INSTANCE.avatar2());
            subjectField = new Label(message.subject().getStringView());
            messageField = new Label(message.text().getStringView());
            dateField = new Label(message.date().getStringView());
            senderField = new Label("John Smith");

            setWidget(0, 0, photoImage);
            getFlexCellFormatter().setRowSpan(0, 0, 2);
            getFlexCellFormatter().setWidth(0, 0, "1px");

            setWidget(0, 1, senderField);

            setWidget(0, 2, dateField);
            getFlexCellFormatter().setWidth(0, 2, "1px");

            setWidget(1, 0, subjectField);
            getFlexCellFormatter().setColSpan(1, 0, 2);
            setWidget(2, 0, messageField);
            getFlexCellFormatter().setColSpan(2, 0, 3);

        }
    }

    class HeaderHolder extends FlowPanel {

        private final Label messagesLabel;

        private final Image writeActionImage;

        public HeaderHolder() {

            setStyleName(PortalRootPaneTheme.StyleName.CommHeader.name());

            getElement().getStyle().setPosition(Position.RELATIVE);

            messagesLabel = new Label("Messages(5)");
            messagesLabel.setStyleName(PortalRootPaneTheme.StyleName.CommHeaderTitle.name());
            messagesLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            writeActionImage = new Image(PortalImages.INSTANCE.writeMessage());
            writeActionImage.setStyleName(PortalRootPaneTheme.StyleName.CommHeaderWriteAction.name());
            writeActionImage.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            add(messagesLabel);
            add(writeActionImage);

        }

        public void setNumberOfMessages(int number) {
            messagesLabel.setText(String.valueOf(number));
        }
    }

    @Override
    public void onResize() {
        contentPanel.onResize();
    }
}
