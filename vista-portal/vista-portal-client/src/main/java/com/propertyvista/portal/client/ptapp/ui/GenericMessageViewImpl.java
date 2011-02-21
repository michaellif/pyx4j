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
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager.Message;

import com.pyx4j.essentials.client.crud.CrudDebugId;

public class GenericMessageViewImpl extends FlowPanel implements GenericMessageView {

    private static I18n i18n = I18nFactory.getI18n(RetrievePasswordViewImpl.class);

    private Presenter presenter;

    private final HTML titleHtml;

    private final HTML messageHtml;

    private final Button actionButton;

    public GenericMessageViewImpl() {

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

        getElement().getStyle().setMarginLeft(5, Unit.PCT);
        getElement().getStyle().setMarginRight(5, Unit.PCT);
        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setMessage(Message message) {
        if (message == null) {
            titleHtml.setText("");
            messageHtml.setText("");
            actionButton.setVisible(false);
        } else {
            titleHtml.setText(message.getTitle());
            messageHtml.setText(message.getMessage());
            actionButton.setText(message.getButtonText());
            actionButton.setVisible(true);
        }

    }
}