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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.dto.wizards.BusinessInformationForm;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.PersonalInformationForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.EquifaxSetupRequestDTO;

public class EquifaxApprovalForm extends OperationsEntityForm<EquifaxSetupRequestDTO> {

    private static final I18n i18n = I18n.get(EquifaxApprovalForm.class);

    public EquifaxApprovalForm(IForm<EquifaxSetupRequestDTO> view) {
        super(EquifaxSetupRequestDTO.class, view);
        this.setEditable(false);

        FormPanel formPanel = new FormPanel(this);
        formPanel.h1("Credit Pricing Option");
        formPanel.append(Location.Left, proto().reportType()).decorate();
        formPanel.append(Location.Left, proto().setupFee()).decorate();
        formPanel.append(Location.Left, proto().perApplicantFeee()).decorate();

        formPanel.h1("Business Information");
        formPanel.append(Location.Dual, proto().businessInformation(), new BusinessInformationForm());

        formPanel.h1("Personal Information");
        formPanel.append(Location.Dual, proto().personalInformation(), new PersonalInformationForm());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

}
