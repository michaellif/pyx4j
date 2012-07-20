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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.PasswordTextBox;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeViewerViewImpl extends CrmViewerViewImplBase<EmployeeDTO> implements EmployeeViewerView {

    private static final I18n i18n = I18n.get(EmployeeViewerViewImpl.class);

    private final Button passwordAction;

    private final Button viewLoginLogAction;

    private final Button accountRecoveryOptionsAction;

    public EmployeeViewerViewImpl() {
        super(CrmSiteMap.Organization.Employee.class, new EmployeeForm(true));

        // Add actions:        
        passwordAction = new Button(i18n.tr("Change Password"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((EmployeeViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().user().getPrimaryKey(), getForm().getValue().name()
                        .getStringView());
            }
        });

        viewLoginLogAction = new Button(i18n.tr("View Login History"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((EmployeeViewerView.Presenter) getPresenter()).goToLoginHistory(getForm().getValue().user());
            }
        });

        accountRecoveryOptionsAction = new Button(i18n.tr("Account Recovery Options"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new GetPasswordDialog().show();
            }
        });

        addHeaderToolbarTwoItem(passwordAction.asWidget());
        addHeaderToolbarTwoItem(accountRecoveryOptionsAction.asWidget());
        addHeaderToolbarTwoItem(viewLoginLogAction.asWidget());
    }

    @Override
    public void populate(EmployeeDTO value) {
        super.populate(value);
        passwordAction.setVisible((getPresenter()).canEdit());
        viewLoginLogAction.setVisible((getPresenter()).canEdit());
        accountRecoveryOptionsAction.setVisible(((EmployeeViewerView.Presenter) getPresenter()).canGoToAccountRecoveryOptions());
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