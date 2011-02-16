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
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class UserMessageViewImpl extends FlowPanel implements UserMessageView {

    private Presenter presenter;

    private final Map<Type, HTML> holders;

    public UserMessageViewImpl() {

        holders = new HashMap<Type, HTML>();

        for (Type type : Type.values()) {
            Holder holder = new Holder(type.getColor());
            holders.put(type, holder);
            add(holder);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(List<String> messages, Type type) {
        HTML html = holders.get(type);
        html.setHTML(buildMessage(messages));
        html.setVisible(true);
    }

    @Override
    public void hide(Type type) {
        HTML html = holders.get(type);
        html.setHTML("");
        html.setVisible(false);
    }

    private String buildMessage(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        builder.append("<ul>");
        for (String message : messages) {
            builder.append("<li>").append(message).append("</li>");
        }
        builder.append("</ul>");
        return builder.toString();
    }

    class Holder extends HTML {
        Holder(String borderColor) {
            setWidth("100%");
            getElement().getStyle().setBorderColor("green");
            getElement().getStyle().setBorderWidth(1, Unit.PX);
            getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);

        }
    }
}
