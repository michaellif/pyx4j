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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.NodeType;
import com.propertyvista.domain.policy.PolicyPreset;
import com.propertyvista.domain.policy.dto.EffectivePolicyPresetDTO;

public class PolicyManagementViewImpl extends DockLayoutPanel implements PolicyManagementView {
    private static final I18n i18n = I18n.get(PolicyManagementViewImpl.class);

    private static final double BROWSER_WIDTH = 20.0;

    FormFlexPanel panel;

    CEntityEditor<EffectivePolicyPresetDTO> policiesForm;

    CEntityEditor<PolicyPreset> presetForm;

    private Presenter presenter;

    public PolicyManagementViewImpl() {
        super(Unit.EM);

        addEast(new OrganizationBrowser() {
            @Override
            public void onNodeSelected(Key nodeKey, NodeType nodeType) {
                getPresenter().populateEffectivePolicyPreset(nodeKey, nodeType);
            }
        }, BROWSER_WIDTH);

        policiesForm = new CEntityDecoratableEditor<EffectivePolicyPresetDTO>(EffectivePolicyPresetDTO.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                content.setSize("100%", "100%");
                int row = -1;
                content.setWidget(++row, 0, inject(proto().effectivePolicies(), new PolicyFolder()));
                content.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
                return content;
            }

        };

        policiesForm.setEditable(false);
        policiesForm.initContent();
        add(policiesForm);

        setSize("100%", "100%");
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
