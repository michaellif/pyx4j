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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PeopleStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PeopleStep.class);

    private final HTML warningMessage = new HTML();

    private final String warningText = i18n
            .tr("Each tenant that is {0} or older is required to complete an additional application form. Access details will be emailed to them upon completion of this application.");

    public PeopleStep() {
        super(OnlineApplicationWizardStepMeta.People);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("People Living with You"));
        panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder(getWizard())));

        panel.setWidget(++row, 0, warningMessage);
        warningMessage.setStyleName(StyleName.WarningMessage.name());

        return panel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue().maturedOccupantsAreApplicants().isBooleanTrue()) {
            warningMessage.setHTML(SimpleMessageFormat.format(warningText, getValue().ageOfMajority()));
            warningMessage.setVisible(true);
        }
    }

    private class CoapplicantsFolder extends PortalBoxFolder<CoapplicantDTO> {

        private final ApplicationWizard wizard;

        public CoapplicantsFolder(ApplicationWizard applicationWizard) {
            super(CoapplicantDTO.class, i18n.tr("Occupant"));
            this.wizard = applicationWizard;
        }

        public Integer ageOfMajority() {
            return wizard.getValue().ageOfMajority().getValue();
        }

        public boolean enforceAgeOfMajority() {
            return wizard.getValue().enforceAgeOfMajority().isBooleanTrue();
        }

        public boolean maturedOccupantsAreApplicants() {
            return wizard.getValue().maturedOccupantsAreApplicants().isBooleanTrue();
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CoapplicantDTO) {
                return new CoapplicantForm();
            } else {
                return super.create(member);
            }
        }

        class CoapplicantForm extends CEntityForm<CoapplicantDTO> {

            public CoapplicantForm() {
                super(CoapplicantDTO.class);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

                int row = -1;
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().matured())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().dependent())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().firstName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().lastName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().birthDate()), 150).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().relationship())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email())).build());

                // tweaks:
                get(proto().dependent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        get(proto().birthDate()).setVisible(getValue().dependent().getValue());
                    }
                });
                get(proto().matured()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        get(proto().dependent()).setValue(!event.getValue());
                        get(proto().birthDate()).setVisible(getValue().dependent().getValue());
                    }
                });

                return mainPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().matured()).setVisible(maturedOccupantsAreApplicants());
                get(proto().dependent()).setVisible(!maturedOccupantsAreApplicants());
                get(proto().birthDate()).setVisible(getValue().dependent().getValue());
            }

            @Override
            public void addValidations() {
                super.addValidations();

                get(proto().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                    @Override
                    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                        if (value != null && getValue() != null) {
                            if (maturedOccupantsAreApplicants()) {
                                if (!getValue().matured().getValue()) {
                                    if (TimeUtils.isOlderThan(value, ageOfMajority())) {
                                        return new ValidationError(component, i18n.tr(
                                                "This person is matured. According to regulations age of majority is {0}.", ageOfMajority()));
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }
    }
}
