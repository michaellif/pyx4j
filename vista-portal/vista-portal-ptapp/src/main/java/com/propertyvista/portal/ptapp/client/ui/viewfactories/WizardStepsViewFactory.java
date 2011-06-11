/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.portal.ptapp.client.ui.steps.ApartmentView;
import com.propertyvista.portal.ptapp.client.ui.steps.ApartmentViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.ChargesView;
import com.propertyvista.portal.ptapp.client.ui.steps.ChargesViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.CompletionView;
import com.propertyvista.portal.ptapp.client.ui.steps.CompletionViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.FinancialView;
import com.propertyvista.portal.ptapp.client.ui.steps.FinancialViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.InfoView;
import com.propertyvista.portal.ptapp.client.ui.steps.InfoViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.PaymentView;
import com.propertyvista.portal.ptapp.client.ui.steps.PaymentViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.PetsView;
import com.propertyvista.portal.ptapp.client.ui.steps.PetsViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.SummaryView;
import com.propertyvista.portal.ptapp.client.ui.steps.SummaryViewImpl;
import com.propertyvista.portal.ptapp.client.ui.steps.TenantsView;
import com.propertyvista.portal.ptapp.client.ui.steps.TenantsViewImpl;

public class WizardStepsViewFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (ApartmentView.class.equals(type)) {
                map.put(type, new ApartmentViewImpl());
            } else if (TenantsView.class.equals(type)) {
                map.put(type, new TenantsViewImpl());
            } else if (InfoView.class.equals(type)) {
                map.put(type, new InfoViewImpl());
            } else if (FinancialView.class.equals(type)) {
                map.put(type, new FinancialViewImpl());
            } else if (PetsView.class.equals(type)) {
                map.put(type, new PetsViewImpl());
            } else if (ChargesView.class.equals(type)) {
                map.put(type, new ChargesViewImpl());
            } else if (SummaryView.class.equals(type)) {
                map.put(type, new SummaryViewImpl());
            } else if (PaymentView.class.equals(type)) {
                map.put(type, new PaymentViewImpl());
            } else if (CompletionView.class.equals(type)) {
                map.put(type, new CompletionViewImpl());
            }
        }
        return map.get(type);
    }
}
