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
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationListerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseListerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseListerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.TenantScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.TenantScreeningEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.tenant.screening.TenantScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.tenant.screening.TenantScreeningViewerViewImpl;

public class TenantViewFactory extends ViewFactoryBase {

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (TenantListerView.class.equals(type)) {
                map.put(type, new TenantListerViewImpl());
            } else if (TenantViewerView.class.equals(type)) {
                map.put(type, new TenantViewerViewImpl());
            } else if (TenantEditorView.class.equals(type)) {
                map.put(type, new TenantEditorViewImpl());

            } else if (TenantScreeningViewerView.class.equals(type)) {
                map.put(type, new TenantScreeningViewerViewImpl());
            } else if (TenantScreeningEditorView.class.equals(type)) {
                map.put(type, new TenantScreeningEditorViewImpl());

            } else if (LeaseListerView.class.equals(type)) {
                map.put(type, new LeaseListerViewImpl());
            } else if (LeaseViewerView.class.equals(type)) {
                map.put(type, new LeaseViewerViewImpl());
            } else if (LeaseEditorView.class.equals(type)) {
                map.put(type, new LeaseEditorViewImpl());

            } else if (MasterApplicationListerView.class.equals(type)) {
                map.put(type, new MasterApplicationListerViewImpl());
            } else if (MasterApplicationViewerView.class.equals(type)) {
                map.put(type, new MasterApplicationViewerViewImpl());
            } else if (MasterApplicationEditorView.class.equals(type)) {
                map.put(type, new MasterApplicationEditorViewImpl());

            } else if (ApplicationListerView.class.equals(type)) {
                map.put(type, new ApplicationListerViewImpl());
            } else if (ApplicationViewerView.class.equals(type)) {
                map.put(type, new ApplicationViewerViewImpl());
            } else if (ApplicationEditorView.class.equals(type)) {
                map.put(type, new ApplicationEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
