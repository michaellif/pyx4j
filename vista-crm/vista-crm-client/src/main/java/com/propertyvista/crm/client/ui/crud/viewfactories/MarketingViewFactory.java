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
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryListerView;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryListerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryViewerView;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentListerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentListerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentViewerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.AppointmentViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadListerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadListerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadViewerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.LeadViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingListerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingListerViewImpl;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingViewerView;
import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingViewerViewImpl;

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

            } else if (InquiryListerView.class.equals(type)) {
                map.put(type, new InquiryListerViewImpl());
            } else if (InquiryViewerView.class.equals(type)) {
                map.put(type, new InquiryViewerViewImpl());
            } else if (InquiryEditorView.class.equals(type)) {
                map.put(type, new InquiryEditorViewImpl());

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
