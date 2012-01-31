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
package com.propertyvista.crm.client.activity.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.BillCrudService;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerActivity extends ViewerActivityBase<LeaseDTO> implements LeaseViewerView.Presenter {

    private final IListerView.Presenter<Bill> billLister;

    @SuppressWarnings("unchecked")
    public LeaseViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(LeaseViewerView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class));

        billLister = new ListerActivityBase<Bill>(place, ((LeaseViewerView) view).getBillListerView(),
                (AbstractCrudService<Bill>) GWT.create(BillCrudService.class), Bill.class);
    }

    @Override
    public void createMasterApplication() {
        ((LeaseCrudService) service).createMasterApplication(new AsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, entityId);
    }

    @Override
    public void runBill() {
        // TODO Auto-generated method stub

    }

    @Override
    public Presenter<Bill> getBillListerPresenter() {
        return billLister;
    }

    @Override
    protected void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);

        List<DataTableFilterData> preDefinedFilters = new ArrayList<DataTableFilterData>();
        preDefinedFilters.add(new DataTableFilterData(EntityFactory.getEntityPrototype(Bill.class).billingAccount().id().getPath(), Operators.is, result
                .leaseFinancial().billingAccount().getPrimaryKey()));
        billLister.setPreDefinedFilters(preDefinedFilters);
        billLister.populate();
    }

}
