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

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent.UserMessageType;

@Singleton
public class UserMessageViewImpl extends FlowPanel implements UserMessageView {

    private Presenter presenter;

    private final Map<UserMessageType, HTML> holders;

    public UserMessageViewImpl() {

        holders = new HashMap<UserMessageType, HTML>();

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
        HTML html = holders.get(type);
        html.setHTML(message);
        html.setVisible(true);
    }

    @Override
    public void hide(UserMessageType type) {
        HTML html = holders.get(type);
        html.setHTML("");
        html.setVisible(false);
    }

    private class Holder extends HTML {
        Holder(String borderColor) {
            setWidth("100%");
            getElement().getStyle().setBorderColor(borderColor);
            getElement().getStyle().setBorderWidth(2, Unit.PX);
            getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        }
    }
}
