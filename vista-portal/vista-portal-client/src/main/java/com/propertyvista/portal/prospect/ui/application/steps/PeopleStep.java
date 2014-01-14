/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;

public class PeopleStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PeopleStep.class);

    public PeopleStep() {
        super(OnlineApplicationWizardStepMeta.People);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("People Living with You"));
        panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder(getView())));

        return panel;
    }

}
