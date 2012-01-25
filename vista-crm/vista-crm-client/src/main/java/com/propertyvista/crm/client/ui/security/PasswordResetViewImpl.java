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

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.common.client.ui.components.login.PasswordEditorForm;

public class PasswordResetViewImpl extends FlowPanel implements PasswordResetView {

    private Presenter presenter;

    private final PasswordEditorForm form;

    public PasswordResetViewImpl() {
        form = new PasswordEditorForm(PasswordEditorForm.Type.RESET) {
            @Override
            protected void onConfirmPasswordChange() {
                presenter.passwordReset(form.getValue());
            }
        };
        form.initContent();
        add(form);
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
