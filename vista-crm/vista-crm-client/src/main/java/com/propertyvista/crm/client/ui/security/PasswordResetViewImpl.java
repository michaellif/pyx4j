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
package com.propertyvista.crm.client.ui.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.login.PasswordEditorForm;

public class PasswordResetViewImpl extends VerticalPanel implements PasswordResetView {

    private final static I18n i18n = I18n.get(PasswordResetViewImpl.class);

    private Presenter presenter;

    private final PasswordEditorForm form;

    public PasswordResetViewImpl() {
        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        form = new PasswordEditorForm(PasswordEditorForm.Type.RESET);
        form.initContent();
        add(form);

        final Button submitButton = new Button(i18n.tr("Submit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (form.isValid()) {
                    presenter.resetPassword(form.getValue());
                } else {
                    // here we hope that because the focus left the form and moved to submitButton,
                    // we get the relevant validation error on the form. 
                }
            }
        });
        submitButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString()); // TODO why we need this???
        add(submitButton);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.populateNew();
    }

    @Override
    public void discard() {
        form.discard();
    }
}
