/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerView;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.lease.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.LeaseListerView;
import com.propertyvista.crm.client.ui.crud.lease.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationListerView;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.PastLeaseListerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.PastLeaseListerViewImpl;

public class LeaseViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (LeaseListerView.class.equals(type)) {
                map.put(type, new LeaseListerViewImpl());
            } else if (LeaseViewerView.class.equals(type)) {
                map.put(type, new LeaseViewerViewImpl());
            } else if (LeaseEditorView.class.equals(type)) {
                map.put(type, new LeaseEditorViewImpl());

            } else if (PastLeaseListerView.class.equals(type)) {
                map.put(type, new PastLeaseListerViewImpl());

            } else if (LeaseApplicationListerView.class.equals(type)) {
                map.put(type, new LeaseApplicationListerViewImpl());
            } else if (LeaseApplicationViewerView.class.equals(type)) {
                map.put(type, new LeaseApplicationViewerViewImpl());
            } else if (LeaseApplicationEditorView.class.equals(type)) {
                map.put(type, new LeaseApplicationEditorViewImpl());

            } else if (BillViewerView.class.equals(type)) {
                map.put(type, new BillViewerViewImpl());

            } else if (OnlineMasterApplicationListerView.class.equals(type)) {
                map.put(type, new OnlineMasterApplicationListerViewImpl());
            } else if (OnlineMasterApplicationViewerView.class.equals(type)) {
                map.put(type, new OnlineMasterApplicationViewerViewImpl());

            } else if (ApplicationListerView.class.equals(type)) {
                map.put(type, new ApplicationListerViewImpl());
            } else if (ApplicationViewerView.class.equals(type)) {
                map.put(type, new ApplicationViewerViewImpl());
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
