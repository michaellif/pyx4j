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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;

public class PetPolicyForm extends PolicyDTOTabPanelBasedForm<PetPolicyDTO> {

    private static final I18n i18n = I18n.get(PetPolicyForm.class);

    public PetPolicyForm(IForm<PetPolicyDTO> view) {
        super(PetPolicyDTO.class, view);
        addTab(createSettignsForm(), i18n.tr("Pet Limits"));

    }

    private IsWidget createSettignsForm() {
        DualColumnForm formPanel = new DualColumnForm(this);
        formPanel.append(Location.Left, proto().constraints(), new PetConstraintsFolder());
        return formPanel;
    }

}
