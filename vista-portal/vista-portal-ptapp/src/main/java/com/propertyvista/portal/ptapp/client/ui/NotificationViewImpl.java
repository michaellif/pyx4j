/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.site.Notification;

public class NotificationViewImpl extends FlowPanel implements NotificationView {

    private static final I18n i18n = I18n.get(NotificationViewImpl.class);

    private Presenter presenter;

    private final HTML titleHtml;

    private final HTML messageHtml;

    private final Button actionButton;

    public NotificationViewImpl() {

        titleHtml = new HTML();
        add(titleHtml);

        messageHtml = new HTML();
        add(messageHtml);

        actionButton = new Button();
        actionButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        actionButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.action();
            }

        });
        actionButton.getElement().getStyle().setMarginLeft(6.4, Unit.EM);
        actionButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        actionButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        add(actionButton);

        setWidth("100%");

        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNotification(Notification notification) {
        if (notification == null) {
            titleHtml.setText("");
            messageHtml.setText("");
            actionButton.setVisible(false);
        } else {
            titleHtml.setText(notification.getTitle());
            messageHtml.setText(notification.getMessage());
            actionButton.setVisible(false);

        }

    }
}