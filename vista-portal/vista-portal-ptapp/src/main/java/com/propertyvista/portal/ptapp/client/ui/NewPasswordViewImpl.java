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

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class NewPasswordViewImpl extends FlowPanel implements NewPasswordView {

    private static I18n i18n = I18n.get(NewPasswordViewImpl.class);

    private Presenter presenter;

    private final NewPasswordViewForm form;

    public NewPasswordViewImpl() {

        form = new NewPasswordViewForm();
        form.initContent();
        form.populate(null);
        add(form);

        Button newPasswordButton = new Button(i18n.tr("New Password"));
        newPasswordButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        newPasswordButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.passwordReset(form.getValue());
            }

        });

        newPasswordButton.getElement().getStyle().setMarginLeft(6.4, Unit.EM);
        newPasswordButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        newPasswordButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        add(newPasswordButton);

        setWidth("50%");

        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setConversationType(ConversationType type) {
        form.setConversationType(type);
    }
}
