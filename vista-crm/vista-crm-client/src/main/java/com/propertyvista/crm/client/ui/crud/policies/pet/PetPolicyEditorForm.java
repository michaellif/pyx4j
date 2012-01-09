/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.pet;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;
import com.propertyvista.domain.policy.policies.specials.PetConstraints;

public class PetPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<PetPolicyDTO> {

    private static final I18n i18n = I18n.get(PetPolicyEditorForm.class);

    public PetPolicyEditorForm(IEditableComponentFactory factory) {
        super(PetPolicyDTO.class, factory);
    }

    @Override
    protected List<PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(new TabDescriptor(createSettignsForm(), i18n.tr("Pet Limits")));
    }

    private Widget createSettignsForm() {
        FormFlexPanel content = new FormFlexPanel();
        content.setWidget(0, 0, inject(proto().constraints(), new PetConstraintsFolder(isEditable())));
        return content;
    }

    @Override
    protected void onPopulate() {
        // FIXME Remove this function 
        super.onPopulate();

        PetPolicyDTO policy = getValue();
        if (!policy.constraints().isEmpty()) {
            PetConstraints constraints = policy.constraints().get(0);
        }
    }
}
