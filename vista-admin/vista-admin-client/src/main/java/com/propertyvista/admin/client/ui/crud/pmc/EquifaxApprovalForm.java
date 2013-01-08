/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.EquifaxSetupRequestDTO;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.BusinessInformationForm;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.PersonalInformationForm;

public class EquifaxApprovalForm extends AdminEntityForm<EquifaxSetupRequestDTO> {

    public EquifaxApprovalForm(IFormView<EquifaxSetupRequestDTO> view) {
        super(EquifaxSetupRequestDTO.class, view);

        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;

        panel.setH1(++row, 0, 1, "Credit Pricing Option");
        panel.setWidget(+row, 0, inject(proto().creditPricingOption()));

        panel.setH1(++row, 0, 1, "Business Information");
        panel.setWidget(++row, 0, inject(proto().businessInformation(), new BusinessInformationForm()));

        panel.setH1(++row, 0, 1, "Personal Information");
        panel.setWidget(++row, 0, inject(proto().personalInformation(), new PersonalInformationForm()));

        selectTab(addTab(panel));
    }

}
