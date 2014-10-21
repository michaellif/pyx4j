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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.EntityContainerValidator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepStatus;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.prospect.ui.application.steps.AboutYouStep;
import com.propertyvista.portal.prospect.ui.application.steps.AdditionalInfoStep;
import com.propertyvista.portal.prospect.ui.application.steps.ConfirmationStep;
import com.propertyvista.portal.prospect.ui.application.steps.EmergencyContactsStep;
import com.propertyvista.portal.prospect.ui.application.steps.FinancialStep;
import com.propertyvista.portal.prospect.ui.application.steps.LeaseStep;
import com.propertyvista.portal.prospect.ui.application.steps.LegalStep;
import com.propertyvista.portal.prospect.ui.application.steps.OptionsStep;
import com.propertyvista.portal.prospect.ui.application.steps.PaymentStep;
import com.propertyvista.portal.prospect.ui.application.steps.PeopleStep;
import com.propertyvista.portal.prospect.ui.application.steps.UnitStep;
import com.propertyvista.portal.prospect.ui.application.steps.summary.SummaryStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class ApplicationWizard extends CPortalEntityWizard<OnlineApplicationDTO> {

    private static final I18n i18n = I18n.get(ApplicationWizard.class);

    private final HashMap<Class<? extends ApplicationWizardStep>, ApplicationWizardStep> steps = new HashMap<Class<? extends ApplicationWizardStep>, ApplicationWizardStep>();

    private ApplicationWizardPresenter presenter;

    public ApplicationWizard(ApplicationWizardViewImpl view, FeePayment feePaymentPolicy) {
        super(OnlineApplicationDTO.class, view, new ApplicationWizardDecorator());

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            if (SecurityController.check(PortalProspectBehavior.CanEditLeaseTerms)) {
                addStep(new UnitStep());
                addStep(new OptionsStep());
            } else {
                addStep(new LeaseStep());
            }
            addStep(new PeopleStep());
            addStep(new AboutYouStep());
            addStep(new AdditionalInfoStep());
            addStep(new FinancialStep());
            addStep(new EmergencyContactsStep());
            addStep(new LegalStep() {
                @Override
                public void onDownloadLeaseAgreementDraft() {
                    ApplicationWizard.this.presenter.downloadLeaseAgreementDraft();
                }
            });
            addStep(new SummaryStep());
            addStep(new PaymentStep());
            addStep(new ConfirmationStep());
        } else {
            addStep(new LeaseStep());
            addStep(new AboutYouStep());
            addStep(new AdditionalInfoStep());
            addStep(new FinancialStep());
            if (!SecurityController.check(PortalProspectBehavior.Guarantor)) {
                addStep(new EmergencyContactsStep());
            }
            addStep(new LegalStep() {
                @Override
                public void onDownloadLeaseAgreementDraft() {
                    ApplicationWizard.this.presenter.downloadLeaseAgreementDraft();
                }
            });
            addStep(new SummaryStep());
            if (!SecurityController.check(PortalProspectBehavior.Guarantor) && feePaymentPolicy == FeePayment.perApplicant) {
                addStep(new PaymentStep());
            }
            addStep(new ConfirmationStep());
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
        super.addStep(step);
        steps.put(step.getClass(), step);
    }

    public ApplicationWizardStep getStep(Class<? extends ApplicationWizardStep> stepClass) {
        return steps.get(stepClass);
    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);
        ((ApplicationWizardStep) selectedStep).onStepSelected();
    }

    @Override
    protected boolean allowLeavingCurrentStep() {
        int currentStepIndex = getSelectedIndex();
        WizardStep currentStep = getSelectedStep();

        if (currentStepIndex > -1) {
            currentStep.showErrors(true);
            ((ApplicationWizardStep) currentStep).onStepLeaving();
        }

        return true;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        for (ApplicationWizardStep step : steps.values()) {
            step.addValidations();
        }

        // remove standard validators:
        this.getComponentValidators().clear();
        // some inter-step validation:
        this.addComponentValidator(new AbstractComponentValidator<OnlineApplicationDTO>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null) {
                    OnlineApplicationDTO value = getComponent().getValue();

                    if (hasDuplicateEmails0(value)) {
                        return new BasicValidationError(getComponent(), i18n.tr("Applicant and Co-applicant have the same email address"));
                    }

                    if (hasDuplicateEmails1(value)) {
                        return new BasicValidationError(getComponent(), i18n.tr("Tenant and Guarantor have the same email address"));
                    }
                }

                EntityContainerValidator std = new EntityContainerValidator();
                std.setComponent(getComponent());
                return (BasicValidationError) std.isValid();
            }

            private boolean hasDuplicateEmails0(OnlineApplicationDTO value) {
                if (!value.applicantData().person().email().isNull()) {
                    for (CoapplicantDTO coap : value.coapplicants()) {
                        if (value.applicantData().person().email().getValue().equals(coap.email().getValue())) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private boolean hasDuplicateEmails1(OnlineApplicationDTO value) {
                boolean duplicate = false;

                if (!value.guarantors().isEmpty()) {
                    Collection<String> emails = new ArrayList<>();

                    if (!value.applicantData().person().email().isNull()) {
                        emails.add(value.applicantData().person().email().getValue());
                    }
                    for (CoapplicantDTO coap : value.coapplicants()) {
                        if (!coap.email().isNull()) {
                            emails.add(coap.email().getValue());
                        }
                    }

                    for (GuarantorDTO grnt : value.guarantors()) {
                        if (emails.contains(grnt.email().getValue())) {
                            duplicate = true;
                            break;
                        }
                    }
                }

                return duplicate;
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {

        IList<OnlineApplicationWizardStepStatus> statuses = getValue().stepsStatuses();
        HashMap<OnlineApplicationWizardStepMeta, Boolean> visitedStatusMap = new HashMap<>();

        for (OnlineApplicationWizardStepStatus status : statuses) {
            visitedStatusMap.put(status.step().getValue(), status.visited().getValue());
        }

        for (ApplicationWizardStep step : steps.values()) {
            step.onValueSet(populate);

            Boolean visited = visitedStatusMap.get(step.getOnlineApplicationWizardStepMeta());
            if (visited == null) {
                visited = false;
            }
            step.setStepVisited(visited);
            if (visited) {
                step.showErrors(true);
                ValidationResults validationResults = step.getValidationResults();
                if (validationResults.isValid()) {
                    step.setStepComplete(true);
                } else {
                    step.setStepWarning(validationResults.getValidationShortMessage());
                }
            }
        }

        super.onValueSet(populate);

    }

    @Override
    public void updateProgress(WizardStep currentStep, WizardStep previousStep) {
        super.updateProgress(currentStep, previousStep);

        ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(this, ApplicationWizardStateChangeEvent.ChangeType.stepChange));

        ((ApplicationWizardDecorator) getDecorator()).setCaption(currentStep.getStepTitle());
    }

    @Override
    public void generateMockData() {
        get(proto().applicantData().legalQuestions().suedForRent()).setMockValue(true);
        get(proto().applicantData().legalQuestions().suedForDamages()).setMockValue(true);
        get(proto().applicantData().legalQuestions().everEvicted()).setMockValue(true);
        get(proto().applicantData().legalQuestions().defaultedOnLease()).setMockValue(true);
        get(proto().applicantData().legalQuestions().convictedOfFelony()).setMockValue(true);
        get(proto().applicantData().legalQuestions().legalTroubles()).setMockValue(true);
        get(proto().applicantData().legalQuestions().filedBankruptcy()).setMockValue(true);
    }

    static class ApplicationWizardDecorator extends WizardDecorator<OnlineApplicationDTO> {

        public ApplicationWizardDecorator() {
            super(i18n.tr("Submit"));

            setCaption(i18n.tr("Lease Application"));

            getBtnCancel().setVisible(false);

            getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

            getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

            getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));

        }

    }
}
