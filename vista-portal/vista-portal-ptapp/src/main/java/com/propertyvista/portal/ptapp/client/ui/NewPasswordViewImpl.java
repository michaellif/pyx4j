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

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.login.PasswordForm;

public class NewPasswordViewImpl extends FlowPanel implements NewPasswordView {

    private static final I18n i18n = I18n.get(NewPasswordViewImpl.class);

    private Presenter presenter;

    private final PasswordForm form;

    public NewPasswordViewImpl() {

        form = new PasswordForm(PasswordForm.Type.RESET);
        // FIXME add submit button
        form.initContent();
        add(form);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.populateNew();
    }

}
