/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

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

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ApartmentView.class).to(ApartmentViewImpl.class).in(Singleton.class);
        bind(TenantsView.class).to(TenantsViewImpl.class).in(Singleton.class);
        bind(InfoView.class).to(InfoViewImpl.class).in(Singleton.class);
        bind(FinancialView.class).to(FinancialViewImpl.class).in(Singleton.class);
        bind(PetsView.class).to(PetsViewImpl.class).in(Singleton.class);
        bind(PaymentView.class).to(PaymentViewImpl.class).in(Singleton.class);
        bind(ChargesView.class).to(ChargesViewImpl.class).in(Singleton.class);
        bind(SummaryView.class).to(SummaryViewImpl.class).in(Singleton.class);
        bind(CompletionView.class).to(CompletionViewImpl.class).in(Singleton.class);

        bind(GenericMessageView.class).to(GenericMessageViewImpl.class).in(Singleton.class);
    }

}
