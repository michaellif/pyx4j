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
package com.propertyvista.operations.client.activity.crud.adminusers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserListerView;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;

public class AdminUserListerActivity extends AbstractListerActivity<OperationsUserDTO> {

    public AdminUserListerActivity(Place place) {
        super(place, OperationsSite.getViewFactory().instantiate(AdminUserListerView.class), GWT.<AdminUserCrudService> create(AdminUserCrudService.class),
                OperationsUserDTO.class);
    }
}
