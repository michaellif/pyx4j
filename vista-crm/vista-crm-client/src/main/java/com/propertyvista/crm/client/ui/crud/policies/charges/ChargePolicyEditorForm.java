/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.charges;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.ChargePolicyDTO;

public class ChargePolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<ChargePolicyDTO> {
    private final static I18n i18n = I18n.get(ChargePolicyEditorForm.class);

    public ChargePolicyEditorForm() {
        this(false);
    }

    public ChargePolicyEditorForm(boolean viewMode) {
        super(ChargePolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createChargesPanel(), i18n.tr("Items"))
        );
    }

    private Widget createChargesPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        return panel;
    }
}
