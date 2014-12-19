/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 */
package com.propertyvista.operations.client.activity.crud.adminusers;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeViewerActivity;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;

public class AdminUserViewerActivity extends AbstractPrimeViewerActivity<OperationsUserDTO> implements AdminUserViewerView.Presenter {

    public AdminUserViewerActivity(CrudAppPlace place) {
        super(OperationsUserDTO.class, place, OperationsSite.getViewFactory().getView(AdminUserViewerView.class), GWT
                .<AdminUserCrudService> create(AdminUserCrudService.class));
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new OperationsSiteMap.PasswordChange();
        passwordChangePlace.placeArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.placeArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_NAME_ARG, userName);
        passwordChangePlace.placeArg(PasswordChangeView.PasswordChangePresenter.PRINCIPAL_CLASS, PasswordChangeView.PasswordChangePresenter.PrincipalClass.ADMIN.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

}
