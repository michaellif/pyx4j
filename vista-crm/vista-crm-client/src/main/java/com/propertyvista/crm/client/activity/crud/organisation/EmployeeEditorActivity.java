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
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.OrganizationViewFactory;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;

public class EmployeeEditorActivity extends EditorActivityBase<EmployeeDTO> implements EmployeeEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public EmployeeEditorActivity(Place place) {
        super(place, OrganizationViewFactory.instance(EmployeeEditorView.class), (AbstractCrudService<EmployeeDTO>) GWT.create(EmployeeCrudService.class),
                EmployeeDTO.class);
    }

    @Override
    public void changePassword(String old, String newOne) {
        // TODO Auto-generated method stub
    }
}
