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

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyForm extends PolicyDTOTabPanelBasedForm<BackgroundCheckPolicyDTO> {

    private final static I18n i18n = I18n.get(BackgroundCheckPolicyForm.class);

    public BackgroundCheckPolicyForm(IForm<BackgroundCheckPolicyDTO> view) {
        super(BackgroundCheckPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                createItemsPanel()
        );//@formatter:on
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Policy"));
        int row = -1;

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().bankruptcy()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().judgment()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().collection()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().chargeOff()), 5).build());

        panel.setH3(++row, 0, 1, i18n.tr("Help"));
        panel.setWidget(++row, 0, new HTML(CrmResources.INSTANCE.backgroundCheckHelp().getText()));

        return panel;
    }
}
