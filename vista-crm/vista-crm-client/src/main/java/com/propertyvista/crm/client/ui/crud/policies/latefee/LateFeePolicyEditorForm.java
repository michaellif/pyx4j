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
package com.propertyvista.crm.client.ui.crud.policies.latefee;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.LateFeePolicyDTO;

public class LateFeePolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<LateFeePolicyDTO> {

    private final static I18n i18n = I18n.get(LateFeePolicyEditorForm.class);

    public LateFeePolicyEditorForm() {
        this(false);
    }

    public LateFeePolicyEditorForm(boolean viewMode) {
        super(LateFeePolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createFeesPanel(), i18n.tr("Fees"))
        );//@formatter:on
    }

    private Widget createFeesPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().baseFee())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().baseFee2())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().baseFeeType())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().baseFeeType2())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().gracePeriod())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().gracePeriod2())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maxTotalFee())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().maxTotalFeeType())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dailyFee())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().maxDays())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().minimumAmounDue())).build());
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeNoticeResident())).build());
        panel.setWidget(row, 1, new DecoratorBuilder(inject(proto().chargePastResident())).build());

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lateFeeStatement())).build());
        panel.getFlexCellFormatter().setColSpan(row, 0, 2);

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");

        return panel;
    }
}
