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
import com.propertyvista.portal.prospect.ui.application.steps.FinancialStep;
import com.propertyvista.portal.shared.ui.util.editors.PersonalAssetFolder;
import com.propertyvista.portal.shared.ui.util.editors.PersonalIncomeFolder;

public class FinancialSectionPanel extends AbstractSectionPanel {

    private static final I18n i18n = I18n.get(FinancialSectionPanel.class);

    public FinancialSectionPanel(int index, SummaryForm form, FinancialStep step) {
        super(index, OnlineApplicationWizardStepMeta.Financial.toString(), form, step);

        addCaption(i18n.tr("Income"));

        addField(proto().applicant().incomes(), new PersonalIncomeFolder(), false);

        addCaption(i18n.tr("Assets"));
        addField(proto().applicant().assets(), new PersonalAssetFolder(), false);

    }
}
