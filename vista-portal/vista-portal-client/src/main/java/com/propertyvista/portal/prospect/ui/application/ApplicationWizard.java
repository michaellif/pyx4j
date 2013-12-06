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

import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.IdUploaderFolder;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.misc.BusinessRules;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.resident.services.ResidentPictureUploadService;
import com.propertyvista.portal.shared.ui.AbstractPortalPanel;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.decorators.PortalWidgetDecorator;
import com.propertyvista.portal.shared.ui.util.editors.EmergencyContactFolder;
import com.propertyvista.portal.shared.ui.util.editors.PersonalAssetFolder;
import com.propertyvista.portal.shared.ui.util.editors.PersonalIncomeFolder;
import com.propertyvista.portal.shared.ui.util.editors.PriorAddressEditor;

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

    private final BasicFlexFormPanel previousAddress = new BasicFlexFormPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().applicant().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private IdUploaderFolder fileUpload;

    public ApplicationWizard(ApplicationWizardViewImpl view) {
        super(OnlineApplicationDTO.class, view, i18n.tr("Profile Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast2);

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

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            panel.setH3(++row, 0, 1, i18n.tr("People"));

            panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder((ApplicationWizardViewImpl) getView())));
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

        panel.setH3(++row, 0, 1, i18n.tr("People Living with You"));
        panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder((ApplicationWizardViewImpl) getView())));

        return panel;
    }

    private BasicFlexFormPanel createPersonalInfoAStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("About You"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        CImage<CustomerPicture> imageHolder = new CImage<CustomerPicture>(GWT.<ResidentPictureUploadService> create(ResidentPictureUploadService.class),
                new VistaFileURLBuilder<CustomerPicture>(CustomerPicture.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().picture(), imageHolder)).customLabel("").build());

        panel.setH3(++row, 0, 1, i18n.tr("Personal Information"));
        panel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().applicant().person().name(), new CEntityLabel<Name>()), 200).customLabel(i18n.tr("Full Name"))
                        .build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().sex()), 100).build());

        panel.setH3(++row, 0, 1, i18n.tr("Contact Information"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().homePhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().mobilePhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().workPhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().email()), 230).build());

        panel.setH3(++row, 0, 1, i18n.tr("Secure Information"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().birthDate()), 150).build());

        panel.setH3(++row, 0, 1, i18n.tr("Identification Documents"));
        panel.setWidget(++row, 0, 2, inject(proto().applicant().documents(), fileUpload = new IdUploaderFolder()));

        return panel;
    }

    private BasicFlexFormPanel createPersonalInfoBStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Additional Info"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setH3(++row, 0, 1, i18n.tr("Current Address"));
        panel.setWidget(++row, 0, inject(proto().applicant().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH3(0, 0, 1, i18n.tr("Previous Address"));
        previousAddress.setWidget(1, 0, inject(proto().applicant().previousAddress(), new PriorAddressEditor()));
        panel.setWidget(++row, 0, previousAddress);

        panel.setH3(++row, 0, 1, i18n.tr("General Questions"));
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().suedForRent())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().suedForDamages())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().everEvicted())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().defaultedOnLease())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().convictedOfFelony())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().legalTroubles())).build());
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().filedBankruptcy())).build());

        panel.setH3(++row, 0, 1, i18n.tr("How Did You Hear About Us?"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().refSource()), 180).build());

        return panel;
    }

    private BasicFlexFormPanel createFinancialStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Financial"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setH3(++row, 0, 1, i18n.tr("Income"));
        panel.setWidget(++row, 0, inject(proto().applicant().incomes(), new PersonalIncomeFolder()));

        panel.setH3(++row, 0, 1, i18n.tr("Assets"));
        panel.setWidget(++row, 0, inject(proto().applicant().assets(), new PersonalAssetFolder()));

        panel.setH3(++row, 0, 1, i18n.tr("Guarantors"));
        panel.setWidget(++row, 0, inject(proto().guarantors(), new GuarantorsFolder((ApplicationWizardViewImpl) getView())));

        return panel;
    }

    private BasicFlexFormPanel createContactsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Contacts"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, inject(proto().applicant().emergencyContacts(), new EmergencyContactFolder()));

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
    public void addValidations() {
        super.addValidations();

        // ------------------------------------------------------------------------------------------------
        // PersonalInfoBStep:

        CEntityForm<PriorAddress> currentAddressForm = ((CEntityForm<PriorAddress>) get(proto().applicant().currentAddress()));
        CEntityForm<PriorAddress> previousAddressForm = ((CEntityForm<PriorAddress>) get(proto().applicant().previousAddress()));

        CComponent<LogicalDate> c1 = currentAddressForm.get(currentAddressForm.proto().moveInDate());
        CComponent<LogicalDate> c2 = currentAddressForm.get(currentAddressForm.proto().moveOutDate());
        CComponent<LogicalDate> p1 = previousAddressForm.get(previousAddressForm.proto().moveInDate());
        CComponent<LogicalDate> p2 = previousAddressForm.get(previousAddressForm.proto().moveOutDate());

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        p1.addValueValidator(new PastDateValidator());
        c1.addValueValidator(new PastDateIncludeTodayValidator());
        c2.addValueValidator(new FutureDateIncludeTodayValidator());

        new StartEndDateValidation(c1, c2);
        new StartEndDateValidation(p1, p2);
        StartEndDateWithinMonth(c1, p2, i18n.tr("Current Move In Date Should Be Within 30 Days Of Previous Move Out Date"));
        StartEndDateWithinMonth(p2, c1, i18n.tr("Current Move In Date Should Be Within 30 Days Of Previous Move Out Date"));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        previousAddressForm.get(previousAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(currentAddressForm.get(currentAddressForm.proto().moveInDate())));

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(currentAddressForm.get(currentAddressForm.proto().moveInDate())));

        currentAddressForm.get(currentAddressForm.proto().moveInDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveOutDate())));

        previousAddressForm.get(previousAddressForm.proto().moveOutDate()).addValueChangeHandler(
                new RevalidationTrigger<LogicalDate>(previousAddressForm.get(previousAddressForm.proto().moveInDate())));

        // ------------------------------------------------------------------------------------------------
        // createFinancialStep:

        this.addValueValidator(new EditableValueValidator<OnlineApplicationDTO>() {
            @Override
            public ValidationError isValid(CComponent<OnlineApplicationDTO> component, OnlineApplicationDTO value) {
                return (value.applicant().assets().size() > 0) || (value.applicant().incomes().size() > 0) ? null : new ValidationError(component, i18n
                        .tr("At least one source of income or one asset is required"));
            }
        });

        // ------------------------------------------------------------------------------------------------
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            fileUpload.setParentEntity(getValue());
        }

        enablePreviousAddress();
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().applicant().currentAddress().moveInDate().getValue()));
    }

    private void StartEndDateWithinMonth(final CComponent<LogicalDate> value1, final CComponent<LogicalDate> value2, final String message) {
        value1.addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value == null || getValue() == null || getValue().isEmpty() || value2.getValue() == null) {
                    return null;
                }

                Date date = value2.getValue();
                long limit1 = date.getTime() + 2678400000L; //limits date1 to be within a month of date2
                long limit2 = date.getTime() - 2678400000L;
                return (date == null || (value.getTime() > limit2 && value.getTime() < limit1)) ? null : new ValidationError(component, message);
            }
        });
    }

    @Override
    public void updateProgress() {
        super.updateProgress();
        ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(this, ApplicationWizardStateChangeEvent.ChangeType.stepChange));
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

    class LegalQuestionWidgetDecoratorBuilder extends PortalWidgetDecorator.Builder {

        public LegalQuestionWidgetDecoratorBuilder(CComponent<?> component) {
            super(component);
            labelWidth(300 + "px");
            contentWidth(70 + "px");
            componentWidth(70 + "px");
            labelPosition(AbstractPortalPanel.getWidgetLabelPosition());
            useLabelSemicolon(false);
            labelAlignment(Alignment.left);
        }

    }
}
