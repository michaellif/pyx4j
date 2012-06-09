/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.cycle;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleView;
import com.propertyvista.crm.client.ui.crud.viewfactories.FinancialViewFactory;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseCrudService;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingCycleViewerActivity extends CrmViewerActivity<BillingCycleDTO> implements BillingCycleView.Presenter {

    private final IListerView.Presenter<BillingCycleLeaseDTO> leaseLister;

    public BillingCycleViewerActivity(CrudAppPlace place) {
        super(place, FinancialViewFactory.instance(BillingCycleView.class), GWT.<BillingCycleCrudService> create(BillingCycleCrudService.class));

        leaseLister = new ListerActivityBase<BillingCycleLeaseDTO>(place, ((BillingCycleView) getView()).getLeaseListerView(),
                GWT.<BillingCycleLeaseCrudService> create(BillingCycleLeaseCrudService.class), BillingCycleLeaseDTO.class);
    }

    @Override
    public void onStop() {
        ((AbstractActivity) leaseLister).onStop();
        super.onStop();
    }

    @Override
    protected void onPopulateSuccess(BillingCycleDTO result) {
        super.onPopulateSuccess(result);

        leaseLister.clearPreDefinedFilters();
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        leaseLister.addPreDefinedFilter(PropertyCriterion.eq(criteria.proto().unit().belongsTo(), result.building()));
        leaseLister.addPreDefinedFilter(PropertyCriterion.eq(criteria.proto().billingAccount().billingType(), result.billingType()));
        leaseLister.populate();
    }
}
