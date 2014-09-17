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
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.CommunicationCrmTheme;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.dto.MessageDTO;

public class CommunicationViewImpl extends FlowPanel implements CommunicationView, RequiresResize {

    private final DockLayoutPanel contentPanel;

    private final HeaderHolder headerHolder;

    private final FlowPanel mainHolder;

    private final HTML calloutHandler;

    public CommunicationViewImpl() {

        setStyleName(CommunicationCrmTheme.StyleName.Comm.name());

        calloutHandler = new HTML("<svg xmlns='http://www.w3.org/2000/svg' version='1.1'><polyline points='16,0 0,16 32,16' class='"
                + CommunicationCrmTheme.StyleName.CommCallout.name() + "'/></svg>");
        calloutHandler.getElement().getStyle().setPosition(Position.ABSOLUTE);
        calloutHandler.getElement().getStyle().setProperty("right", "38px");
        calloutHandler.getElement().getStyle().setProperty("top", "0px");

        headerHolder = new HeaderHolder();
        mainHolder = new FlowPanel();

        contentPanel = new DockLayoutPanel(Unit.PX);
        contentPanel.setStyleName(CommunicationCrmTheme.StyleName.CommContent.name());

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
        case tabletPortrait:
            calloutHandler.setVisible(false);
            addStyleDependentName(CommunicationCrmTheme.StyleDependent.sideComm.name());
            break;
        default:
            calloutHandler.setVisible(true);
            removeStyleDependentName(CommunicationCrmTheme.StyleDependent.sideComm.name());
            break;
        }
    }

    @Override
    public void populate(Vector<MessageDTO> messages) {
        mainHolder.clear();
        int directMessagesNum = 0;
        int dispatchedMessagesNum = 0;
        if (messages != null && messages.size() > 0) {
            for (final MessageDTO message : messages) {
                boolean isDirect = message.isDirect().getValue(false);//MessageGroupCategory.Custom.toString().equals(message.topic().category().getValue().toString());
                mainHolder.add(new MessagePanel(message, isDirect));
                if (isDirect) {
                    directMessagesNum++;
                } else {
                    dispatchedMessagesNum++;
                }
            }
        }
        setHeader(directMessagesNum, dispatchedMessagesNum);
    }

    private void doToggleHandler() {
        LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
        if (LayoutType.phonePortrait.equals(layout) || LayoutType.phoneLandscape.equals(layout) || LayoutType.tabletPortrait.equals(layout)) {
            AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideComm));
        }
    }

    @Override
    public void setHeader(int directMessagesNum, int dispatchedMessagesNum) {
        headerHolder.setNumberOfMessages(directMessagesNum, dispatchedMessagesNum);
    }

    class MessagePanel extends FlexTable {

        private final Image photoImage;

        private final Label subjectField;

        private final Label messageField;

        private final Label dateField;

        private final Label senderField;

        public MessagePanel(final MessageDTO message, boolean isDirect) {
            setStyleName(CommunicationCrmTheme.StyleName.CommMessage.name());

            photoImage = new Image(isDirect ? CrmImages.INSTANCE.avatar() : CrmImages.INSTANCE.alertsOn());
            subjectField = new Label(message.subject().getStringView());
            getElement().getStyle().setCursor(Cursor.POINTER);
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.viewer);
                    AppSite.getPlaceController().goTo(place.formViewerPlace(message.getPrimaryKey()));
                    doToggleHandler();
                }
            });
            messageField = new Label(message.text().getStringView());

            dateField = new Label(message.date().getStringView());
            senderField = new Label(message.senderDTO().getStringView());

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

        private final Anchor messagesAnchor;

        private final Anchor ticketsAnchor;

        private final Anchor newMessageAnchor;

        private final Anchor newTicketAnchor;

        public HeaderHolder() {

            setStyleName(CommunicationCrmTheme.StyleName.CommHeader.name());

            getElement().getStyle().setPosition(Position.RELATIVE);

            messagesAnchor = new Anchor("Messages", new Command() {

                @Override
                public void execute() {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.lister);
                    AppSite.getPlaceController().goTo(place);
                    doToggleHandler();
                }
            });

            messagesAnchor.setStyleName(CommunicationCrmTheme.StyleName.CommHeaderTitle.name());
            messagesAnchor.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            ticketsAnchor = new Anchor("Tickets", new Command() {

                @Override
                public void execute() {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message(CategoryType.Ticket);
                    place.setType(Type.lister);
                    doToggleHandler();
                    AppSite.getPlaceController().goTo(place);
                }
            });
            ticketsAnchor.setStyleName(CommunicationCrmTheme.StyleName.CommHeaderTitle.name());
            ticketsAnchor.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            newMessageAnchor = new Anchor("Create Message, ", new Command() {

                @Override
                public void execute() {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.editor);
                    doToggleHandler();
                    AppSite.getPlaceController().goTo(place);
                }
            });
            newMessageAnchor.setStyleName(CommunicationCrmTheme.StyleName.CommHeaderTitle.name());
            newMessageAnchor.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            newTicketAnchor = new Anchor("Create Ticket", new Command() {

                @Override
                public void execute() {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message(CategoryType.Ticket);
                    place.setType(Type.editor);
                    doToggleHandler();
                    AppSite.getPlaceController().goTo(place);
                }
            });
            newTicketAnchor.setStyleName(CommunicationCrmTheme.StyleName.CommHeaderTitle.name());
            newTicketAnchor.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            add(messagesAnchor);
            add(ticketsAnchor);
            add(newMessageAnchor);
            add(newTicketAnchor);

        }

        public void setNumberOfMessages(int directMessagesNum, int dispatchedMessagesNum) {
            if (directMessagesNum > 0) {
                StringBuffer statusLabel = new StringBuffer();
                statusLabel.append(directMessagesNum);
                messagesAnchor.setText("Messages (" + statusLabel.toString() + "), ");
            } else {
                messagesAnchor.setText("Messages, ");
            }

            if (dispatchedMessagesNum > 0) {
                StringBuffer statusLabel = new StringBuffer();
                statusLabel.append(dispatchedMessagesNum);
                ticketsAnchor.setVisible(true);
                ticketsAnchor.setText("Tickets in Process (" + statusLabel.toString() + "), ");
            } else {
                ticketsAnchor.setVisible(false);
            }
        }
    }

    @Override
    public void onResize() {
        contentPanel.onResize();
    }
}
