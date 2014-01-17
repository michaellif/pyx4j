/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.prospectportal;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ProspectPortalPolicyDTO;

public class ProspectPortalPolicyForm extends PolicyDTOTabPanelBasedForm<ProspectPortalPolicyDTO> {

    private final static I18n i18n = I18n.get(ProspectPortalPolicyForm.class);

    public ProspectPortalPolicyForm(IForm<ProspectPortalPolicyDTO> view) {
        super(ProspectPortalPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createDetailsTab());
    }

    private TwoColumnFlexFormPanel createDetailsTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Details"));
        int row = -1;

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().unitAvailabilitySpan()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxExactMatchUnits()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxPartialMatchUnits()), 5, true).build());

        content.setBR(++row, 0, 2);

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().feePayment()), 10, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().feeAmount()), 10, true).build());

        return content;
    }

}
