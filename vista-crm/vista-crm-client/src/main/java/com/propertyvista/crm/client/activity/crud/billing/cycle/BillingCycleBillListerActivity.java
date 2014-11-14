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

import java.util.Collection;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleBillListerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;

public class BillingCycleBillListerActivity extends AbstractPrimeListerActivity<BillDataDTO> implements BillingCycleBillListerView.Presenter {

    private Key billingCycleId;

    private Bill.BillStatus billStatusValue;

    public BillingCycleBillListerActivity(AppPlace place) {
        super(BillDataDTO.class, place, CrmSite.getViewFactory().getView(BillingCycleBillListerView.class));

        String val;
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID)) != null) {
            billingCycleId = new Key(val);
        }
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BILL_STATUS)) != null) {
            billStatusValue = Enum.valueOf(Bill.BillStatus.class, val);
        }

        assert billingCycleId != null;
        assert billStatusValue != null;

        ((BillingCycleBillListerView) getView()).setActionButtonsVisible(billStatusValue == Bill.BillStatus.Finished);
    }

    @Override
    public void populate() {
        clearPreDefinedFilters();
        BillDataDTO proto = EntityFactory.getEntityPrototype(BillDataDTO.class);
        addPreDefinedFilter(PropertyCriterion.eq(proto.bill().billingCycle(), EntityFactory.createIdentityStub(BillingCycle.class, billingCycleId)));
        addPreDefinedFilter(PropertyCriterion.eq(proto.bill().billStatus(), billStatusValue));
        super.populate();
    }

    @Override
    public void confirm(Collection<BillDataDTO> bills) {
        GWT.<BillingCycleBillListService> create(BillingCycleBillListService.class).confirm(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, new Vector<BillDataDTO>(bills));
    }

    @Override
    public void reject(Collection<BillDataDTO> bills, String reason) {
        GWT.<BillingCycleBillListService> create(BillingCycleBillListService.class).reject(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, new Vector<BillDataDTO>(bills), reason);
    }

    @Override
    public void print(Collection<BillDataDTO> bills) {
        for (BillDataDTO bill : bills) {
            EntityQueryCriteria<Bill> criteria = new EntityQueryCriteria<Bill>(Bill.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().id(), bill.bill()));
            new ReportDialog("Bill", "Creating Bill...").start(GWT.<BillPrintService> create(BillPrintService.class), criteria);
        }
    }
}
