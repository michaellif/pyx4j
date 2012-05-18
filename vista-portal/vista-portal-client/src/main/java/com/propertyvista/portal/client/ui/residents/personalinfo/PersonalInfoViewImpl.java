/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.personalinfo;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoViewImpl extends FlowPanel implements PersonalInfoView {

    private static final I18n i18n = I18n.get(PersonalInfoViewImpl.class);

    private final UserMessagePanel errorPanel = new UserMessagePanel();

    private final PersonalInfoForm form;

    private Presenter presenter;

    public PersonalInfoViewImpl() {
        add(errorPanel);

        form = new PersonalInfoForm();
        add(form);

        Button saveButton = new Button(i18n.tr("Save"));
        saveButton.getElement().getStyle().setMargin(20, Unit.PX);
        saveButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    form.setVisited(true);
                    String msg = i18n.tr("The form was completed with errors outlined below. Please review and try again.");
                    showError(msg);
                } else {
                    presenter.save(form.getValue());
                }
                Window.scrollTo(0, 0);
            }
        });

        add(saveButton);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(ResidentDTO personalInfo) {
        errorPanel.clearMessage();
        form.populate(personalInfo);
    }

    @Override
    public void showError(String msg) {
        errorPanel.setMessage(msg, UserMessageType.ERROR);
    }

    @Override
    public void showNote(String msg) {
        errorPanel.setMessage(msg, UserMessageType.INFO);
    }
}
