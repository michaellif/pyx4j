/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.ar;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ARPolicyDTO;

public class ARPolicyForm extends PolicyDTOTabPanelBasedForm<ARPolicyDTO> {

    private static final I18n i18n = I18n.get(ARPolicyForm.class);

    public ARPolicyForm(IForm<ARPolicyDTO> view) {
        super(ARPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createARPoliciesTab());
    }

    private TwoColumnFlexFormPanel createARPoliciesTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("AR Settings"));
        int row = -1;
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().creditDebitRule()), true).build());

        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, 2, new HTML(CrmResources.INSTANCE.arPolicyRuleDescription().getText()));

        return content;
    }
}
