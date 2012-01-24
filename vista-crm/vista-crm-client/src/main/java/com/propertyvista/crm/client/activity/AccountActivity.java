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
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.OrganizationViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;

public class AccountActivity extends ViewerActivityBase<EmployeeDTO> implements EmployeeViewerView.Presenter {

    public AccountActivity(Place place) {
        super(place, OrganizationViewFactory.instance(EmployeeViewerView.class), GWT.<AbstractCrudService<EmployeeDTO>> create(EmployeeCrudService.class));
    }

    @Override
    public void goToChangePassword(Key userId) {
        AppSite.getPlaceController().goTo(new CrmSiteMap.PasswordChange(userId));
    }

}
