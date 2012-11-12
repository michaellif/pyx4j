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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuotationRequestForm;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuoteDTO;

public class TenantSurePurchaseViewImpl extends Composite implements TenantSurePurchaseView {

    public FlowPanel panel;

    private final TenantSureQuotationRequestForm quotationRequestForm;

    public TenantSurePurchaseViewImpl() {
        panel = new FlowPanel();
        quotationRequestForm = new TenantSureQuotationRequestForm();
        quotationRequestForm.initContent();
        panel.add(quotationRequestForm);

        initWidget(panel);
    }

    @Override
    public void init(TenantSureQuotationRequestParamsDTO quotationRequestParams) {
        quotationRequestForm.setRequestParams(quotationRequestParams);
    }

    @Override
    public void populateQuote(TenantSureQuoteDTO quote) {
        // TODO Auto-generated method stub
    }

}
