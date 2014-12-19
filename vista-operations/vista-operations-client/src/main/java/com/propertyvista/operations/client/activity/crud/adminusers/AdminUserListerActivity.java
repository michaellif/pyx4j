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

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserListerView;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;

public class AdminUserListerActivity extends AbstractPrimeListerActivity<OperationsUserDTO> {

    public AdminUserListerActivity(AppPlace place) {
        super(OperationsUserDTO.class, place, OperationsSite.getViewFactory().getView(AdminUserListerView.class));
    }
}
