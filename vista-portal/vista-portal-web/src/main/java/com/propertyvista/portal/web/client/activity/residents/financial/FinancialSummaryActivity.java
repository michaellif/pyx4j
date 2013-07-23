/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.residents.financial;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.residents.financial.yardi.FinancialSummaryView;

/**
 * Used to display financial status of Tenants from Yardi integrated accounts.
 */
public class FinancialSummaryActivity extends AbstractActivity implements FinancialSummaryView.Presenter {

    private final FinancialSummaryView view;

    private final BillSummaryService service;

    public FinancialSummaryActivity(Place place) {
        view = PortalWebSite.getViewFactory().instantiate(FinancialSummaryView.class);
        service = GWT.<BillSummaryService> create(BillSummaryService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        service.retrieve(new DefaultAsyncCallback<FinancialSummaryDTO>() {
            @Override
            public void onSuccess(FinancialSummaryDTO result) {
                view.setPresenter(FinancialSummaryActivity.this);
                view.setEnablePayments(SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values()));
                // TODO need to merge FinancialSummaryView someday
                view.populate(result.<YardiFinancialSummaryDTO> cast());
                panel.setWidget(view);
            }
        });
    }

    @Override
    public void payNow() {
        if (SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values())) {
            AppSite.getPlaceController().goTo(new Financial.PayNow());
        }
    }

    @Override
    public void edit(Key id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(YardiFinancialSummaryDTO value) {
        // TODO Auto-generated method stub

    }
}
