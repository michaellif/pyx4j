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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ARPolicyDTO;

public class ARPolicyForm extends PolicyDTOTabPanelBasedForm<ARPolicyDTO> {

    private static final I18n i18n = I18n.get(ARPolicyForm.class);

    public ARPolicyForm() {
        this(false);
    }

    public ARPolicyForm(boolean viewMode) {
        super(ARPolicyDTO.class, viewMode);
    }

    @Override
    protected List<PolicyDTOTabPanelBasedForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(new TabDescriptor(createARPoliciesTab(), i18n.tr("AR Settings")));
    }

    private Widget createARPoliciesTab() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditDebitRule())).labelWidth(20).componentWidth(20).build());

        return content;
    }
}
