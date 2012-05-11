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
package com.propertyvista.crm.client.activity;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.OrganizationViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;

/**
 * This one should use separate service (just for self management)
 */
public class AccountViewerActivity extends CrmViewerActivity<EmployeeDTO> implements EmployeeViewerView.Presenter {

    public AccountViewerActivity(CrudAppPlace place) {
        super(place, OrganizationViewFactory.instance(EmployeeViewerView.class), GWT.<AbstractCrudService<EmployeeDTO>> create(CrmUserService.class));
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new CrmSiteMap.PasswordChange();
        passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.EMPLOYEE.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

}
