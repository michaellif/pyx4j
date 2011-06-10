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

import com.propertyvista.crm.client.ui.crud.IView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryListerView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.InquiryViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseListerView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.TenantViewerViewImpl;

public class TenantViewFactory extends ViewFactoryBase {

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (TenantListerView.class.equals(type)) {
                map.put(type, new TenantListerViewImpl());
            } else if (TenantViewerView.class.equals(type)) {
                map.put(type, new TenantViewerViewImpl());
            } else if (TenantEditorView.class.equals(type)) {
                map.put(type, new TenantEditorViewImpl());
            } else if (LeaseListerView.class.equals(type)) {
                map.put(type, new LeaseListerViewImpl());
            } else if (LeaseViewerView.class.equals(type)) {
                map.put(type, new LeaseViewerViewImpl());
            } else if (LeaseEditorView.class.equals(type)) {
                map.put(type, new LeaseEditorViewImpl());
            } else if (ApplicationListerView.class.equals(type)) {
                map.put(type, new ApplicationListerViewImpl());
            } else if (ApplicationViewerView.class.equals(type)) {
                map.put(type, new ApplicationViewerViewImpl());
            } else if (ApplicationEditorView.class.equals(type)) {
                map.put(type, new ApplicationEditorViewImpl());
            } else if (InquiryListerView.class.equals(type)) {
                map.put(type, new InquiryListerViewImpl());
            } else if (InquiryViewerView.class.equals(type)) {
                map.put(type, new InquiryViewerViewImpl());
            } else if (InquiryEditorView.class.equals(type)) {
                map.put(type, new InquiryEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
