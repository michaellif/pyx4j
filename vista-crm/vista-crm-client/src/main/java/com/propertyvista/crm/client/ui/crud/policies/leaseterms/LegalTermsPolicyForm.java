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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LegalTermsPolicyDTO;

public class LegalTermsPolicyForm extends PolicyDTOTabPanelBasedForm<LegalTermsPolicyDTO> {

    public LegalTermsPolicyForm(IForm<LegalTermsPolicyDTO> view) {
        super(LegalTermsPolicyDTO.class, view);

        addTab(createResidentPortalTermsAndConditionsPanel(), proto().residentPortalTermsAndConditions().getMeta().getCaption());
        addTab(createResidentPortalPrivacyPolicyPanel(), proto().residentPortalPrivacyPolicy().getMeta().getCaption());
        addTab(createProspectPortalTermsAndConditionsPanel(), proto().prospectPortalTermsAndConditions().getMeta().getCaption());
        addTab(createProspectPortalPrivacyPolicyPanel(), proto().prospectPortalPrivacyPolicy().getMeta().getCaption());
    }

    private IsWidget createResidentPortalTermsAndConditionsPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().residentPortalTermsAndConditions(), new LegalTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

    private IsWidget createResidentPortalPrivacyPolicyPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().residentPortalPrivacyPolicy(), new LegalTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

    private IsWidget createProspectPortalTermsAndConditionsPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().prospectPortalTermsAndConditions(), new LegalTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

    private IsWidget createProspectPortalPrivacyPolicyPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().prospectPortalPrivacyPolicy(), new LegalTermsPolicyItemForm(isEditable()));
        return formPanel;
    }

}
