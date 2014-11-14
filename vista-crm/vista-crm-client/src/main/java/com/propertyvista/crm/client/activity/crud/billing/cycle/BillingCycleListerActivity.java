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
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.cycle;

import com.pyx4j.site.client.backoffice.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleListerView;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;

public class BillingCycleListerActivity extends AbstractListerActivity<BillingCycleDTO> {

    public BillingCycleListerActivity(AppPlace place) {
        super(BillingCycleDTO.class, place, CrmSite.getViewFactory().getView(BillingCycleListerView.class));
    }

}
