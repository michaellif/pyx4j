/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuoteDTO;

public class TenantSurePurchaseActivity extends AbstractActivity implements TenantSurePurchaseView.Presenter {

    private final TenantSurePurchaseView view;

    private final TenantSurePurchaseService service;

    public TenantSurePurchaseActivity() {
        view = PortalViewFactory.instance(TenantSurePurchaseView.class);
        service = GWT.<TenantSurePurchaseService> create(TenantSurePurchaseService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        service.getQuotationRequestParams(new DefaultAsyncCallback<TenantSureQuotationRequestParamsDTO>() {
            @Override
            public void onSuccess(TenantSureQuotationRequestParamsDTO quotationRequestParams) {
                view.init(quotationRequestParams);
                panel.setWidget(view);
            }
        });

    }

    @Override
    public void requestQuotation(TenantSureQuotationRequestDTO dto) {
        // TODO Auto-generated method stub
    }

    @Override
    public void acceptQuote(TenantSureQuoteDTO quote, CreditCardInfo creditCardInfo) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

}
