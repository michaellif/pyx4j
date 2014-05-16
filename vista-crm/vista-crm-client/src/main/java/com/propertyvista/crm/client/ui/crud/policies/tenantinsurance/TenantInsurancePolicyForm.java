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

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.TenantInsurancePolicyDTO;

public class TenantInsurancePolicyForm extends PolicyDTOTabPanelBasedForm<TenantInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantInsurancePolicyForm.class);

    public TenantInsurancePolicyForm(IForm<TenantInsurancePolicyDTO> view) {
        super(TenantInsurancePolicyDTO.class, view);
        addTab(createInsuranceRequirementsTab(), i18n.tr("Insurance Requirements"));
    }

    private IsWidget createInsuranceRequirementsTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().requireMinimumLiability()).decorate().componentWidth(60);
        get(proto().requireMinimumLiability()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimumRequiredLiability()).setVisible(event.getValue());
            }
        });
        formPanel.append(Location.Left, proto().minimumRequiredLiability()).decorate().componentWidth(200);
        get(proto().minimumRequiredLiability()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
            @Override
            public BasicValidationError isValid() {
                // HARD CODED by request form Leonard and due to TenantSure max possible liability which is $5,000,000
                // if we do it like that, we don't have to worry about what to do with TenantSure in portal if the policy sets the min liability is over 5 million
                final BigDecimal MAX_FLOOR = new BigDecimal("5000000.00");
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(MAX_FLOOR) > 0) {
                    return new BasicValidationError(getComponent(), i18n.tr("The maximum of minimum required liability is ${0,number,#,##0.00}", MAX_FLOOR));
                }
                if (getComponent().getValue() != null && getComponent().getValue().compareTo(BigDecimal.ZERO) < 0) {
                    return new BasicValidationError(getComponent(), i18n.tr("Please provide a non-negative value"));
                } else {
                    return null;
                }
            }
        });
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().minimumRequiredLiability()).setVisible(getValue().requireMinimumLiability().getValue(false));
    }
}
