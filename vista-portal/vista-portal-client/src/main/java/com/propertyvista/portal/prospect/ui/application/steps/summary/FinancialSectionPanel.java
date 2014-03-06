/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps.summary;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.editors.PersonalAssetFolder;
import com.propertyvista.portal.prospect.ui.application.editors.PersonalIncomeFolder;
import com.propertyvista.portal.prospect.ui.application.steps.FinancialStep;

public class FinancialSectionPanel extends AbstractSectionPanel {

    private static final I18n i18n = I18n.get(FinancialSectionPanel.class);

    public FinancialSectionPanel(int index, SummaryForm form, FinancialStep step) {
        super(index, OnlineApplicationWizardStepMeta.Financial.toString(), form, step);

        addCaption(i18n.tr("Income"));
        PersonalIncomeFolder personalIncomeFolder = new PersonalIncomeFolder();
        personalIncomeFolder.setViewable(true);
        addField(proto().applicant().incomes(), personalIncomeFolder, false);

        addCaption(i18n.tr("Assets"));
        PersonalAssetFolder personalAssetFolder = new PersonalAssetFolder();
        personalIncomeFolder.setViewable(true);
        addField(proto().applicant().assets(), personalAssetFolder, false);

    }
}
