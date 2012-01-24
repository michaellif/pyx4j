/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.security;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.ui.components.login.PasswordEditorForm;

public class PasswordChangeViewImpl extends FlowPanel implements PasswordChangeView {

    private static final I18n i18n = I18n.get(PasswordChangeViewImpl.class);

    private Presenter presenter;

    private final PasswordEditorForm form;

    public PasswordChangeViewImpl() {
        form = new PasswordEditorForm(PasswordEditorForm.Type.CHANGE) {
            @Override
            protected void onConfirmPasswordChange() {
                if (form.isValid()) {
                    presenter.passwordChange(form.getValue());
                }
            }
        };
        form.initContent();
        form.populateNew();
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(PasswordChangeRequest request) {
        form.populate(request);
    }

    @Override
    public PasswordChangeRequest getValue() {
        return form.getValue();
    }

    @Override
    public void discard() {
        form.populateNew();
    }

}
