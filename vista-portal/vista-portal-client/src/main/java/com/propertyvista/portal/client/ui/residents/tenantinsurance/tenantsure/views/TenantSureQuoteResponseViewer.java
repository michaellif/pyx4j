/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureMonthlyPaymentViewer;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuoteViewer;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteResponseDTO;

public class TenantSureQuoteResponseViewer extends CEntityViewer<TenantSureQuoteResponseDTO> {
    private static final I18n i18n = I18n.get(TenantSureQuoteResponseViewer.class);

    @Override
    public IsWidget createContent(TenantSureQuoteResponseDTO value) {
        FormFlexPanel panel = new FormFlexPanel();
        if (value != null) {
            int row = -1;
            panel.setH2(++row, 0, 1, i18n.tr("Annual Payment"));
            panel.setWidget(++row, 0, new TenantSureQuoteViewer(true).createContent(value.quote()));

            panel.setH2(++row, 0, 1, i18n.tr("Monthly Payment"));
            panel.setWidget(++row, 0, new TenantSureMonthlyPaymentViewer().createContent(value.monthlyPayment()));
        }
        return panel;
    }

}
