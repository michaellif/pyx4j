/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LegalTermsPolicyDTO;

public class LegalTermsPolicyForm extends PolicyDTOTabPanelBasedForm<LegalTermsPolicyDTO> {

    public LegalTermsPolicyForm(IForm<LegalTermsPolicyDTO> view) {
        super(LegalTermsPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(//
                createRentalCriteriaGuidelinesPanel() // 
                );
    }

    private TwoColumnFlexFormPanel createRentalCriteriaGuidelinesPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().rentalCriteriaGuidelines().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().rentalCriteriaGuidelines(), new LegalTermsPolicyItemForm(isEditable())));
        return container;
    }

}
