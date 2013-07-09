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
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.ui.crud.customer.creditcheck.CustomerCreditCheckLongReportViewerView;
import com.propertyvista.crm.client.ui.crud.customer.creditcheck.CustomerCreditCheckLongReportViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.FormerGuarantorListerView;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.FormerGuarantorListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorEditorView;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorListerView;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorViewerView;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.screening.CustomerScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.customer.screening.CustomerScreeningEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.screening.CustomerScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.customer.screening.CustomerScreeningViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.tenant.FormerTenantListerView;
import com.propertyvista.crm.client.ui.crud.customer.tenant.FormerTenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantViewerViewImpl;

public class CustomerViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (TenantListerView.class.equals(type)) {
                map.put(type, new TenantListerViewImpl());
            } else if (TenantViewerView.class.equals(type)) {
                map.put(type, new TenantViewerViewImpl());
            } else if (TenantEditorView.class.equals(type)) {
                map.put(type, new TenantEditorViewImpl());

            } else if (FormerTenantListerView.class.equals(type)) {
                map.put(type, new FormerTenantListerViewImpl());

            } else if (FormerGuarantorListerView.class.equals(type)) {
                map.put(type, new FormerGuarantorListerViewImpl());

            } else if (GuarantorListerView.class.equals(type)) {
                map.put(type, new GuarantorListerViewImpl());
            } else if (GuarantorViewerView.class.equals(type)) {
                map.put(type, new GuarantorViewerViewImpl());
            } else if (GuarantorEditorView.class.equals(type)) {
                map.put(type, new GuarantorEditorViewImpl());

            } else if (CustomerScreeningViewerView.class.equals(type)) {
                map.put(type, new CustomerScreeningViewerViewImpl());
            } else if (CustomerScreeningEditorView.class.equals(type)) {
                map.put(type, new CustomerScreeningEditorViewImpl());

            } else if (CustomerCreditCheckLongReportViewerView.class.equals(type)) {
                map.put(type, new CustomerCreditCheckLongReportViewerViewImpl());
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
