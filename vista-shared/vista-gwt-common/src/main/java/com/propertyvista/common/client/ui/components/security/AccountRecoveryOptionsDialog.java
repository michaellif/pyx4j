/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.user.client.Timer;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.portal.rpc.shared.services.AbstractAccountRecoveryOptionsService;

public class AccountRecoveryOptionsDialog extends OkCancelDialog {

    public static I18n i18n = I18n.get(AccountRecoveryOptionsDialog.class);

    private final int CANCEL_TIMEOUT = 5 * 60 * 1000;

    private final AccountRecoveryOptionsForm recoveryOptionsForm;

    private final AbstractAccountRecoveryOptionsService service;

    private final String password;

    private final Timer cancellationTimer;

    public AccountRecoveryOptionsDialog(String password, AccountRecoveryOptionsDTO recoveryOptions, boolean isSecurityQuestionRequired,
            AbstractAccountRecoveryOptionsService accountRecoveryOptionsService) {
        super(i18n.tr("Account Recovery Options"));
        this.service = accountRecoveryOptionsService;
        this.password = password;
        this.recoveryOptionsForm = new AccountRecoveryOptionsForm(true);
        this.recoveryOptionsForm.initContent();
        this.recoveryOptionsForm.setSecurityQuestionRequired(isSecurityQuestionRequired);
        this.recoveryOptionsForm.populate(recoveryOptions);
        this.cancellationTimer = new Timer() {
            @Override
            public void run() {
                AccountRecoveryOptionsDialog.this.hide();
            }
        };
        this.cancellationTimer.schedule(CANCEL_TIMEOUT);

        setBody(this.recoveryOptionsForm.asWidget());
    }

    @Override
    public boolean onClickOk() {
        this.cancellationTimer.cancel();
        AccountRecoveryOptionsDTO recoveryOptions = recoveryOptionsForm.getValue().duplicate(AccountRecoveryOptionsDTO.class);
        recoveryOptions.password().setValue(password);
        service.updateRecoveryOptions(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                if (result != null) {
                    ClientContext.authenticated(result);
                }
                MessageDialog.info(i18n.tr("Account recovery options were updated successfully"));
            }
        }, recoveryOptions);
        return true;
    }

    @Override
    public boolean onClickCancel() {
        this.cancellationTimer.cancel();
        return super.onClickCancel();
    }

    @Override
    protected String optionTextOk() {
        return i18n.tr("Update");
    }

}
