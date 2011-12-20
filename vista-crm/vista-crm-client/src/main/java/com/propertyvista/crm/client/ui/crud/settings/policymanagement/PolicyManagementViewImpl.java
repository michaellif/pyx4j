/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.policymanagement;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.EffectivePolicyPresetDTO;
import com.propertyvista.domain.policy.PolicyPreset;
import com.propertyvista.domain.policy.PolicyPresetAtNode.NodeType;

public class PolicyManagementViewImpl implements PolicyManagementView {
    private static final I18n i18n = I18n.get(PolicyManagementViewImpl.class);

    FormFlexPanel panel;

    CEntityEditor<EffectivePolicyPresetDTO> policiesForm;

    CEntityEditor<PolicyPreset> presetForm;

    private Presenter presenter;

    public PolicyManagementViewImpl() {
        int row = -1;

        panel = new FormFlexPanel();
        panel.setSize("100%", "100%");

        panel.setWidget(0, 0, new OrganizationBrowser() {
            @Override
            public void onNodeSelected(Key nodeKey, NodeType nodeType) {
                getPresenter().populateEffectivePolicyPreset(nodeKey, nodeType);
            }
        });
        panel.getFlexCellFormatter().setWidth(0, 0, "10em");
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

        policiesForm = new CEntityDecoratableEditor<EffectivePolicyPresetDTO>(EffectivePolicyPresetDTO.class) {
//            private final Label inheritedLabel = new Label(i18n.tr("Inherited policies"));

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                content.setSize("100%", "100%");
                int row = -1;
                content.setWidget(++row, 0, new CButton(i18n.tr("Edit"), new Command() {
                    @Override
                    public void execute() {
                    }
                }));
                content.setWidget(++row, 0,
                        new DecoratorBuilder(inject(proto().assignedPolicyPreset().policyPreset().name())).customLabel(i18n.tr("Assigned Preset")).build());
//                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().assignedPolicyPreset().policyPreset().description(), new CTextArea())).build());

                content.setWidget(++row, 0, inject(proto().effectivePolicies(), new PolicyFolder()));
                return content;
            }

            @Override
            protected void onPopulate() {
                super.onPopulate();
//                EffectivePolicyPresetDTO value = getValue();
//                if (value.isNull() || value.assignedPolicyPreset().isNull()) {
//                    get(proto().assignedPolicyPreset().policyPreset().name()).setVisible(false);
//                    inheritedLabel.setVisible(true);
//                    //  get(proto().assignedPolicyPreset().policyPreset().description()).setVisible(false);
//                } else {
//                    get(proto().assignedPolicyPreset().policyPreset().name()).setVisible(true);
//                    inheritedLabel.setVisible(false);
//                    //  get(proto().assignedPolicyPreset().policyPreset().description()).setVisible(true);
//                }
            }
        };

        policiesForm.setEditable(false);
        policiesForm.initContent();
        panel.setWidget(0, 1, policiesForm);
        panel.getFlexCellFormatter().setWidth(0, 1, "40em");
        panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void displayEffectivePreset(EffectivePolicyPresetDTO effectivePolicyPreset) {
        this.policiesForm.populate(effectivePolicyPreset);
    }
}
