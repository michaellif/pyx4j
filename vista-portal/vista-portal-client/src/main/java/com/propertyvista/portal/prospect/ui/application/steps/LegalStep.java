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

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;

public abstract class LegalStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(LegalStep.class);

    public LegalStep() {
        super(OnlineApplicationWizardStepMeta.Legal);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().legalTerms(), new LegalTermsFolder()));
        panel.setWidget(++row, 0, new Button(i18n.tr("Download Lease Agreement Draft"), new Command() {
            @Override
            public void execute() {
                onDownloadLeaseAgreementDraft();
            }
        }));
        return panel;
    }

    public abstract void onDownloadLeaseAgreementDraft();

}
