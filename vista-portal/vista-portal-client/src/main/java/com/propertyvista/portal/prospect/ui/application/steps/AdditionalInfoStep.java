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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.validators.ClientBusinessRules;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateWithinPeriodValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.components.LegalQuestionFolder;
import com.propertyvista.portal.prospect.ui.application.components.PriorAddressEditor;
import com.propertyvista.portal.shared.ui.util.decorators.RadioButtonGroupDecoratorBuilder;

public class AdditionalInfoStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(AdditionalInfoStep.class);

    private FormPanel previousAddress;

    public AdditionalInfoStep() {
        super(OnlineApplicationWizardStepMeta.AdditionalInfo);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        formPanel.h3(i18n.tr("Current Address"));
        formPanel.append(Location.Left, proto().applicantData().currentAddress(), new PriorAddressEditor());

        previousAddress = new FormPanel(getWizard()) {
            @Override
            public void setVisible(boolean visible) {
                if (visible != get(proto().applicantData().previousAddress()).isVisible()) {
                    get(proto().applicantData().previousAddress()).clear();
                }
                get(proto().applicantData().previousAddress()).setVisible(visible);
                super.setVisible(visible);
            }
        };
        previousAddress.h3(i18n.tr("Previous Address"));
        previousAddress.append(Location.Left, proto().applicantData().previousAddress(), new PriorAddressEditor(true));
        formPanel.append(Location.Left, previousAddress);

        formPanel.h3(i18n.tr("General Questions"));
        formPanel.append(Location.Left, proto().applicantData().legalQuestions(), new LegalQuestionFolder());

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            formPanel.h3(i18n.tr("References"));
            formPanel.append(Location.Left, proto().referenceSource()).decorate().componentWidth(150);
        }

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        enablePreviousAddress();

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            get(proto().referenceSource()).setMandatory(getValue().referenceSourceIsMandatory().getValue(false));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> currentAF = ((CForm<PriorAddress>) get(proto().applicantData().currentAddress()));

        currentAF.get(currentAF.proto().moveInDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveOutDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
        new StartEndDateValidation(currentAF.get(currentAF.proto().moveInDate()), currentAF.get(currentAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        currentAF.get(currentAF.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> previousAF = ((CForm<PriorAddress>) get(proto().applicantData().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addComponentValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addComponentValidator(new PastDateValidator());
        new StartEndDateValidation(previousAF.get(previousAF.proto().moveInDate()), previousAF.get(previousAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), 1, 0,
                i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(ClientBusinessRules.needPreviousAddress(getValue().applicantData().currentAddress().moveInDate().getValue(), getValue()
                .yearsToForcingPreviousAddress().getValue()));
    }

    class LegalQuestionWidgetDecoratorBuilder extends RadioButtonGroupDecoratorBuilder {

        public LegalQuestionWidgetDecoratorBuilder() {
            super();
            labelWidth("100%");
        }

        @Override
        public FieldDecorator build() {
            FieldDecorator decorator = super.build();
            decorator.getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);
            return decorator;
        }
    }
}
