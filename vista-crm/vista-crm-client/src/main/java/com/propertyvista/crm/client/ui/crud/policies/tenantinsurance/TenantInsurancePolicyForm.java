/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.tenantinsurance;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.TenantInsurancePolicyDTO;

public class TenantInsurancePolicyForm extends PolicyDTOTabPanelBasedForm<TenantInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantInsurancePolicyForm.class);

    public TenantInsurancePolicyForm(boolean viewMode) {
        super(TenantInsurancePolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                createInsuranceRequirementsTab(),
                createPortalConfigurationTab()                
        );//@formatter:on
    }

    private FormFlexPanel createInsuranceRequirementsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Insurance Requirements"));
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requireMinimumLiability()), 5).build());
        get(proto().requireMinimumLiability()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimumRequiredLiability()).setVisible(event.getValue());
            }
        });
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().minimumRequiredLiability()), 20).build());
        return panel;
    }

    private FormFlexPanel createPortalConfigurationTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Portal Configuration"));
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().noInsuranceStatusMessage()), 50).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenantInsuranceInvitation()), 50).build());
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().minimumRequiredLiability()).setVisible(getValue().requireMinimumLiability().isBooleanTrue());
    }
}
