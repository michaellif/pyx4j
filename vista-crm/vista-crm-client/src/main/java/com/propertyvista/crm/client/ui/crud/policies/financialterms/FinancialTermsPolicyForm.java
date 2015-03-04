/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2015
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.policies.financialterms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.FinancialTermsPolicyDTO;

public class FinancialTermsPolicyForm extends PolicyDTOTabPanelBasedForm<FinancialTermsPolicyDTO> {

    public FinancialTermsPolicyForm(IPrimeFormView<FinancialTermsPolicyDTO, ?> view) {
        super(FinancialTermsPolicyDTO.class, view);

        addTab(createTenantBillingTermsPanel(), proto().tenantBillingTerms().getMeta().getCaption());
        addTab(createTenantPreauthorizedPaymentECheckTerms(), proto().tenantPreauthorizedPaymentECheckTerms().getMeta().getCaption());
        addTab(createTenantPreauthorizedPaymentCardTerms(), proto().tenantPreauthorizedPaymentCardTerms().getMeta().getCaption());
    }

    private IsWidget createTenantBillingTermsPanel() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().tenantBillingTerms(), new FinancialTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

    private IsWidget createTenantPreauthorizedPaymentECheckTerms() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().tenantPreauthorizedPaymentECheckTerms(), new FinancialTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

    private IsWidget createTenantPreauthorizedPaymentCardTerms() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().tenantPreauthorizedPaymentCardTerms(), new FinancialTermsPolicyItemForm(isEditable()));
        return formPanel;
    }
}
