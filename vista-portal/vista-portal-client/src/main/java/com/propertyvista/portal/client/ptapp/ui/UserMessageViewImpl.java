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

import java.util.List;

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

    private final HTML notesHolder;

    private final HTML errorsHolder;

    private final HTML failuresHolder;

    public UserMessageViewImpl() {
        notesHolder = new HTML();
        notesHolder.setWidth("100%");
        notesHolder.getElement().getStyle().setBorderColor("blue");
        notesHolder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        notesHolder.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        add(notesHolder);

        errorsHolder = new HTML();
        errorsHolder.setWidth("100%");
        errorsHolder.getElement().getStyle().setBorderColor("orange");
        errorsHolder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        errorsHolder.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        add(errorsHolder);

        failuresHolder = new HTML();
        failuresHolder.setWidth("100%");
        failuresHolder.getElement().getStyle().setBorderColor("red");
        failuresHolder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        failuresHolder.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        add(failuresHolder);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNotes(List<String> messages) {
        notesHolder.setHTML(buildMessage(messages));
    }

    @Override
    public void showErrors(List<String> messages) {
        errorsHolder.setHTML(buildMessage(messages));
    }

    @Override
    public void showFailures(List<String> messages) {
        failuresHolder.setHTML(buildMessage(messages));
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
}
