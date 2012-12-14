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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.wizard.WizardForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.dto.CreditCheckWizardDTO;

public class CreditCheckWizardForm extends WizardForm<CreditCheckWizardDTO> {

    private static final I18n i18n = I18n.get(CreditCheckWizardForm.class);

    public CreditCheckWizardForm() {
        super(CreditCheckWizardDTO.class, VistaTheme.defaultTabHeight);
    }

    @Override
    public void createSteps() {

        Tab tab = null;

        tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

    }

    private FormFlexPanel createGeneralTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Summary"));

        return main;
    }

}
