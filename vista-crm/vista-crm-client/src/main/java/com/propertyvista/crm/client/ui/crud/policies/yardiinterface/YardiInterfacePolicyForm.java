/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.yardiinterface;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.YardiInterfacePolicyDTO;

public class YardiInterfacePolicyForm extends PolicyDTOTabPanelBasedForm<YardiInterfacePolicyDTO> {

    private static final I18n i18n = I18n.get(YardiInterfacePolicyDTO.class);

    public YardiInterfacePolicyForm(IForm<YardiInterfacePolicyDTO> view) {
        super(YardiInterfacePolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        TwoColumnFlexFormPanel formPanel = new TwoColumnFlexFormPanel(i18n.tr("Integration"));
        int row = -1;
        formPanel.setH1(++row, 0, 2, i18n.tr("Yardi charge codes to ignore during import:"));
        formPanel.setWidget(++row, 0, 2, inject(proto().ignoreChargeCodes(), new YardiInterfacePolicyChargeCodeIgnoreFolder()));
        return Arrays.asList(//@formatter:off
                formPanel
        );//@formatter:on
    }

}
