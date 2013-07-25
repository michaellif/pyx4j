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

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLeaseListerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseListService;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.dto.LeaseDTO;

public class BillingCycleLeaseListerActivity extends AbstractListerActivity<LeaseDTO> {

    private Key billingCycleId;

    private Key billingTypeId;

    public BillingCycleLeaseListerActivity(AppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(BillingCycleLeaseListerView.class), GWT
                .<BillingCycleLeaseListService> create(BillingCycleLeaseListService.class), LeaseDTO.class);

        String val;
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID)) != null) {
            billingCycleId = new Key(val);
        }
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BT_ID)) != null) {
            billingTypeId = new Key(val);
        }

        assert billingTypeId != null;
    }

    @Override
    public void populate() {
        clearPreDefinedFilters();
        LeaseDTO proto = EntityFactory.getEntityPrototype(LeaseDTO.class);
        addPreDefinedFilter(PropertyCriterion.eq(proto.billingAccount().billingType(), EntityFactory.createIdentityStub(BillingType.class, billingTypeId)));
        if (billingCycleId != null) {
            // add 'not run' criteria here:
//TODO Review VladL and VladS, commented out by VladS
//            addPreDefinedFilter(PropertyCriterion.eq(proto.billingAccount().bills().$().billingCycle(),
//                    EntityFactory.createIdentityStub(BillingType.class, billingCycleId)));
//            addPreDefinedFilter(PropertyCriterion.notExists(proto.billingAccount().bills()));
            throw new Error("Call ValdL and VladS");
        }
        super.populate();
    }
}
