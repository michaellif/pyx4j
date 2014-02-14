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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateWithinPeriodValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.misc.BusinessRules;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.decorators.RadioButtonGroupDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.PriorAddressEditor;

public class AdditionalInfoStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(AdditionalInfoStep.class);

    private final BasicFlexFormPanel previousAddress = new BasicFlexFormPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().applicant().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    public AdditionalInfoStep() {
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
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().suedForDamages())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().everEvicted())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().defaultedOnLease())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().convictedOfFelony())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().legalTroubles())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);
        panel.setBR(++row, 0, 1);
        panel.setWidget(++row, 0, new LegalQuestionWidgetDecoratorBuilder(inject(proto().applicant().legalQuestions().filedBankruptcy())).build());
        panel.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.LEFT);

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

        // ------------------------------------------------------------------------------------------------
        CEntityForm<PriorAddress> currentAF = ((CEntityForm<PriorAddress>) get(proto().applicant().currentAddress()));

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
        CEntityForm<PriorAddress> previousAF = ((CEntityForm<PriorAddress>) get(proto().applicant().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addComponentValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addComponentValidator(new PastDateValidator());
        new StartEndDateValidation(previousAF.get(previousAF.proto().moveInDate()), previousAF.get(previousAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), 1, 0,
                i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().applicant().currentAddress().moveInDate().getValue()));
    }

    class LegalQuestionWidgetDecoratorBuilder extends RadioButtonGroupDecoratorBuilder {

        public LegalQuestionWidgetDecoratorBuilder(CComponent<?> component) {
            super(component);
            labelWidth("100%");
        }
    }
}
