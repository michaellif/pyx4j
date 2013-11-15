/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import java.util.List;

import com.google.gwt.core.client.Scheduler;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardProgressIndicator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicationDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class ApplicationWizard extends CPortalEntityWizard<ApplicationDTO> {

    private static final I18n i18n = I18n.get(ApplicationWizard.class);

    private final WizardStep leaseStep;

    private final WizardStep unitStep;

    private final WizardStep optionsStep;

    private final WizardStep personalInfoAStep;

    private final WizardStep personalInfoBStep;

    private final WizardStep financialStep;

    private final WizardStep peopleStep;

    private final WizardStep contactsStep;

    private final WizardStep pmcCustomStep;

    private final WizardStep summaryStep;

    private final WizardStep paymentStep;

    private ApplicationWizardPresenter presenter;

    public ApplicationWizard(ApplicationWizardViewImpl view) {
        super(ApplicationDTO.class, view, i18n.tr("Profile Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);
        leaseStep = addStep(createLeaseStep());
        unitStep = addStep(createUnitStep());
        optionsStep = addStep(createOptionsStep());
        personalInfoAStep = addStep(createPersonalInfoAStep());
        personalInfoBStep = addStep(createPersonalInfoBStep());
        financialStep = addStep(createFinancialStep());
        peopleStep = addStep(createPeopleStep());
        contactsStep = addStep(createContactsStep());
        pmcCustomStep = addStep(createPmcCustomStep());
        summaryStep = addStep(createSummaryStep());
        paymentStep = addStep(createPaymentStep());

    }

    public void setPresenter(ApplicationWizardPresenter presenter) {
        this.presenter = presenter;
    }

    private BasicFlexFormPanel createLeaseStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Lease Information"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createUnitStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Selection"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createOptionsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Options"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createPersonalInfoAStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("About You"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createPersonalInfoBStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Additional Information"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createFinancialStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Financial"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createPeopleStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("People"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createContactsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Contacts"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createPmcCustomStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("PMC Custom"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createSummaryStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Summary"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createPaymentStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Payment"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    @Override
    protected IDecorator<?> createDecorator() {
        return new ApplicationWizardDecorator();
    }

    class ApplicationWizardDecorator extends WizardDecorator<ApplicationDTO> implements WizardProgressIndicator {

        private final ApplicationProgressPanel progressPanel;

        public ApplicationWizardDecorator() {
            super(i18n.tr("Submit"));

            setCaption(i18n.tr("Lease Application"));

            getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));

            getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));

            getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));

            progressPanel = new ApplicationProgressPanel();
            getHeaderPanel().clear();
            getHeaderPanel().add(progressPanel);
        }

        @Override
        public void updateProgress(List<WizardStep> steps) {
            progressPanel.updateStepButtons(steps);
        }
    }

}
