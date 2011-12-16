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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.policy.Policy;

public class PolicyManagementViewImpl implements PolicyManagementView {
    private final CEntityEditor<Policy> form;

    public PolicyManagementViewImpl() {
        form = new CEntityEditor<Policy>(Policy.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel panel = new FormFlexPanel();
                panel.setSize("100%", "100%");

                //panel.setWidget(0, 0, inject(proto().foo()));

                return panel;
            }
        };
        form.initContent();
    }

    @Override
    public Widget asWidget() {
        return form.asWidget();
    }

    @Override
    public void populate(Policy policy) {
        form.populate(policy);
    }
}
