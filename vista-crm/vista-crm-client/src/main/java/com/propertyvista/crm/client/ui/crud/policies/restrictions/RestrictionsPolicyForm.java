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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

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
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().maxParkingSpots())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().maxLockers())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().maxPets())).labelWidth(20).componentWidth(3).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().occupantsPerBedRoom())).labelWidth(20).componentWidth(3).build());

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().occupantsOver18areApplicants())).labelWidth(20).componentWidth(2).build());

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().enforceAgeOfMajority())).labelWidth(20).componentWidth(2).build());
        get(proto().enforceAgeOfMajority()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().ageOfMajority()).setVisible(event.getValue() == true);
            }
        });
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().ageOfMajority())).labelWidth(20).componentWidth(2).build());

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().ageOfMajority()).setVisible(getValue().enforceAgeOfMajority().isBooleanTrue());
    }

}
