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
package com.propertyvista.portal.client.ui.residents;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoViewImpl extends FlowPanel implements PersonalInfoView {

    private final PersonalInfoForm form;

    private PersonalInfoView.Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(PersonalInfoViewImpl.class);

    public PersonalInfoViewImpl() {
        form = new PersonalInfoForm();
        form.initContent();
        add(form);

        Button saveButton = new Button(i18n.tr("Save"));
        saveButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                Window.scrollTo(0, 0);
            }
        });

        getElement().getStyle().setMarginTop(15, Unit.PX);
        HTML separator = new HTML();
        separator.getElement().getStyle().setProperty("borderTop", "1px dotted black");
        separator.getElement().getStyle().setProperty("margin", "1em 0em 0em 0em");
        add(separator);

        saveButton.getElement().getStyle().setProperty("margin", "1em 1em 1em 0em");
        add(saveButton);

        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(ResidentDTO personalInfo) {
        form.populate(personalInfo);

    }

}
