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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardProgressIndicator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.resident.ui.profile.EmergencyContactFolder;
import com.propertyvista.portal.resident.ui.profile.ProfilePageViewImpl;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ApplicationWizard extends CPortalEntityWizard<OnlineApplicationDTO> {

    private static final I18n i18n = I18n.get(ApplicationWizard.class);

    private WizardStep leaseStep;

    private WizardStep unitStep;

    private WizardStep optionsStep;

    private WizardStep personalInfoAStep;

    private WizardStep personalInfoBStep;

    private WizardStep financialStep;

    private WizardStep peopleStep;

    private WizardStep contactsStep;

    private WizardStep pmcCustomStep;

    private WizardStep summaryStep;

    private WizardStep paymentStep;

    private ApplicationWizardPresenter presenter;

    public ApplicationWizard(ApplicationWizardViewImpl view) {
        super(OnlineApplicationDTO.class, view, i18n.tr("Profile Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        if (SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            if (SecurityController.checkBehavior(PortalProspectBehavior.CanEditLeaseTerms)) {
                unitStep = addStep(createUnitStep());
                optionsStep = addStep(createOptionsStep());
            } else {
                leaseStep = addStep(createLeaseStep());
            }
            peopleStep = addStep(createPeopleStep());
            personalInfoAStep = addStep(createPersonalInfoAStep());
            personalInfoBStep = addStep(createPersonalInfoBStep());
            financialStep = addStep(createFinancialStep());
            contactsStep = addStep(createContactsStep());
            pmcCustomStep = addStep(createLegalStep());
            summaryStep = addStep(createSummaryStep());
            paymentStep = addStep(createPaymentStep());

        } else {
            leaseStep = addStep(createLeaseStep());
            personalInfoAStep = addStep(createPersonalInfoAStep());
            personalInfoBStep = addStep(createPersonalInfoBStep());
            financialStep = addStep(createFinancialStep());
            contactsStep = addStep(createContactsStep());
            pmcCustomStep = addStep(createLegalStep());
            summaryStep = addStep(createSummaryStep());
        }

    }

    public void setPresenter(ApplicationWizardPresenter presenter) {
        this.presenter = presenter;
        updateProgress();
    }

    private BasicFlexFormPanel createLeaseStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Lease Information"));
        int row = -1;

        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setH3(++row, 0, 1, i18n.tr("Unit"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().info().number(), new CLabel<String>())).build());
        panel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().unit().building().info().address(), new CEntityLabel<AddressStructured>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().floorplan(), new CEntityLabel<Floorplan>())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Term"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseFrom(), new CLabel<LogicalDate>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseTo(), new CLabel<LogicalDate>())).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leasePrice(), new CLabel<LogicalDate>())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Options"));

        panel.setWidget(++row, 0, inject(proto().options(), new ApplicationOptionsFolder((ApplicationWizardViewImpl) getView())));

        if (SecurityController.checkBehavior(PortalProspectBehavior.Applicant) && !SecurityController.checkBehavior(PortalProspectBehavior.CanEditLeaseTerms)) {
            panel.setH3(++row, 0, 1, i18n.tr("People"));
        }

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

    private BasicFlexFormPanel createPeopleStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("People"));
        int row = -1;

        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setH3(++row, 0, 1, i18n.tr("Tenants"));

        panel.setH3(++row, 0, 1, i18n.tr("Guarantors"));

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

    private BasicFlexFormPanel createContactsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Contacts"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());
        return panel;
    }

    private BasicFlexFormPanel createLegalStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Legal"));
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

    @Override
    protected void onValuePropagation(OnlineApplicationDTO value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
    }

    class ApplicationWizardDecorator extends WizardDecorator<OnlineApplicationDTO> implements WizardProgressIndicator {

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
        public void updateProgress() {
            progressPanel.updateStepButtons();
        }

        @Override
        public void setComponent(CEntityWizard<OnlineApplicationDTO> component) {
            super.setComponent(component);
            progressPanel.setWizard(component);
        }
    }

}
