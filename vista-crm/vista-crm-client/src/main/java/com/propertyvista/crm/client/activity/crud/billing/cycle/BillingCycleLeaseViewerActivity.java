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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLeaseView;
import com.propertyvista.crm.client.ui.crud.viewfactories.FinancialViewFactory;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleLeaseCrudService;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingCycleLeaseViewerActivity extends CrmViewerActivity<BillingCycleLeaseDTO> implements BillingCycleLeaseView.Presenter {

    private final IListerView.Presenter<BillDataDTO> billLister;

    public BillingCycleLeaseViewerActivity(CrudAppPlace place) {
        super(place, FinancialViewFactory.instance(BillingCycleLeaseView.class), GWT.<BillingCycleLeaseCrudService> create(BillingCycleLeaseCrudService.class));

        billLister = new ListerActivityBase<BillDataDTO>(place, ((BillingCycleLeaseView) getView()).getBillListerView(),
                GWT.<BillCrudService> create(BillCrudService.class), BillDataDTO.class);
    }

    @Override
    protected void onPopulateSuccess(BillingCycleLeaseDTO result) {
        super.onPopulateSuccess(result);

        populateBills(result.lease());
    }

    protected void populateBills(Lease result) {
        List<Criterion> preDefinedFilters = new ArrayList<Criterion>();
        preDefinedFilters.add(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillDataDTO.class).bill().billingAccount().id(), result.billingAccount()
                .getPrimaryKey()));
        billLister.setPreDefinedFilters(preDefinedFilters);
        billLister.populate();
    }
}
