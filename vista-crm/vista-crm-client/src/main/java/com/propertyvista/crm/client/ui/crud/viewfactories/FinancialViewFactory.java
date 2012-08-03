/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerView;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleBillListerView;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleBillListerViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLeaseListerView;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleView;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentEditorView;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentViewerView;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferListerView;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferListerViewImpl;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferViewerView;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferViewerViewImpl;

public class FinancialViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (BillingCycleView.class.equals(type)) {
                map.put(type, new BillingCycleViewImpl());

            } else if (BillingCycleBillListerView.class.equals(type)) {
                map.put(type, new BillingCycleBillListerViewImpl());

            } else if (BillingCycleLeaseListerView.class.equals(type)) {
                map.put(type, new BillingCycleLeaseListerViewImpl());

            } else if (BillViewerView.class.equals(type)) {
                map.put(type, new BillViewerViewImpl());

            } else if (PaymentEditorView.class.equals(type)) {
                map.put(type, new PaymentEditorViewImpl());
            } else if (PaymentViewerView.class.equals(type)) {
                map.put(type, new PaymentViewerViewImpl());

            } else if (AggregatedTransferListerView.class.equals(type)) {
                map.put(type, new AggregatedTransferListerViewImpl());
            } else if (AggregatedTransferViewerView.class.equals(type)) {
                map.put(type, new AggregatedTransferViewerViewImpl());
            }
        }
        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
