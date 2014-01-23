/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.restrictions;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.RestrictionsPolicyDTO;

public class RestrictionsPolicyForm extends PolicyDTOTabPanelBasedForm<RestrictionsPolicyDTO> {

    private static final I18n i18n = I18n.get(RestrictionsPolicyForm.class);

    public RestrictionsPolicyForm(IForm<RestrictionsPolicyDTO> view) {
        super(RestrictionsPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createMiscPoliciesTab());
    }

    private TwoColumnFlexFormPanel createMiscPoliciesTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Restrictions"));
        int row = -1;
        String lbw = "220px";
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxParkingSpots()), 3, true).labelWidth(lbw).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxLockers()), 3, true).labelWidth(lbw).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxPets()), 3, true).labelWidth(lbw).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().occupantsPerBedRoom()), 3, true).labelWidth(lbw).build());

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().ageOfMajority()), 3, true).labelWidth(lbw).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().enforceAgeOfMajority()), 3, true).labelWidth(lbw).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maturedOccupantsAreApplicants()), 3, true).labelWidth(lbw).build());

        return content;
    }
}
