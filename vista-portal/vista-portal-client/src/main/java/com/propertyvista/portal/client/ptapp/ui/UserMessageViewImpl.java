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
package com.propertyvista.portal.client.ptapp.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;

@Singleton
public class UserMessageViewImpl extends FlowPanel implements UserMessageView {

    private Presenter presenter;

    private final Map<UserMessageType, Holder> holders;

    public UserMessageViewImpl() {

        holders = new HashMap<UserMessageType, Holder>();

        for (UserMessageType type : UserMessageType.values()) {
            Holder holder = new Holder(getColor(type));
            holders.put(type, holder);
            add(holder);
        }

    }

    private String getColor(UserMessageType type) {
        switch (type) {
        case INFO:
            return "green";
        case WARN:
            return "blue";
        case ERROR:
            return "orange";
        case FAILURE:
            return "red";
        case DEBUG:
            return "black";
        default:
            return "yellow";
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(String message, UserMessageType type) {
        Holder holder = holders.get(type);
        holder.setHTML(message);
        holder.setVisible(true);
    }

    @Override
    public void hide(UserMessageType type) {
        Holder holder = holders.get(type);
        holder.setHTML("");
        holder.setVisible(false);
    }

    private class Holder extends HorizontalPanel {

        private final HTML html;

        private final Image image;

        Holder(String borderColor) {
            setWidth("100%");
            getElement().getStyle().setMarginLeft(5, Unit.PCT);
            getElement().getStyle().setMarginRight(5, Unit.PCT);
            getElement().getStyle().setMarginTop(15, Unit.PX);
            getElement().getStyle().setMarginBottom(15, Unit.PX);

            image = new Image(SiteImages.INSTANCE.userMessageInfo());
            add(image);
            setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);

            html = new HTML();
            html.getElement().getStyle().setPaddingLeft(15, Unit.PX);
            add(html);
            setCellVerticalAlignment(html, HorizontalPanel.ALIGN_MIDDLE);
            setCellWidth(html, "100%");

        }

        public void setHTML(String string) {
            html.setHTML(string);
        }
    }
}
