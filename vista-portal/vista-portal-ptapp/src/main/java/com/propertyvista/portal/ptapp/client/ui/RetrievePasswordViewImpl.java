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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.portal.rpc.ptapp.PasswordRetrievalRequest;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;

public class RetrievePasswordViewImpl extends FlowPanel implements RetrievePasswordView {

    private static I18n i18n = I18nFactory.getI18n(RetrievePasswordViewImpl.class);

    private Presenter presenter;

    private final CEntityEditor<PasswordRetrievalRequest> form;

    public RetrievePasswordViewImpl() {

        form = new RetrievePasswordViewForm();
        form.initContent();
        form.populate(null);
        add(form);

        Button retrievePasswordButton = new Button(i18n.tr("Retrieve Password"));
        retrievePasswordButton.ensureDebugId(VistaFormsDebugId.Auth_RetrivePassword.toString());
        retrievePasswordButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.retrievePassword(form.getValue());
            }

        });

        retrievePasswordButton.getElement().getStyle().setMarginLeft(90, Unit.PX);
        retrievePasswordButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        retrievePasswordButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        add(retrievePasswordButton);

        setWidth("30%");

        getElement().getStyle().setMarginTop(1, Unit.EM);
        getElement().getStyle().setMarginBottom(1, Unit.EM);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
