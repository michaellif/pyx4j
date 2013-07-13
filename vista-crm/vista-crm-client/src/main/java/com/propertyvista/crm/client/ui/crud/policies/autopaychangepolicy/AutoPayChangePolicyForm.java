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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.autopaychangepolicy;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.AutoPayChangePolicyDTO;

public class AutoPayChangePolicyForm extends PolicyDTOTabPanelBasedForm<AutoPayChangePolicyDTO> {

    private static final I18n i18n = I18n.get(AutoPayChangePolicyForm.class);

    public AutoPayChangePolicyForm(IForm<AutoPayChangePolicyDTO> view) {
        super(AutoPayChangePolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createPolicyEditorPanel());
    }

    private TwoColumnFlexFormPanel createPolicyEditorPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel("Settings");
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().rule())).build());
        return panel;
    }

}
