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
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.adminusers;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.ViewerActivityBase;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.AdminUserDTO;
import com.propertyvista.admin.rpc.services.AdminUserCrudService;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;

public class AdminUserViewerActivity extends ViewerActivityBase<AdminUserDTO> implements AdminUserViewerView.Presenter {

    public AdminUserViewerActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(AdminUserViewerView.class), GWT.<AdminUserCrudService> create(AdminUserCrudService.class));
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new AdminSiteMap.PasswordChange();
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_NAME_ARG, userName);
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.ADMIN.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

}
