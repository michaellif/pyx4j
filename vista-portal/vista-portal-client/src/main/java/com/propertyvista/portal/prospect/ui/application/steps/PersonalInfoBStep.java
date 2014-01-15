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

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.misc.BusinessRules;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.AbstractPortalPanel;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecorator;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.PriorAddressEditor;

public class PersonalInfoBStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PersonalInfoBStep.class);

    private final BasicFlexFormPanel previousAddress = new BasicFlexFormPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().applicant().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    public PersonalInfoBStep() {
        super(OnlineApplicationWizardStepMeta.AdditionalInfo);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

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

        // TODO currently removed, then rethink: 
        if (false && !SecurityController.checkBehavior(PortalProspectBehavior.Guarantor)) {
            panel.setH3(++row, 0, 1, i18n.tr("How Did You Hear About Us?"));
            panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().refSource()), 180).build());
        }

        return panel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        enablePreviousAddress();
    }

    @Override
    public void addValidations() {
        super.addValidations();

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

    class LegalQuestionWidgetDecoratorBuilder extends FormWidgetDecorator.Builder {

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
