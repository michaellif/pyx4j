/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leasebilling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;

public class LeaseBillingPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<LeaseBillingPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseBillingPolicyEditorForm.class);

    public LeaseBillingPolicyEditorForm() {
        this(false);
    }

    public LeaseBillingPolicyEditorForm(boolean viewMode) {
        super(LeaseBillingPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createBillingPanel(), i18n.tr("Billing")),
                new TabDescriptor(createLateFeesPanel(), i18n.tr("Late Fee"))
        );//@formatter:on
    }

    private Widget createBillingPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().prorationMethod())).build());
        CCheckBox checkBox = new CCheckBox();
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().billingPeriodStartDay()).setEnabled(event.getValue());

            }
        });

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().useBillingPeriodSartDay(), checkBox)).build());

        ArrayList<Integer> options = new ArrayList<Integer>();
        for (int i = 1; i < 29; i++) {
            options.add(i);
        }

        CComboBox<Integer> comboBox = new CComboBox<Integer>();
        comboBox.setOptions(options);
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingPeriodStartDay(), comboBox), 20).build());

        return panel;
    }

    private Widget createLateFeesPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lateFee().baseFeeType())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lateFee().baseFee())).build());

        row = -1;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lateFee().maxTotalFeeType())).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lateFee().maxTotalFee())).build());

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");

        return panel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().billingPeriodStartDay()).setEnabled(getValue().useBillingPeriodSartDay().getValue());
    }
}
