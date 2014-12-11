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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeePreferencesViewerView;
import com.propertyvista.crm.rpc.services.profile.CrmUserDeliveryPreferencesCrudService;
import com.propertyvista.dto.CrmUserDeliveryPreferencesDTO;

/**
 * This one should use separate service (just for self management)
 */
public class AccountPreferencesViewerActivity extends CrmViewerActivity<CrmUserDeliveryPreferencesDTO> {

    public AccountPreferencesViewerActivity(CrudAppPlace place) {
        super(CrmUserDeliveryPreferencesDTO.class, place, CrmSite.getViewFactory().getView(EmployeePreferencesViewerView.class), GWT
                .<AbstractCrudService<CrmUserDeliveryPreferencesDTO>> create(CrmUserDeliveryPreferencesCrudService.class));
    }

}
