/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.account;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.ViewerActivityBase;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.operations.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserService;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;

public class AccountViewerActivity extends ViewerActivityBase<OperationsUserDTO> implements AdminUserViewerView.Presenter {

    public AccountViewerActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(AdminUserViewerView.class), GWT.<AdminUserService> create(AdminUserService.class));
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new OperationsSiteMap.PasswordChange();
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_NAME_ARG, userName);
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.ADMIN.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

}
