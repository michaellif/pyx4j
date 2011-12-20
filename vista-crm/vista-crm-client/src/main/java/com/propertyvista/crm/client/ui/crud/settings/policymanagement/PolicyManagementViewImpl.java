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

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.policy.EffectivePolicyPresetDTO;
import com.propertyvista.domain.policy.PolicyPresetAtNode.NodeType;

public class PolicyManagementViewImpl implements PolicyManagementView {
    FormFlexPanel panel;

    PolicyFolder policyFolder;

    private Presenter presenter;

    public PolicyManagementViewImpl() {
        panel = new FormFlexPanel();
        panel.setSize("100%", "100%");

        panel.setWidget(0, 0, new OrganizationBrowser() {
            @Override
            public void onNodeSelected(Key nodeKey, NodeType nodeType) {
                getPresenter().populateEffectivePolicyPreset(nodeKey, nodeType);
            }
        });
        panel.getFlexCellFormatter().setWidth(0, 0, "50%");
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

        panel.setWidget(0, 1, (policyFolder = new PolicyFolder()));
        panel.getFlexCellFormatter().setWidth(0, 1, "50%");

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
        this.policyFolder.populate(effectivePolicyPreset.effectivePolicies());
    }
}
