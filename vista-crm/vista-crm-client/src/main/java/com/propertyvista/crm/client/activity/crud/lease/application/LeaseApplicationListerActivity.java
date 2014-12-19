/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.client.activity.crud.lease.application;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseListerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationListerView;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationListerActivity extends LeaseListerActivityBase<LeaseApplicationDTO> {

    public LeaseApplicationListerActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LeaseApplicationListerView.class), LeaseApplicationDTO.class);
    }

}
