/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.account;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsDialog;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.portal.rpc.shared.services.AbstractAccountRecoveryOptionsService;

/**
 * This one should use separate service (just for self management)
 */
public class AccountViewerActivity extends CrmViewerActivity<EmployeeDTO> implements EmployeeViewerView.Presenter {

    private final AbstractAccountRecoveryOptionsService accountRecoveryOptionsService;

    public AccountViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(EmployeeViewerView.class), GWT.<AbstractCrudService<EmployeeDTO>> create(CrmUserService.class));
        accountRecoveryOptionsService = GWT.<AbstractAccountRecoveryOptionsService> create(CrmAccountRecoveryOptionsUserService.class);
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new CrmSiteMap.PasswordChange();
        passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.EMPLOYEE.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

    @Override
    public void goToLoginHistory(CrmUser userStub) {
        AppPlace loginHistoryPlace = new CrmSiteMap.Account.LoginAttemptsLog();
        loginHistoryPlace.formPlace(userStub.getPrimaryKey());
        AppSite.getPlaceController().goTo(loginHistoryPlace);
    }

    @Override
    public void goToAccountRecoveryOptions(final String password) {

        AuthenticationRequest authRequest = EntityFactory.create(AuthenticationRequest.class);
        authRequest.password().setValue(password);
        accountRecoveryOptionsService.obtainRecoveryOptions(new DefaultAsyncCallback<AccountRecoveryOptionsDTO>() {
            @Override
            public void onSuccess(AccountRecoveryOptionsDTO result) {
                new AccountRecoveryOptionsDialog(//@formatter:off
                        password,
                        result,
                        SecurityController.checkBehavior(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion),
                        false,
                        accountRecoveryOptionsService
                ).show();//@formatter:on
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof UserRuntimeException) {
                    MessageDialog.error("", caught.getMessage());
                } else {
                    super.onFailure(caught);
                }
            }
        }, authRequest);

    }

    @Override
    public boolean canGoToAccountRecoveryOptions() {
        return true;
    }

    @Override
    protected void onPopulateSuccess(EmployeeDTO result) {
        ((EmployeeViewerView) getView()).restrictSecuritySensitiveControls(SecurityController.checkBehavior(VistaCrmBehavior.Organization), true);
        super.onPopulateSuccess(result);
    }

    @Override
    public boolean canClearSecurityQuestion() {
        return false;
    }

    @Override
    public void clearSecurityQuestionAction(DefaultAsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId) {
        // this is never possible since canClearSecurityQuestion always will return false        
    }

    @Override
    public boolean canSendPasswordResetEmail() {
        return false;
    }

    @Override
    public void sendPasswordResetEmailAction(DefaultAsyncCallback<VoidSerializable> defaultAsyncCallback, EmployeeDTO employeeId) {
        // do nothing: this is not permitted in this activity because it doesn't make a lot of sense
    }

}
