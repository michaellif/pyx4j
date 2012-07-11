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
package com.propertyvista.crm.client.ui.crud.policies.backgroundcheck;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyForm extends PolicyDTOTabPanelBasedForm<BackgroundCheckPolicyDTO> {

    private final static I18n i18n = I18n.get(BackgroundCheckPolicyForm.class);

    public BackgroundCheckPolicyForm() {
        this(false);
    }

    public BackgroundCheckPolicyForm(boolean viewMode) {
        super(BackgroundCheckPolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                createItemsPanel()
        );//@formatter:on
    }

    private FormFlexPanel createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Policy"));
        int row = -1;

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankruptcy()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().judgment()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().collection()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeOff()), 5).build());

        return panel;
    }
}
