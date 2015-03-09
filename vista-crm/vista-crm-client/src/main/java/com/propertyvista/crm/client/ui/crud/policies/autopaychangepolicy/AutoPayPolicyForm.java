/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.autopaychangepolicy;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.AutoPayPolicyDTO;

public class AutoPayPolicyForm extends PolicyDTOTabPanelBasedForm<AutoPayPolicyDTO> {

    private static final I18n i18n = I18n.get(AutoPayPolicyForm.class);

    public AutoPayPolicyForm(IPrimeFormView<AutoPayPolicyDTO, ?> view) {
        super(AutoPayPolicyDTO.class, view);
        addTab(createPolicyEditorPanel(), i18n.tr("Settings"));
    }

    private IsWidget createPolicyEditorPanel() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().onLeaseChargeChangeRule()).decorate().componentWidth(200).labelWidth(250);

        formPanel.append(Location.Left, proto().excludeFirstBillingPeriodCharge()).decorate().componentWidth(50).labelWidth(250);
        formPanel.append(Location.Left, proto().excludeLastBillingPeriodCharge()).decorate().componentWidth(50).labelWidth(250);
        formPanel.append(Location.Left, proto().allowCancelationByResident()).decorate().componentWidth(50).labelWidth(250);

        return formPanel;
    }
}
