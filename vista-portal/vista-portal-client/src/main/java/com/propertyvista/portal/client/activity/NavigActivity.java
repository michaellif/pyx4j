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
package com.propertyvista.portal.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.NavigView;
import com.propertyvista.portal.client.ui.residents.payment.PortalPaymentTypesUtil;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.shared.config.VistaFeatures;

public class NavigActivity extends AbstractActivity implements NavigView.NavigPresenter {

    private final NavigView view;

    public NavigActivity(Place place) {
        this.view = PortalSite.getViewFactory().instantiate(NavigView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public NavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setNavig(createNavigationItems());
        panel.setWidget(view);
    }

    @Override
    public void navigTo(AppPlace place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public AppPlace getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    private List<AppPlace> createNavigationItems() {
        List<AppPlace> items = new ArrayList<AppPlace>();

        items.add(new Resident());

        if (!VistaFeatures.instance().yardiIntegration()) {
            items.add(new Resident.Financial.BillSummary());
            items.add(new Resident.Financial.BillingHistory());
        } else {
            items.add(new Resident.Financial.FinancialSummary());
        }
        items.add(new Resident.Maintenance());
        if (VistaTODO.ENABLE_COMMUNCATION_CENTER) {
            items.add(new Resident.CommunicationCenter());
        }
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.CreditCardPaymentsAllowed,
                VistaCustomerPaymentTypeBehavior.EcheckPaymentsAllowed)) {

            if (PortalPaymentTypesUtil.isPreauthorizedPaumentAllowed()) {
                items.add(new Resident.Financial.PreauthorizedPayments());
            }

            items.add(new Financial.PaymentMethods());
        }
        items.add(new Resident.ProfileViewer());
        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            items.add(new Resident.ResidentServices.TenantInsurance());
        }
        if (SecurityController.checkBehavior(VistaCustomerBehavior.HasMultipleLeases)) {
            items.add(new PortalSiteMap.LeaseContextSelection());
        }

        return items;
    }
}
