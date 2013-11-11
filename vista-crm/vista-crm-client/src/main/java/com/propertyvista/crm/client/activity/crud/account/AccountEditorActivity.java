/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.account;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeEditorView;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class AccountEditorActivity extends CrmEditorActivity<EmployeeDTO> implements EmployeeEditorView.Presenter {

    public AccountEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(EmployeeEditorView.class), GWT.<AbstractCrudService<EmployeeDTO>> create(CrmUserService.class),
                EmployeeDTO.class);
    }

    @Override
    public void onPopulateSuccess(EmployeeDTO result) {
        ((EmployeeEditorView) getView()).restrictSecuritySensitiveControls(SecurityController.checkBehavior(VistaCrmBehavior.Organization), true);
        super.onPopulateSuccess(result);
    }
}
