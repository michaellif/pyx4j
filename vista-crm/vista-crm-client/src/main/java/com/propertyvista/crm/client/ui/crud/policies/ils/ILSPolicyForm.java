/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.ils;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ILSPolicyDTO;

public class ILSPolicyForm extends PolicyDTOTabPanelBasedForm<ILSPolicyDTO> {

    private final static I18n i18n = I18n.get(ILSPolicyForm.class);

    public ILSPolicyForm(IForm<ILSPolicyDTO> view) {
        super(ILSPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createProvidersPanel());
    }

    private TwoColumnFlexFormPanel createProvidersPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("ILS Providers"));
        int row = -1;

//        panel.setWidget(++row, 0, 2, inject(proto().policyItems(), new ILSPolicyItemEditorFolder()));

        return panel;
    }

}
