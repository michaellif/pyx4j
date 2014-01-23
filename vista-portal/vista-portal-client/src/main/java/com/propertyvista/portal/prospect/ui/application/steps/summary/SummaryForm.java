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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.prospect.ui.application.steps.AboutYouStep;
import com.propertyvista.portal.prospect.ui.application.steps.AdditionalInfoStep;
import com.propertyvista.portal.prospect.ui.application.steps.ContactsStep;
import com.propertyvista.portal.prospect.ui.application.steps.FinancialStep;
import com.propertyvista.portal.prospect.ui.application.steps.LeaseStep;
import com.propertyvista.portal.prospect.ui.application.steps.LegalStep;
import com.propertyvista.portal.prospect.ui.application.steps.OptionsStep;
import com.propertyvista.portal.prospect.ui.application.steps.PeopleStep;
import com.propertyvista.portal.prospect.ui.application.steps.UnitStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;

public class SummaryForm extends CEntityForm<OnlineApplicationDTO> {

    private final ApplicationWizard applicationWizard;

    public SummaryForm(ApplicationWizard applicationWizard) {
        super(OnlineApplicationDTO.class);
        this.applicationWizard = applicationWizard;
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();

        int row = -1;

        for (WizardStep step : applicationWizard.getAllSteps()) {
            AbstractSectionPanel panel = null;
            if (step instanceof UnitStep) {
                panel = new UnitSectionPanel(this, (UnitStep) step);
            } else if (step instanceof OptionsStep) {
                panel = new OptionsSectionPanel(this, (OptionsStep) step);
            } else if (step instanceof LeaseStep) {
                panel = new LeaseSectionPanel(this, (LeaseStep) step);
            } else if (step instanceof PeopleStep) {
                panel = new PeopleSectionPanel(this, (PeopleStep) step);
            } else if (step instanceof AboutYouStep) {
                panel = new AboutYouSectionPanel(this, (AboutYouStep) step);
            } else if (step instanceof AdditionalInfoStep) {
                panel = new AdditionalInfoSectionPanel(this, (AdditionalInfoStep) step);
            } else if (step instanceof FinancialStep) {
                panel = new FinancialSectionPanel(this, (FinancialStep) step);
            } else if (step instanceof ContactsStep) {
                panel = new ContactsSectionPanel(this, (ContactsStep) step);
            } else if (step instanceof LegalStep) {
                panel = new LegalSectionPanel(this, (LegalStep) step);
            }

            if (panel != null) {
                contentPanel.setWidget(++row, 0, panel);
            }
        }

        return contentPanel;
    }

}
