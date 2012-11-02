/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leasetermination;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LeaseTerminationPolicyDTO;

public class LeaseTerminationPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseTerminationPolicyDTO> {

    private static final I18n i18n = I18n.get(LeaseTerminationPolicyForm.class);

    public LeaseTerminationPolicyForm(boolean viewMode) {
        super(LeaseTerminationPolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        FormFlexPanel general = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        general.setWidget(++row, 0, new DecoratorBuilder(inject(proto().periodOfNotice()), 5).build());

        return Arrays.asList(general);
    }
}
