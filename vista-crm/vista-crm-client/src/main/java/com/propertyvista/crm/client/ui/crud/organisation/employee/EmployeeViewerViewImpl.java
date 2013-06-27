/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.PasswordTextBox;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.security.CrmUser;

public class EmployeeViewerViewImpl extends CrmViewerViewImplBase<EmployeeDTO> implements EmployeeViewerView {

    private static final I18n i18n = I18n.get(EmployeeViewerViewImpl.class);

    private final MenuItem passwordAction;

    private final MenuItem viewLoginLogAction;

    private final MenuItem accountRecoveryOptionsAction;

    private final MenuItem clearSecurityQuestionAction;

    private final MenuItem sendPasswordResetEmailAction;

    public EmployeeViewerViewImpl() {
        setForm(new EmployeeForm(this));

        // Add actions:        
        passwordAction = new MenuItem(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((EmployeeViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().user().getPrimaryKey(), getForm().getValue().name()
                        .getStringView());
            }
        });
        addAction(passwordAction);

        viewLoginLogAction = new MenuItem(i18n.tr("View Login History"), new Command() {
            @Override
            public void execute() {
                ((EmployeeViewerView.Presenter) getPresenter()).goToLoginHistory(getForm().getValue().user());
            }
        });
        addAction(viewLoginLogAction);

        accountRecoveryOptionsAction = new MenuItem(i18n.tr("Account Recovery Options"), new Command() {
            @Override
            public void execute() {
                new GetPasswordDialog().show();
            }
        });
        addAction(accountRecoveryOptionsAction);

        clearSecurityQuestionAction = new MenuItem(i18n.tr("Clear Security Question"), new Command() {

            @Override
            public void execute() {
                ((EmployeeViewerView.Presenter) getPresenter()).clearSecurityQuestionAction(new DefaultAsyncCallback<VoidSerializable>() {

                    @Override
                    public void onSuccess(VoidSerializable result) {
                        MessageDialog.info(i18n.tr("Security question has been cleared successfully"));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        if (caught instanceof UserRuntimeException) {
                            MessageDialog.error("", ((UserRuntimeException) caught).getMessage());
                        } else {
                            super.onFailure(caught);
                        }
                    }

                }, getForm().getValue().<EmployeeDTO> createIdentityStub());

            }

        });
        addAction(clearSecurityQuestionAction);

        sendPasswordResetEmailAction = new MenuItem(i18n.tr("Send Password Reset Email"), new Command() {

            @Override
            public void execute() {
                ((EmployeeViewerView.Presenter) getPresenter()).sendPasswordResetEmailAction(new DefaultAsyncCallback<VoidSerializable>() {

                    @Override
                    public void onSuccess(VoidSerializable result) {
                        MessageDialog.info(i18n.tr("Password has been sent successfully"));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        if (caught instanceof UserRuntimeException) {
                            MessageDialog.error("", ((UserRuntimeException) caught).getMessage());
                        } else {
                            super.onFailure(caught);
                        }
                    }

                }, getForm().getValue().user().<CrmUser> createIdentityStub());
            }

        });
        addAction(sendPasswordResetEmailAction);

    }

    @Override
    public void reset() {
        setActionVisible(passwordAction, false);
        setActionVisible(viewLoginLogAction, false);
        setActionVisible(accountRecoveryOptionsAction, false);
        setActionVisible(clearSecurityQuestionAction, false);
        setActionVisible(sendPasswordResetEmailAction, false);
        super.reset();
    }

    @Override
    public void populate(EmployeeDTO value) {
        super.populate(value);

        setActionVisible(passwordAction, getPresenter().canEdit());
        setActionVisible(viewLoginLogAction, getPresenter().canEdit());
        setActionVisible(accountRecoveryOptionsAction, ((EmployeeViewerView.Presenter) getPresenter()).canGoToAccountRecoveryOptions());
        setActionVisible(clearSecurityQuestionAction, ((EmployeeViewerView.Presenter) getPresenter()).canClearSecurityQuestion());
        setActionVisible(sendPasswordResetEmailAction, ((EmployeeViewerView.Presenter) getPresenter()).canSendPasswordResetEmail());
    }

    private class GetPasswordDialog extends OkCancelDialog {

        private final PasswordTextBox passwordBox;

        public GetPasswordDialog() {
            super(i18n.tr("Your password is required to proceed"));
            passwordBox = new PasswordTextBox();
            setBody(passwordBox);
        }

        @Override
        public boolean onClickOk() {
            ((EmployeeViewerView.Presenter) getPresenter()).goToAccountRecoveryOptions(passwordBox.getText());
            return true;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }
    }

    @Override
    public void restrictSecuritySensitiveControls(boolean isManager, boolean isSelfEdit) {
        ((EmployeeForm) getForm()).restrictSecurityRelatedControls(isManager, isSelfEdit);
    }
}