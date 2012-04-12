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

import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorEditorView;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorListerView;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorListerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorViewerView;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.lease.tenant.TenantViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.PastTenantListerView;
import com.propertyvista.crm.client.ui.crud.tenant.PastTenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.EquifaxResultViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.EquifaxResultViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningViewerViewImpl;

public class ClientViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (PersonScreeningViewerView.class.equals(type)) {
                map.put(type, new PersonScreeningViewerViewImpl());
            } else if (PersonScreeningEditorView.class.equals(type)) {
                map.put(type, new PersonScreeningEditorViewImpl());

            } else if (EquifaxResultViewerView.class.equals(type)) {
                map.put(type, new EquifaxResultViewerViewImpl());

            } else if (PastTenantListerView.class.equals(type)) {
                map.put(type, new PastTenantListerViewImpl());

            } else if (TenantListerView.class.equals(type)) {
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
