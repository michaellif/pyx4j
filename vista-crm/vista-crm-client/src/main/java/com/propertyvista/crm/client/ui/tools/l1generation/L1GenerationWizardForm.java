/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.prime.wizard.WizardForm;

import com.propertyvista.domain.legal.l1.L1FormFieldsData;

public class L1GenerationWizardForm extends WizardForm<L1FormFieldsData> {

    private static final I18n i18n = I18n.get(L1GenerationWizardForm.class);

    public L1GenerationWizardForm(IWizard<? extends IEntity> view) {
        super(L1FormFieldsData.class, view);
        addStep(createPartZeroStep());
    }

    private TwoColumnFlexFormPanel createPartZeroStep() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Step Zero"));
        return panel;
    }

}
