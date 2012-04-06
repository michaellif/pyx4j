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

import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.OnlineMasterApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseListerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.EquifaxResultViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.EquifaxResultViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorListerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorViewerViewImpl;

public class TenantViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (TenantListerView.class.equals(type)) {
                map.put(type, new TenantListerViewImpl());
            } else if (TenantViewerView.class.equals(type)) {
                map.put(type, new TenantViewerViewImpl());
            } else if (TenantEditorView.class.equals(type)) {
                map.put(type, new TenantEditorViewImpl());

            } else if (GuarantorListerView.class.equals(type)) {
                map.put(type, new GuarantorListerViewImpl());
            } else if (GuarantorViewerView.class.equals(type)) {
                map.put(type, new GuarantorViewerViewImpl());
            } else if (GuarantorEditorView.class.equals(type)) {
                map.put(type, new GuarantorEditorViewImpl());

            } else if (PersonScreeningViewerView.class.equals(type)) {
                map.put(type, new PersonScreeningViewerViewImpl());
            } else if (PersonScreeningEditorView.class.equals(type)) {
                map.put(type, new PersonScreeningEditorViewImpl());

            } else if (LeaseListerView.class.equals(type)) {
                map.put(type, new LeaseListerViewImpl());
            } else if (LeaseViewerView.class.equals(type)) {
                map.put(type, new LeaseViewerViewImpl());
            } else if (LeaseEditorView.class.equals(type)) {
                map.put(type, new LeaseEditorViewImpl());

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

            } else if (EquifaxResultViewerView.class.equals(type)) {
                map.put(type, new EquifaxResultViewerViewImpl());
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
