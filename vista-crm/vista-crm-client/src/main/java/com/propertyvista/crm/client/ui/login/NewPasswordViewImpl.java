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
package com.propertyvista.crm.client.ui.login;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.common.client.ui.components.login.NewPasswordForm;
import com.propertyvista.common.client.ui.components.login.NewPasswordForm.ConversationType;

public class NewPasswordViewImpl extends FlowPanel implements NewPasswordView {

    private static I18n i18n = I18n.get(NewPasswordViewImpl.class);

    private Presenter presenter;

    private final NewPasswordForm form;

    public NewPasswordViewImpl() {

        form = new NewPasswordForm(i18n.tr("New Password"), new Command() {

            @Override
            public void execute() {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.passwordReset(form.getValue());
            }
        });
        form.initContent();
        form.populate(null);
        add(form);

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
