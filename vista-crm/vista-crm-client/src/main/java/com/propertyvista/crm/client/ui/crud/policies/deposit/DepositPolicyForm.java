/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.deposit;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;

public class DepositPolicyForm extends PolicyDTOTabPanelBasedForm<DepositPolicyDTO> {

    private final static I18n i18n = I18n.get(DepositPolicyForm.class);

    public DepositPolicyForm(IFormView<DepositPolicyDTO> view) {
        super(DepositPolicyDTO.class, view);
        addTab(createItemsPanel(), i18n.tr("Details"));
    }

    private IsWidget createItemsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().annualInterestRate()).decorate().labelWidth(200).componentWidth(50);
        formPanel.append(Location.Left, proto().securityDepositRefundWindow()).decorate().labelWidth(200).componentWidth(50);

        return formPanel;
    }
}
