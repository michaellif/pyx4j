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
package com.propertyvista.crm.client.ui.crud.policies.misc;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.MiscPolicyDTO;

public class MiscPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<MiscPolicyDTO> {

    private static final I18n i18n = I18n.get(MiscPolicyEditorForm.class);

    public MiscPolicyEditorForm(IEditableComponentFactory factory) {
        super(MiscPolicyDTO.class, factory);
    }

    @Override
    protected List<PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(new TabDescriptor(createMiscPoliciesTab(), i18n.tr("Misc Settings")));
    }

    private Widget createMiscPoliciesTab() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maxParkingSpots())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maxPets())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().occupantsPerBedRoom())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().occupantsOver18areApplicants())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().oneMonthDeposit())).labelWidth(20).componentWidth(3).build());
        return content;
    }
}
