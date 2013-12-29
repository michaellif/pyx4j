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
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.portal.rpc.shared.services.AbstractAccountRecoveryOptionsService;

public class AccountRecoveryOptionsDialog extends Composite {

    public static I18n i18n = I18n.get(AccountRecoveryOptionsDialog.class);

    private final int CANCEL_TIMEOUT = 5 * 60 * 1000;

    private final AccountRecoveryOptionsForm recoveryOptionsForm;

    private final AbstractAccountRecoveryOptionsService service;

    private final String password;

    private final Timer cancellationTimer;

    private Dialog dialog;

    public AccountRecoveryOptionsDialog(String password, AccountRecoveryOptionsDTO recoveryOptions, boolean isSecurityQuestionRequired, boolean forceSetup,
            AbstractAccountRecoveryOptionsService accountRecoveryOptionsService) {

        this.service = accountRecoveryOptionsService;
        this.password = password;
        this.recoveryOptionsForm = new AccountRecoveryOptionsForm(true);
        this.recoveryOptionsForm.initContent();
        this.recoveryOptionsForm.setSecurityQuestionRequired(isSecurityQuestionRequired);
        this.recoveryOptionsForm.populate(recoveryOptions);
        this.cancellationTimer = new Timer() {
            @Override
            public void run() {
                AccountRecoveryOptionsDialog.this.dialog.hide(false);
            }
        };
        this.cancellationTimer.schedule(CANCEL_TIMEOUT);

        this.dialog = new Dialog(//@formatter:off
                i18n.tr("Account Recovery Options"), 
                (forceSetup ? new OkOption() { @Override public boolean onClickOk() { return AccountRecoveryOptionsDialog.this.onClickOk();} }                              
                            : new OkCancelOption() { 
                                @Override public boolean onClickOk() { return AccountRecoveryOptionsDialog.this.onClickOk();}
                                @Override public boolean onClickCancel() { return AccountRecoveryOptionsDialog.this.onClickCancel();}})            
                , this.recoveryOptionsForm.asWidget()
        ) { 
            @Override protected String optionTextOk() { 
                return i18n.tr("Update"); 
            }
        };//@formatter:on
    }

    public void show() {
        dialog.show();
    }

    protected void onUpdateRecoveryOptionsSuccess(AuthenticationResponse result) {

    }

    protected void onUpdateRecoveryOptionsFail(UserRuntimeException caught) {

    }

    private boolean onClickOk() {
        this.cancellationTimer.cancel();
        AccountRecoveryOptionsDTO recoveryOptions = recoveryOptionsForm.getValue().duplicate(AccountRecoveryOptionsDTO.class);
        recoveryOptions.password().setValue(password);
        service.updateRecoveryOptions(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                MessageDialog.info(i18n.tr("Account recovery options were updated successfully"));
                onUpdateRecoveryOptionsSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    MessageDialog.error("", caught.getMessage());
                    onUpdateRecoveryOptionsFail((UserRuntimeException) caught);
                } else {
                    super.onFailure(caught);
                }
            }
        }, recoveryOptions);
        return true;
    }

    private boolean onClickCancel() {
        this.cancellationTimer.cancel();
        return true;
    }

}
