/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.pmc.EquifaxApprovalView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.EquifaxSetupRequestDTO;
import com.propertyvista.admin.rpc.services.EquifaxApprovalCrudService;

public class EquifaxApprovalViewActivity extends AdminViewerActivity<EquifaxSetupRequestDTO> {

    public EquifaxApprovalViewActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(EquifaxApprovalView.class), GWT
                .<AbstractCrudService<EquifaxSetupRequestDTO>> create(EquifaxApprovalCrudService.class));
    }

}
