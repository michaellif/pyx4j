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

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LeaseTerminationPolicyDTO;

public class LeaseTerminationPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseTerminationPolicyDTO> {

    private static final I18n i18n = I18n.get(LeaseTerminationPolicyForm.class);

    public LeaseTerminationPolicyForm(IForm<LeaseTerminationPolicyDTO> view) {
        super(LeaseTerminationPolicyDTO.class, view);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Left, proto().periodOfNotice()).decorate().componentWidth(80);
        addTab(formPanel, i18n.tr("General"));
    }

}
