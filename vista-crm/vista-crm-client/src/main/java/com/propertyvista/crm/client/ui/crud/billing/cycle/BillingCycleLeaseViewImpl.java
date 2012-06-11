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
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class BillingCycleLeaseViewImpl extends CrmViewerViewImplBase<BillingCycleLeaseDTO> implements BillingCycleLeaseView {

    private final IListerView<BillDataDTO> billLister;

    private final IListerView<PaymentRecordDTO> paymentLister;

    public BillingCycleLeaseViewImpl() {
        super(CrmSiteMap.Finance.BillingCycle.Lease.class, true);

        billLister = new ListerInternalViewImplBase<BillDataDTO>(new BillLister());
        paymentLister = new ListerInternalViewImplBase<PaymentRecordDTO>(new PaymentLister());
        paymentLister.getLister().getDataTablePanel().getAddButton().setVisible(false);

        // set main form here:
        setForm(new BillingCycleLeaseForm());
    }

    @Override
    public IListerView<BillDataDTO> getBillListerView() {
        return billLister;
    }

    @Override
    public IListerView<PaymentRecordDTO> getPaymentListerView() {
        return paymentLister;
    }
}
