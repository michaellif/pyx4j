/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteSummaryDTO;

public class TenantSureQuoteForm extends CEntityDecoratableForm<TenantSureQuoteSummaryDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuoteForm.class);

    public TenantSureQuoteForm() {
        super(TenantSureQuoteSummaryDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyInsurancePremium())).build());

        return content;
    }

}
