/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.wizard.IWizardView;
import com.pyx4j.site.client.ui.wizard.WizardForm;
import com.pyx4j.widgets.client.RadioGroup.Layout;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.dto.CreditCheckSetupDTO;

public class CreditCheckWizardForm extends WizardForm<CreditCheckSetupDTO> {

    private static final I18n i18n = I18n.get(CreditCheckWizardForm.class);

    public CreditCheckWizardForm(IWizardView<CreditCheckSetupDTO> view) {
        super(CreditCheckSetupDTO.class, view);
        addStep(createPricingStep(i18n.tr("Pricing")));
        addStep(createBusinessInfoStep(i18n.tr("Business Information")));
        addStep(createPersonalInfoStep(i18n.tr("Personal Information")));
        addStep(createConfirmationStep(i18n.tr("Confirmation")));
    }

    private FormFlexPanel createPricingStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Pricing"));
        main.setWidget(
                ++row,
                0,
                inject(proto().creditReportOption(), new CRadioGroupEnum<CreditCheckSetupDTO.CreditReportOption>(CreditCheckSetupDTO.CreditReportOption.class,
                        Layout.HORISONTAL)));
        return main;
    }

    private FormFlexPanel createBusinessInfoStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Business Information"));
        main.setWidget(++row, 0, inject(proto().companyType()));
        main.setWidget(++row, 0, inject(proto().businessAddress(), new AddressSimpleEditor()));
        main.setWidget(++row, 0, inject(proto().businessNumber()));
        main.setWidget(++row, 0, inject(proto().businessEstablishedDate()));
        return main;
    }

    private FormFlexPanel createPersonalInfoStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(++row, 0, 2, i18n.tr("Personal Information"));
        main.setWidget(++row, 0, inject(proto().name(), new NameEditor()));
        main.setWidget(++row, 0, inject(proto().personalAddress(), new AddressSimpleEditor()));
        main.setWidget(++row, 0, inject(proto().email()));
        main.setWidget(++row, 0, inject(proto().dateOfBirth()));
        main.setWidget(++row, 0, inject(proto().sin()));

        return main;
    }

    private FormFlexPanel createConfirmationStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Confirmation"));
        main.setWidget(++row, 0, inject(proto().creditCardInfo(), new CreditCardInfoEditor()));

        return main;
    }
}
