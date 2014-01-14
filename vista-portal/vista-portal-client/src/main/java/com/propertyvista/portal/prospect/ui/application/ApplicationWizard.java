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

import java.util.HashMap;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.prospect.ui.application.steps.ContactsStep;
import com.propertyvista.portal.prospect.ui.application.steps.FinancialStep;
import com.propertyvista.portal.prospect.ui.application.steps.LeaseStep;
import com.propertyvista.portal.prospect.ui.application.steps.LegalStep;
import com.propertyvista.portal.prospect.ui.application.steps.OptionsStep;
import com.propertyvista.portal.prospect.ui.application.steps.PaymentStep;
import com.propertyvista.portal.prospect.ui.application.steps.PeopleStep;
import com.propertyvista.portal.prospect.ui.application.steps.PersonalInfoAStep;
import com.propertyvista.portal.prospect.ui.application.steps.PersonalInfoBStep;
import com.propertyvista.portal.prospect.ui.application.steps.SummaryStep;
import com.propertyvista.portal.prospect.ui.application.steps.UnitStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class ApplicationWizard extends CPortalEntityWizard<OnlineApplicationDTO> {

    private static final I18n i18n = I18n.get(ApplicationWizard.class);

    private final HashMap<Class<? extends ApplicationWizardStep>, ApplicationWizardStep> steps = new HashMap<Class<? extends ApplicationWizardStep>, ApplicationWizardStep>();

    private ApplicationWizardPresenter presenter;

    public ApplicationWizard(ApplicationWizardViewImpl view) {
        super(OnlineApplicationDTO.class, view, null, i18n.tr("Submit"), ThemeColor.contrast2);

        if (SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            if (SecurityController.checkBehavior(PortalProspectBehavior.CanEditLeaseTerms)) {
                addStep(new UnitStep());
                addStep(new OptionsStep());
            } else {
                addStep(new LeaseStep());
            }
            addStep(new PeopleStep());
            addStep(new PersonalInfoAStep());
            addStep(new PersonalInfoBStep());
            addStep(new FinancialStep());
            addStep(new ContactsStep());
            addStep(new LegalStep());
            addStep(new PaymentStep());
            addStep(new SummaryStep());
        } else {
            addStep(new LeaseStep());
            addStep(new PersonalInfoAStep());
            addStep(new PersonalInfoBStep());
            addStep(new FinancialStep());
            if (!SecurityController.checkBehavior(PortalProspectBehavior.Guarantor)) {
                addStep(new ContactsStep());
            }
            addStep(new LegalStep());
            addStep(new SummaryStep());
        }
    }

    public void setPresenter(ApplicationWizardPresenter presenter) {
        this.presenter = presenter;
    }

    public ApplicationWizardPresenter getPresenter() {
        return presenter;
    }

    public void addStep(ApplicationWizardStep step) {
        step.init(this);
        steps.put(step.getClass(), step);
        super.addStep(step);
    }

    public ApplicationWizardStep getStep(Class<? extends ApplicationWizardStep> stepClass) {
        return steps.get(stepClass);
    }

    @Override
    protected IDecorator<?> createDecorator() {
        return new ApplicationWizardDecorator();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        for (ApplicationWizardStep step : steps.values()) {
            step.addValidations();
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        //TODO Set WizardStep visited flag

        for (ApplicationWizardStep step : steps.values()) {
            step.onValueSet();
        }

    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);

        for (ApplicationWizardStep step : steps.values()) {
            step.onStepSelected(selectedStep);
        }
    }

    @Override
    public void updateProgress(WizardStep currentStep, WizardStep previousStep) {
        super.updateProgress(currentStep, previousStep);

        ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(this, ApplicationWizardStateChangeEvent.ChangeType.stepChange));
        ((ApplicationWizardDecorator) getDecorator()).setCaption(currentStep.getStepTitle());
    }

    @Override
    protected boolean allowLeavingCurrentStep() {
        return true;
    }

    class ApplicationWizardDecorator extends WizardDecorator<OnlineApplicationDTO> {

        public ApplicationWizardDecorator() {
            super(i18n.tr("Submit"));

            setCaption(i18n.tr("Lease Application"));

            getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

            getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

            getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

        }

    }
}
