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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;

import com.propertyvista.common.client.ui.components.login.RetrievePasswordForm;

public class RetrievePasswordViewImpl extends FlowPanel implements RetrievePasswordView {

    private static I18n i18n = I18n.get(RetrievePasswordViewImpl.class);

    private Presenter presenter;

    private final CEntityEditor<PasswordRetrievalRequest> form;

    public RetrievePasswordViewImpl() {

        form = new RetrievePasswordForm(i18n.tr("Retrieve Password"), new Command() {
            @Override
            public void execute() {
                form.setVisited(true);
                if (!form.isValid()) {
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                }
                presenter.retrievePassword(form.getValue());
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
}
