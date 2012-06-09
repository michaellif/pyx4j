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

import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface BillingCycleLeaseView extends IViewerView<BillingCycleLeaseDTO> {

    interface Presenter extends IViewerView.Presenter {

    }

    IListerView<BillDataDTO> getBillListerView();

    IListerView<PaymentRecordDTO> getPaymentListerView();
}
