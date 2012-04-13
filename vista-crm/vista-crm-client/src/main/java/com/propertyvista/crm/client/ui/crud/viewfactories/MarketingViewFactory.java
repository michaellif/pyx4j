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

import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.service.ServiceEditorView;
import com.propertyvista.crm.client.ui.crud.building.catalog.service.ServiceEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.catalog.service.ServiceViewerView;
import com.propertyvista.crm.client.ui.crud.building.catalog.service.ServiceViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadEditorView;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadViewerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentEditorView;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentViewerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingEditorView;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingViewerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.customer.tenant.FutureTenantListerView;
import com.propertyvista.crm.client.ui.crud.customer.tenant.FutureTenantListerViewImpl;

public class MarketingViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (ServiceViewerView.class.equals(type)) {
                map.put(type, new ServiceViewerViewImpl());
            } else if (ServiceEditorView.class.equals(type)) {
                map.put(type, new ServiceEditorViewImpl());
            } else if (FeatureViewerView.class.equals(type)) {
                map.put(type, new FeatureViewerViewImpl());
            } else if (FeatureEditorView.class.equals(type)) {
                map.put(type, new FeatureEditorViewImpl());
            } else if (ConcessionViewerView.class.equals(type)) {
                map.put(type, new ConcessionViewerViewImpl());
            } else if (ConcessionEditorView.class.equals(type)) {
                map.put(type, new ConcessionEditorViewImpl());

            } else if (LeadListerView.class.equals(type)) {
                map.put(type, new LeadListerViewImpl());
            } else if (LeadViewerView.class.equals(type)) {
                map.put(type, new LeadViewerViewImpl());
            } else if (LeadEditorView.class.equals(type)) {
                map.put(type, new LeadEditorViewImpl());

            } else if (AppointmentListerView.class.equals(type)) {
                map.put(type, new AppointmentListerViewImpl());
            } else if (AppointmentViewerView.class.equals(type)) {
                map.put(type, new AppointmentViewerViewImpl());
            } else if (AppointmentEditorView.class.equals(type)) {
                map.put(type, new AppointmentEditorViewImpl());

            } else if (ShowingListerView.class.equals(type)) {
                map.put(type, new ShowingListerViewImpl());
            } else if (ShowingViewerView.class.equals(type)) {
                map.put(type, new ShowingViewerViewImpl());
            } else if (ShowingEditorView.class.equals(type)) {
                map.put(type, new ShowingEditorViewImpl());

            } else if (FutureTenantListerView.class.equals(type)) {
                map.put(type, new FutureTenantListerViewImpl());
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
