/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Singleton;

import com.pyx4j.commons.CompositeDebugId;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;

@Singleton
public class UserMessageViewImpl extends FlowPanel implements UserMessageView {

    private final Map<UserMessageType, Holder> holders;

    public UserMessageViewImpl() {

        holders = new HashMap<UserMessageType, Holder>();

        for (UserMessageType type : UserMessageType.values()) {

            Holder holder = new Holder(type);
            holders.put(type, holder);
            add(holder);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
    }

    @Override
    public void show(String userMessages, String debugMessages, UserMessageType type) {
        Holder holder = holders.get(type);
        StringBuilder message = new StringBuilder();
        message.append(userMessages);
        if (debugMessages != null) {
            message.append("<br/>").append(debugMessages);
        }
        holder.setHTML(message.toString());
        holder.setVisible(true);
    }

    @Override
    public void hide(UserMessageType type) {
        Holder holder = holders.get(type);
        holder.setHTML("");
        holder.setVisible(false);
    }

    @Override
    public void hideAll() {
        for (Holder holder : holders.values()) {
            holder.setHTML("");
            holder.setVisible(false);
        }
    }

    private class Holder extends HorizontalPanel {

        private final HTML html;

        private final Image image;

        private final String colour;

        Holder(UserMessageType type) {
            setWidth("100%");
            getElement().getStyle().setMarginTop(15, Unit.PX);
            getElement().getStyle().setMarginBottom(15, Unit.PX);

            switch (type) {
            case INFO:
                colour = "#BBB";
                image = new Image(PortalImages.INSTANCE.info());
                break;

            case WARN:
                colour = "#F3931F";
                image = new Image(PortalImages.INSTANCE.infoOrange());
                break;

            case ERROR:
            case FAILURE:
                colour = "#E12900";
                image = new Image(PortalImages.INSTANCE.infoRed());
                break;

            default:
                colour = "inherit";
                image = new Image(PortalImages.INSTANCE.userMessageInfo());
            }

            HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
            add(side);

            Element td = DOM.getParent(side.getElement());
            if (td != null) {
                td.getStyle().setBackgroundColor(colour);
            }

            add(new HTML("&nbsp;&nbsp;&nbsp;"));
            add(image);

            html = new HTML();
            html.getElement().getStyle().setColor(colour);
            html.getElement().getStyle().setPaddingLeft(15, Unit.PX);
            html.ensureDebugId(CompositeDebugId.debugId(VistaFormsDebugId.UserMessage_Prefix, type));

            add(html);
            setCellVerticalAlignment(html, HorizontalPanel.ALIGN_MIDDLE);
            setCellWidth(html, "100%");
        }

        public void setHTML(String string) {
            html.setHTML(string);
        }
    }

}
