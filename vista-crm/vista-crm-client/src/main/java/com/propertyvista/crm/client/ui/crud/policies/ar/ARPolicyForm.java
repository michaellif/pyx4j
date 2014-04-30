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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ARPolicyDTO;

public class ARPolicyForm extends PolicyDTOTabPanelBasedForm<ARPolicyDTO> {

    private static final I18n i18n = I18n.get(ARPolicyForm.class);

    public ARPolicyForm(IForm<ARPolicyDTO> view) {
        super(ARPolicyDTO.class, view);
        addTab(createARPoliciesTab(), i18n.tr("AR Settings"));
    }

    private IsWidget createARPoliciesTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().creditDebitRule()).decorate().componentWidth(200);

        formPanel.br();
        formPanel.append(Location.Left, new HTML(CrmResources.INSTANCE.arPolicyRuleDescription().getText()));

        return formPanel;
    }
}
