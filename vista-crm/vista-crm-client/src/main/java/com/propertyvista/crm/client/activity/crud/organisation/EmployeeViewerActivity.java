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
package com.propertyvista.crm.client.activity.crud.organisation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.ac.CRMUserSecurityActions;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class EmployeeViewerActivity extends CrmViewerActivity<EmployeeDTO> implements EmployeeViewerView.Presenter {

    private EmployeeDTO entity;

    public EmployeeViewerActivity(CrudAppPlace place) {
        super(EmployeeDTO.class, place, CrmSite.getViewFactory().getView(EmployeeViewerView.class), GWT
                .<AbstractCrudService<EmployeeDTO>> create(EmployeeCrudService.class));
    }

    @Override
    public boolean canEdit() {
        if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(entity.user().getPrimaryKey())) {
            return SecurityController.check(VistaCrmBehavior.AccountSelf);
        } else {
            return super.canEdit();
        }
    }

    @Override
    public boolean canChangePassword() {
        return SecurityController.check(new ActionPermission(CRMUserSecurityActions.class)) //
                || ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(entity.user().getPrimaryKey());
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new CrmSiteMap.PasswordChange();
        passwordChangePlace.queryArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.queryArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_NAME_ARG, userName);
        passwordChangePlace.queryArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_CLASS, PasswordChangeView.PasswordChangePresenter.PrincipalClass.EMPLOYEE.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

    @Override
    public boolean canViewLoginLog() {
        return true;
    }

    @Override
    public void goToLoginHistory(CrmUser userStub) {
        AppPlace loginHistoryPlace = new CrmSiteMap.Account.LoginAttemptsLog();
        loginHistoryPlace.formPlace(userStub.getPrimaryKey());
        AppSite.getPlaceController().goTo(loginHistoryPlace);
    }

    @Override
    protected void onPopulateSuccess(EmployeeDTO result) {
        this.entity = result;
        ((EmployeeViewerView) getView()).restrictSecuritySensitiveControls(SecurityController.check(VistaCrmBehavior.EmployeeFull), ClientContext
                .getUserVisit().getPrincipalPrimaryKey().equals(result.user().getPrimaryKey()));
        super.onPopulateSuccess(result);
    }

    @Override
    public void goToAccountRecoveryOptions(String password) {
        // supposed to be implemented only for account activity
    }

    @Override
    public boolean canGoToAccountRecoveryOptions() {
        return false;
    }

    @Override
    public void clearSecurityQuestionAction(DefaultAsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId) {
        ((EmployeeCrudService) getService()).clearSecurityQuestion(asyncCallback, employeeId);
    }

    @Override
    public void sendPasswordResetEmailAction(DefaultAsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId) {
        ((EmployeeCrudService) getService()).sendPasswordResetEmail(asyncCallback, employeeId);
    }
}
