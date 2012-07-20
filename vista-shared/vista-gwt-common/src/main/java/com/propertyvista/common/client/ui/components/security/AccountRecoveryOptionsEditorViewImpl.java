/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.EditorViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class AccountRecoveryOptionsEditorViewImpl extends EditorViewImplBase<AccountRecoveryOptionsDTO> implements AccountRecoveryOptionsEditorView {

    private static final I18n i18n = I18n.get(AccountRecoveryOptionsEditorViewImpl.class);

    private final Button btnSave;

    public AccountRecoveryOptionsEditorViewImpl() {
        setForm(new AccountRecoveryOptionsForm());
        setCaption(i18n.tr("Account Recovery Options"));
        btnSave = new Button(i18n.tr("Save"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (getForm().isValid()) {
                    getPresenter().save();
                } else {
                    getForm().setUnconditionalValidationErrorRendering(true);
                    showValidationDialog();
                }
            }
        });
        addFooterToolbarItem(btnSave);
    }

    @Override
    public void setSecurityQuestionRequired(boolean isSecurityQuestionEntiryRequired) {
        ((AccountRecoveryOptionsForm) getForm()).setSecurityQuestionRequired(isSecurityQuestionEntiryRequired);
    }
}
