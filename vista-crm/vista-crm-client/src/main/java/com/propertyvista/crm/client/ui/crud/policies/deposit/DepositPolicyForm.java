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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;

public class DepositPolicyForm extends PolicyDTOTabPanelBasedForm<DepositPolicyDTO> {

    private final static I18n i18n = I18n.get(DepositPolicyForm.class);

    public DepositPolicyForm(IForm<DepositPolicyDTO> view) {
        super(DepositPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Details"));
        int row = -1;

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().annualInterestRate())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().securityDepositRefundWindow())).build());

        return panel;
    }
}
